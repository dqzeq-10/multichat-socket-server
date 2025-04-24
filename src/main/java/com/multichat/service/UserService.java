/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.multichat.service;

import com.multichat.model.User;
import com.multichat.util.DatabaseConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ZEQ
 */
public class UserService {

    /**
     * Xác thực người dùng với thông tin đăng nhập, sử dụng stored procedure sp_GetUserForLogin
     */
    public User autheticateUser(String username, String password) {
        String sql = "{call sp_GetUserForLogin(?)}";
        try (Connection conn = DatabaseConnection.getConnectionDB();
             CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setString(1, username);
            
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    // Kiểm tra mật khẩu
                    String storedPassword = rs.getString("Password");
                    if (!storedPassword.equals(password)) {
                        return null;
                    }
                    
                    // Tạo đối tượng User từ dữ liệu truy vấn
                    User user = new User();
                    user.setUserID(rs.getInt("UserID"));
                    user.setUsername(rs.getString("Username"));
                    
                    // Cập nhật thời gian đăng nhập
                    updateLastLogin(user.getUserID());
                    
                    // Lấy thông tin chi tiết của user
                    return getUserDetails(user.getUserID());
                }
            }
        } catch (SQLException e) {
            System.out.println("Login failed: " + e);
        }
        return null;
    }

    /**
     * Lấy thông tin chi tiết của người dùng theo ID
     */
    private User getUserDetails(int userID) {
        String sql = "SELECT * FROM Users WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("UserID"));
                    user.setUsername(rs.getString("Username"));
                    user.setAvatarURL(rs.getString("AvatarURL"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setIsActive(rs.getBoolean("IsActive"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting user details: " + e);
        }
        return null;
    }

    /**
     * Đăng ký người dùng mới sử dụng stored procedure sp_CreateUser
     */
    public boolean register(User newUser) {
        if (checkExistUsername(newUser.getUsername())) {
            return false;
        }

        String sql = "{call sp_CreateUser(?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getConnectionDB();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, newUser.getUsername());
            cs.setString(2, newUser.getPassword());
            cs.setString(3, newUser.getEmail());
            cs.setString(4, newUser.getFullName());

            int rowsAffected = cs.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Register failed: " + e);
            return false;
        }
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public Boolean checkExistUsername(String username) {
        String sql = "select username from users where username = ?";
        try (Connection conn = DatabaseConnection.getConnectionDB(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Check failed: " + e);
        }
        return true;
    }

    /**
     * Cập nhật thời gian đăng nhập cuối cùng cho người dùng bằng Stored Procedure
     */
    public void updateLastLogin(int userId) {
        String sql = "{call sp_UpdateLastLogin(?)}";

        try (Connection conn = DatabaseConnection.getConnectionDB();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, userId);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating last login: " + e);
        }
    }

} //endclass
