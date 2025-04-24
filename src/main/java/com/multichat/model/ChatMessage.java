package com.multichat.model;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Đại diện cho một tin nhắn chat trong hệ thống
 * Lớp này đại diện cho một tin nhắn chat trong database
 */
public class ChatMessage {
    // Trường theo database
    private long messageID;
    private int roomID;
    private int userID;
    private String messageType;
    private String content;
    private LocalDateTime sentAt;
    private boolean isDeleted;
    
    // Trường tạm thời để tương thích với code cũ
    private String username;
    private String room;
    
    // Enum cho các loại tin nhắn
    public enum MessageType {
        CHAT,    // Tin nhắn chat thông thường
        JOIN,    // Thông báo người dùng tham gia phòng
        LEAVE,   // Thông báo người dùng rời phòng
        SYSTEM   // Tin nhắn hệ thống
    }
    
    // Các hàm khởi tạo
    public ChatMessage() {
        this.sentAt = LocalDateTime.now();
        this.isDeleted = false;
    }
    
    public ChatMessage(String username, String content, String room, MessageType type) {
        this();
        this.username = username;
        this.content = content;
        this.room = room;
        this.messageType = type.toString();
    }
    
    // Constructor bổ sung với ID
    public ChatMessage(int userID, int roomID, String content, MessageType type) {
        this();
        this.userID = userID;
        this.roomID = roomID;
        this.content = content;
        this.messageType = type.toString();
    }
    
    // Getter và Setter cho các trường database
    public long getMessageID() {
        return messageID;
    }
    
    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }
    
    public int getRoomID() {
        return roomID;
    }
    
    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    // Getter và Setter cho các trường tạm thời (tương thích với code cũ)
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getRoom() {
        return room;
    }
    
    public void setRoom(String room) {
        this.room = room;
    }
    
    // Các phương thức để tương thích với code cũ
    public Date getTimestamp() {
        // Chuyển đổi LocalDateTime sang Date cho tương thích ngược
        return sentAt != null ? java.sql.Timestamp.valueOf(sentAt) : null;
    }
    
    public void setTimestamp(Date timestamp) {
        // Chuyển đổi Date sang LocalDateTime
        if (timestamp != null) {
            this.sentAt = new java.sql.Timestamp(timestamp.getTime()).toLocalDateTime();
        }
    }
    
    public MessageType getType() {
        if (messageType == null) return MessageType.CHAT;
        try {
            return MessageType.valueOf(messageType);
        } catch (Exception e) {
            return MessageType.CHAT; // Mặc định
        }
    }
    
    public void setType(MessageType type) {
        this.messageType = type.toString();
    }
    
    @Override
    public String toString() {
        return "ChatMessage [messageID=" + messageID + ", roomID=" + roomID + ", userID=" + userID +
               ", messageType=" + messageType + ", content=" + content + ", sentAt=" + sentAt + 
               ", isDeleted=" + isDeleted + ", username=" + username + ", room=" + room + "]";
    }
}