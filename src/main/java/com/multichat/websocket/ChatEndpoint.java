package com.multichat.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.websocket.EncodeException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.multichat.model.ChatMessage;
import com.multichat.model.ChatMessage.MessageType;
import com.multichat.service.ChatService;
import com.multichat.util.JsonUtil;

/**
 * WebSocket endpoint cho chức năng chat
 * Endpoint xử lý kết nối WebSocket thời gian thực
 */
@ServerEndpoint("/chat-socket/{room}")
public class ChatEndpoint {
    
    private static final Logger logger = LogManager.getLogger(ChatEndpoint.class);
    
    // Map lưu trữ các phiên (session) đang hoạt động theo từng phòng
    private static final Map<String, Set<Session>> roomSessions = new HashMap<>();
    
    // Service xử lý logic nghiệp vụ chat
    private static final ChatService chatService = ChatService.getInstance();
    
    /**
     * Được gọi khi một kết nối mới được thiết lập
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("room") String room) {
        logger.info("New WebSocket connection opened: " + session.getId() + " for room: " + room);
        
        // Thêm session vào phòng tương ứng
        roomSessions.computeIfAbsent(room, k -> new CopyOnWriteArraySet<>()).add(session);
        
        // Gửi lịch sử tin nhắn đến client mới kết nối
        sendChatHistory(session, room);
    }
    
    /**
     * Gửi lịch sử tin nhắn từ database đến client mới kết nối
     */
    private void sendChatHistory(Session session, String room) {
        // Lấy lịch sử tin nhắn từ database thông qua service
        List<ChatMessage> messageHistory = chatService.getMessages(room);
        
        // Gửi từng tin nhắn đến client
        for (ChatMessage message : messageHistory) {
            try {
                String jsonMessage = JsonUtil.toJson(message);
                session.getBasicRemote().sendText(jsonMessage);
            } catch (IOException e) {
                logger.error("Error sending chat history to session " + session.getId(), e);
            }
        }
    }
    
    /**
     * Được gọi khi nhận được một tin nhắn từ client
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("room") String room) {
        logger.debug("Received message: " + message);
        
        try {
            // Phân tích nội dung tin nhắn nhận được
            JSONObject jsonMessage = new JSONObject(message);
            String username = jsonMessage.getString("username");
            String content = jsonMessage.getString("content");
            String type = jsonMessage.getString("type");
            
            // Tạo đối tượng tin nhắn
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUsername(username);
            chatMessage.setContent(content);
            chatMessage.setRoom(room);
            
            // Thiết lập loại tin nhắn
            switch (type) {
                case "JOIN":
                    chatMessage.setType(MessageType.JOIN);
                    break;
                case "LEAVE":
                    chatMessage.setType(MessageType.LEAVE);
                    break;
                case "SYSTEM":
                    chatMessage.setType(MessageType.SYSTEM);
                    break;
                default:
                    chatMessage.setType(MessageType.CHAT);
            }
            
            // Lưu tin nhắn vào database thông qua service
            chatService.addMessage(chatMessage);
            
            // Gửi tin nhắn đến tất cả các session trong phòng
            broadcast(chatMessage, room);
            
        } catch (Exception e) {
            logger.error("Error processing message", e);
        }
    }
    
    /**
     * Được gọi khi kết nối bị đóng
     */
    @OnClose
    public void onClose(Session session, @PathParam("room") String room) {
        logger.info("WebSocket connection closed: " + session.getId());
        
        // Xóa session khỏi phòng tương ứng
        if (roomSessions.containsKey(room)) {
            roomSessions.get(room).remove(session);
            
            // Nếu phòng không còn ai, xóa phòng khỏi danh sách
            if (roomSessions.get(room).isEmpty()) {
                roomSessions.remove(room);
            }
        }
    }
    
    /**
     * Được gọi khi xảy ra lỗi
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket error for session " + session.getId(), throwable);
    }
    
    /**
     * Gửi tin nhắn đến tất cả các session trong một phòng
     */
    private void broadcast(ChatMessage message, String room) {
        if (roomSessions.containsKey(room)) {
            String jsonMessage = JsonUtil.toJson(message);
            
            for (Session session : roomSessions.get(room)) {
                try {
                    session.getBasicRemote().sendText(jsonMessage);
                } catch (IOException e) {
                    logger.error("Error broadcasting message to session " + session.getId(), e);
                }
            }
        }
    }
}