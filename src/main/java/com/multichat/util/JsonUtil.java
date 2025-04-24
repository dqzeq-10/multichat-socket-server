package com.multichat.util;

import org.json.JSONObject;
import com.multichat.model.ChatMessage;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lớp tiện ích cho các thao tác với JSON
 */
public class JsonUtil {
    
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Chuyển đối tượng ChatMessage thành chuỗi JSON
     */
    public static String toJson(ChatMessage message) {
        JSONObject json = new JSONObject();
        
        // Thông tin cơ bản của tin nhắn
        json.put("messageID", message.getMessageID());
        json.put("username", message.getUsername());
        json.put("content", message.getContent());
        json.put("room", message.getRoom());
        json.put("type", message.getType().toString());
        
        // Định dạng thời gian gửi
        if (message.getSentAt() != null) {
            json.put("timestamp", message.getSentAt().format(dateFormatter));
        } else {
            json.put("timestamp", LocalDateTime.now().format(dateFormatter));
        }
        
        // Thông tin bổ sung (nếu có)
        if (message.getUserID() > 0) {
            json.put("userID", message.getUserID());
        }
        
        return json.toString();
    }
    
    /**
     * Phân tích chuỗi JSON thành đối tượng ChatMessage
     */
    public static ChatMessage fromJson(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        
        ChatMessage message = new ChatMessage();
        
        // Trích xuất thông tin cơ bản
        if (json.has("username")) {
            message.setUsername(json.getString("username"));
        }
        
        if (json.has("content")) {
            message.setContent(json.getString("content"));
        }
        
        if (json.has("room")) {
            message.setRoom(json.getString("room"));
        }
        
        // Thiết lập loại tin nhắn
        if (json.has("type")) {
            String type = json.getString("type");
            switch (type) {
                case "JOIN":
                    message.setType(ChatMessage.MessageType.JOIN);
                    break;
                case "LEAVE":
                    message.setType(ChatMessage.MessageType.LEAVE);
                    break;
                case "SYSTEM":
                    message.setType(ChatMessage.MessageType.SYSTEM);
                    break;
                default:
                    message.setType(ChatMessage.MessageType.CHAT);
            }
        } else {
            message.setType(ChatMessage.MessageType.CHAT);
        }
        
        // Trích xuất ID nếu có
        if (json.has("messageID")) {
            message.setMessageID(json.getLong("messageID"));
        }
        
        if (json.has("userID")) {
            message.setUserID(json.getInt("userID"));
        }
        
        return message;
    }
}