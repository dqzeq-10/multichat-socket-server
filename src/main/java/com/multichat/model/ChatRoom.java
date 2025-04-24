/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.multichat.model;

import java.util.Date;

/**
 *
 * @author ZEQ
 */
public class ChatRoom {
    private int RoomID;
    private String RoomName;
    private String Description;
    private Date CreatedAt; // Chuyển lại thành Date
    private int CreatedBy;
    private Boolean IsPrivate;
    private int MemberCount;

    public ChatRoom() {
    }
    
    public ChatRoom(int RoomID, String RoomName, String Description, int CreatedBy, Boolean IsPrivate) {
        this.RoomID = RoomID;
        this.RoomName = RoomName;
        this.Description = Description;
        this.CreatedBy = CreatedBy;
        this.IsPrivate = IsPrivate;
    }

    public int getRoomID() {
        return RoomID;
    }

    public void setRoomID(int RoomID) {
        this.RoomID = RoomID;
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String RoomName) {
        this.RoomName = RoomName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public Date getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Date CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    public int getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(int CreatedBy) {
        this.CreatedBy = CreatedBy;
    }

    public Boolean getIsPrivate() {
        return IsPrivate;
    }

    public void setIsPrivate(Boolean IsPrivate) {
        this.IsPrivate = IsPrivate;
    }
    
    public int getMemberCount() {
        return MemberCount;
    }

    public void setMemberCount(int MemberCount) {
        this.MemberCount = MemberCount;
    }
}
