package com.multichat.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.multichat.model.ChatMessage;
import com.multichat.model.ChatMessage.MessageType;
import com.multichat.model.ChatRoom;
import com.multichat.model.RoomMember;
import com.multichat.model.User;
import com.multichat.util.DatabaseConnection;

/**
 * Lớp Service xử lý logic nghiệp vụ và lưu trữ tin nhắn chat
 * Lớp này xử lý logic nghiệp vụ cho việc quản lý tin nhắn và phòng chat
 */
public class ChatService {
    
    private static final Logger logger = LogManager.getLogger(ChatService.class);
    
    // Biến static lưu instance duy nhất (singleton)
    private static ChatService instance;
    
    // Map lưu trữ người dùng đang hoạt động theo phòng (phòng -> tập hợp username)
    private Map<String, Map<String, String>> activeUsers;
    
    // Số lượng tin nhắn tối đa cho mỗi lần lấy
    private static final int MAX_MESSAGES = 50;
    
    // Hàm khởi tạo private cho singleton
    public ChatService() {
        activeUsers = new ConcurrentHashMap<>();
        
        // Khởi tạo danh sách người dùng hoạt động cho các phòng (vẫn giữ để tương thích với code cũ)
        List<ChatRoom> rooms = getAllRoomsFromDB();
        for (ChatRoom room : rooms) {
            activeUsers.put(room.getRoomName(), new ConcurrentHashMap<>());
        }
        
        logger.info("ChatService đã khởi tạo với các phòng từ database");
    }
    
    /**
     * Lấy instance duy nhất của lớp (singleton)
     */
    public static synchronized ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }
        return instance;
    }
    
    /**
     * Thêm một tin nhắn vào phòng, lưu vào database
     */
    public void addMessage(ChatMessage message) {
        String roomName = message.getRoom();
        
        try {
            // Lấy roomID từ tên phòng
            int roomID = getRoomIDByName(roomName);
            
            if (roomID == -1 && message.getType() == MessageType.CHAT) {
                logger.error("Không thể lưu tin nhắn vì không tìm thấy phòng: " + roomName);
                return;
            }
            
            // Nếu phòng không tồn tại và người dùng đang JOIN, tạo phòng mới
            if (roomID == -1 && message.getType() == MessageType.JOIN) {
                roomID = createNewRoom(roomName, "Phòng chat được tạo tự động", getUserIDByUsername(message.getUsername()));
                logger.info("Đã tạo phòng mới với ID: " + roomID);
            }
            
            // Lưu tin nhắn vào database
            saveMessageToDB(roomID, message);
            
            // Cập nhật người dùng hoạt động dựa vào loại tin nhắn
            if (message.getType() == MessageType.JOIN) {
                addUserToRoom(message.getUsername(), roomName);
            } else if (message.getType() == MessageType.LEAVE) {
                removeUserFromRoom(message.getUsername(), roomName);
            }
            
            logger.debug("Đã thêm tin nhắn vào phòng '" + roomName + "': " + message);
        } catch (SQLException e) {
            logger.error("Lỗi khi lưu tin nhắn: " + e.getMessage());
        }
    }
    
    /**
     * Lấy tất cả tin nhắn của một phòng từ database
     */
    public List<ChatMessage> getMessages(String roomName) {
        try {
            int roomID = getRoomIDByName(roomName);
            if (roomID == -1) {
                return Collections.emptyList();
            }
            return getChatHistoryFromDB(roomID, MAX_MESSAGES);
        } catch (SQLException e) {
            logger.error("Lỗi khi lấy tin nhắn từ database: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Lưu tin nhắn vào database bằng stored procedure
     */
    private void saveMessageToDB(int roomID, ChatMessage message) throws SQLException {
        String sql = "{call sp_SaveChatMessage(?, ?, ?, ?)}";
        
        try (Connection conn = DatabaseConnection.getConnectionDB();
             CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setInt(1, roomID);
            
            // Nếu là tin nhắn hệ thống, UserID có thể null
            if (message.getType() == MessageType.SYSTEM) {
                cs.setNull(2, java.sql.Types.INTEGER);
            } else {
                int userID = getUserIDByUsername(message.getUsername());
                cs.setInt(2, userID);
            }
            
            cs.setString(3, message.getType().toString());
            cs.setString(4, message.getContent());
            
            cs.executeUpdate();
        }
    }
    
    /**
     * Lấy lịch sử chat từ database bằng stored procedure
     */
    private List<ChatMessage> getChatHistoryFromDB(int roomID, int limit) throws SQLException {
        String sql = "{call sp_GetChatHistory(?, ?)}";
        List<ChatMessage> messages = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnectionDB();
             CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setInt(1, roomID);
            cs.setInt(2, limit);
            
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    ChatMessage message = new ChatMessage();
                    message.setMessageID(rs.getLong("MessageID"));
                    message.setUserID(rs.getInt("UserID"));
                    message.setUsername(rs.getString("Username"));
                    message.setContent(rs.getString("Content"));
                    
                    // Chuyển đổi từ string sang enum MessageType
                    String typeStr = rs.getString("MessageType");
                    message.setType(MessageType.valueOf(typeStr));
                    
                    message.setSentAt(rs.getTimestamp("SentAt").toLocalDateTime());
                    
                    // Lấy tên phòng từ roomID
                    message.setRoom(getRoomNameByID(roomID));
                    
                    messages.add(message);
                }
            }
        }
        
        // Đảo ngược danh sách để có thứ tự từ cũ đến mới
        Collections.reverse(messages);
        
        return messages;
    }
    
    /**
     * Lấy roomID từ tên phòng
     */
    private int getRoomIDByName(String roomName) throws SQLException {
        String sql = "SELECT RoomID FROM ChatRooms WHERE RoomName = ?";
        
        try (Connection conn = DatabaseConnection.getConnectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, roomName);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RoomID");
                }
            }
        }
        
        return -1; // Không tìm thấy phòng
    }
    
    /**
     * Lấy tên phòng từ roomID
     */
    private String getRoomNameByID(int roomID) throws SQLException {
        String sql = "SELECT RoomName FROM ChatRooms WHERE RoomID = ?";
        
        try (Connection conn = DatabaseConnection.getConnectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, roomID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("RoomName");
                }
            }
        }
        
        return null; // Không tìm thấy phòng
    }
    
    /**
     * Lấy userID từ username
     */
    public int getUserIDByUsername(String username) throws SQLException {
        String sql = "SELECT UserID FROM Users WHERE Username = ?";
        
        try (Connection conn = DatabaseConnection.getConnectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
                }
            }
        }
        
        return -1; // Không tìm thấy người dùng
    }
    
    /**
     * Tạo phòng chat mới, trả về roomID nếu tạo thành công
     */
    public int createNewRoom(String roomName, String description, int createdBy) throws SQLException {
        String sql = "INSERT INTO ChatRooms (RoomName, Description, CreatedBy) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnectionDB();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, roomName);
            ps.setString(2, description);
            ps.setInt(3, createdBy);
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int roomID = rs.getInt(1);
                    
                    // Thêm người tạo vào phòng với quyền admin
                    addMemberToRoom(roomID, createdBy, true);
                    
                    // Tạo lưu trữ cho người dùng hoạt động
                    activeUsers.put(roomName, new ConcurrentHashMap<>());
                    
                    return roomID;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Thêm thành viên vào phòng
     */
    public void addMemberToRoom(int roomID, int userID, boolean isAdmin) throws SQLException {
        String sql = "INSERT INTO RoomMembers (RoomID, UserID, IsAdmin) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, roomID);
            ps.setInt(2, userID);
            ps.setBoolean(3, isAdmin);
            
            ps.executeUpdate();
        }
    }
    
    /**
     * Kiểm tra người dùng có phải là thành viên của phòng hay không
     */
    public boolean isMemberOfRoom(int roomID, int userID) throws SQLException {
        String sql = "SELECT 1 FROM RoomMembers WHERE RoomID = ? AND UserID = ?";
        
        try (Connection conn = DatabaseConnection.getConnectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, roomID);
            ps.setInt(2, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Lấy tất cả các phòng từ database
     */
    private List<ChatRoom> getAllRoomsFromDB() {
        List<ChatRoom> rooms = new ArrayList<>();
        String sql = "SELECT r.RoomID, r.RoomName, r.Description, r.CreatedAt, r.CreatedBy, r.IsPrivate, COUNT(rm.UserID) AS MemberCount FROM ChatRooms r LEFT JOIN RoomMembers rm ON r.RoomID = rm.RoomID GROUP BY r.RoomID, r.RoomName, r.Description, r.CreatedAt, r.CreatedBy, r.IsPrivate";
        
        try (Connection conn = DatabaseConnection.getConnectionDB();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ChatRoom room = new ChatRoom();
                room.setRoomID(rs.getInt("RoomID"));
                room.setRoomName(rs.getString("RoomName"));
                room.setDescription(rs.getString("Description"));
                room.setCreatedAt(rs.getDate("CreatedAt"));
                room.setCreatedBy(rs.getInt("CreatedBy"));
                room.setIsPrivate(rs.getBoolean("IsPrivate"));
                room.setMemberCount(rs.getInt("MemberCount"));
                
                rooms.add(room);
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi lấy danh sách phòng: " + e.getMessage());
        }
        
        return rooms;
    }
    
    /**
     * Thêm người dùng vào phòng (hiện tại chỉ dùng cho theo dõi người dùng hoạt động)
     */
    public void addUserToRoom(String username, String room) {
        if (!activeUsers.containsKey(room)) {
            activeUsers.put(room, new ConcurrentHashMap<>());
        }
        
        activeUsers.get(room).put(username, username);
        logger.info("Người dùng '" + username + "' đã tham gia phòng: " + room);
        
        // Có thể thêm logic để cập nhật bảng RoomMembers nếu cần
        try {
            int roomID = getRoomIDByName(room);
            int userID = getUserIDByUsername(username);
            
            if (roomID != -1 && userID != -1) {
                if (!isMemberOfRoom(roomID, userID)) {
                    addMemberToRoom(roomID, userID, false);
                }
                
                // Cập nhật LastSeenAt
                updateLastSeenAt(roomID, userID);
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi thêm người dùng vào phòng trong database: " + e.getMessage());
        }
    }
    
    /**
     * Cập nhật thời gian xuất hiện cuối cùng của người dùng trong phòng
     */
    private void updateLastSeenAt(int roomID, int userID) throws SQLException {
        String sql = "UPDATE RoomMembers SET LastSeenAt = GETDATE() WHERE RoomID = ? AND UserID = ?";
        
        try (Connection conn = DatabaseConnection.getConnectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, roomID);
            ps.setInt(2, userID);
            
            ps.executeUpdate();
        }
    }
    
    /**
     * Xóa người dùng khỏi phòng
     */
    public void removeUserFromRoom(String username, String room) {
        if (activeUsers.containsKey(room)) {
            activeUsers.get(room).remove(username);
            logger.info("Người dùng '" + username + "' đã rời phòng: " + room);
        }
        
        // Không xóa người dùng khỏi RoomMembers, chỉ cập nhật LastSeenAt
        try {
            int roomID = getRoomIDByName(room);
            int userID = getUserIDByUsername(username);
            
            if (roomID != -1 && userID != -1) {
                updateLastSeenAt(roomID, userID);
            }
        } catch (SQLException e) {
            logger.error("Lỗi khi cập nhật LastSeenAt: " + e.getMessage());
        }
    }
    
    /**
     * Lấy danh sách người dùng đang hoạt động trong phòng
     */
    public List<String> getActiveUsers(String room) {
        if (!activeUsers.containsKey(room)) {
            return Collections.emptyList();
        }
        
        return new ArrayList<>(activeUsers.get(room).values());
    }
    
    /**
     * Kiểm tra phòng đã tồn tại chưa
     */
    public boolean roomExists(String room) {
        try {
            return getRoomIDByName(room) != -1;
        } catch (SQLException e) {
            logger.error("Lỗi khi kiểm tra phòng tồn tại: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy danh sách tất cả các phòng hiện có
     */
    public List<String> getAllRooms() {
        List<String> roomNames = new ArrayList<>();
        List<ChatRoom> rooms = getAllRoomsFromDB();
        
        for (ChatRoom room : rooms) {
            roomNames.add(room.getRoomName());
        }
        
        return roomNames;
    }
    
    /**
     * Lấy danh sách đầy đủ các phòng chat với thông tin chi tiết
     */
    public List<ChatRoom> getAllChatRooms() {
        return getAllRoomsFromDB();
    }
}