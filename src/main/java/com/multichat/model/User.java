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
public class User {
    private int UserID;
    private String Username;
    private String Password;
    private String Email;
    private String FullName;
    private String AvatarURL;
    private Date CreatedAt;
    private Date LastLoginAt;
    private Boolean IsActive;

    public User() {
    }

    
    
    public User(String Username, String Password, String Email, String FullName) {
        this.Username = Username;
        this.Password = Password;
        this.Email = Email;
        this.FullName = FullName;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int UserID) {
        this.UserID = UserID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String FullName) {
        this.FullName = FullName;
    }

    public String getAvatarURL() {
        return AvatarURL;
    }

    public void setAvatarURL(String AvatarURL) {
        this.AvatarURL = AvatarURL;
    }

    public Date getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Date CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    public Date getLastLoginAt() {
        return LastLoginAt;
    }

    public void setLastLoginAt(Date LastLoginAt) {
        this.LastLoginAt = LastLoginAt;
    }

    public Boolean getIsActive() {
        return IsActive;
    }

    public void setIsActive(Boolean IsActive) {
        this.IsActive = IsActive;
    }
    
    
}
