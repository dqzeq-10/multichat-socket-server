/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.multichat.util;

import com.multichat.model.User;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

/**
 *
 * @author ZEQ
 */
public class DatabaseConnection {

    private static final String dbName = "MultiChatDB";
    private static final String user = "sa";
    private static final String password = "123456";
    private static final String url = "jdbc:sqlserver://localhost:1433;databaseName=" + dbName + ";encrypt=true;trustServerCertificate=true;";

// Static block to load driver when class is loaded
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: SQL Server JDBC driver not found!");
            e.printStackTrace();
        }
    }
    
    
    public static Connection getConnectionDB() {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connect success!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Connect failed");
            e.printStackTrace();
            return null;
        }
    }

    
//test connect
//    public static void main(String[] args) throws SQLException {
//        Connection connection = getConnectionDB();
//        String sql = "select * from users where username = ? and password = ?";
//        PreparedStatement ps = connection.prepareStatement(sql);
//        ps.setString(1, "admin");
//        ps.setString(2, "123");
//        
//        try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    System.out.println(rs.getString("fullname"));
//                }
//            }
//        
//      
//    }
    
    
}
