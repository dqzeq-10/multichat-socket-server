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
public class RoomMember {
    private int RoomID;
    private int UserID;
    private Date JoinedAt;
    private Date LastSeenAt;
    private Boolean IsAdmin;

    public RoomMember() {
    }

    
    
    public RoomMember(int RoomID, int UserID) {
        this.RoomID = RoomID;
        this.UserID = UserID;
    }

    public int getRoomID() {
        return RoomID;
    }

    public void setRoomID(int RoomID) {
        this.RoomID = RoomID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int UserID) {
        this.UserID = UserID;
    }

    public Date getJoinedAt() {
        return JoinedAt;
    }

    public void setJoinedAt(Date JoinedAt) {
        this.JoinedAt = JoinedAt;
    }

    public Date getLastSeenAt() {
        return LastSeenAt;
    }

    public void setLastSeenAt(Date LastSeenAt) {
        this.LastSeenAt = LastSeenAt;
    }

    public Boolean getIsAdmin() {
        return IsAdmin;
    }

    public void setIsAdmin(Boolean IsAdmin) {
        this.IsAdmin = IsAdmin;
    }

    
}
