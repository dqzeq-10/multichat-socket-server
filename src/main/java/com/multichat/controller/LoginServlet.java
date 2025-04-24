/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.multichat.controller;

import com.multichat.model.User;
import com.multichat.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


/**
 *
 * @author ZEQ
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    
    //init 1 service de xu li
    private UserService userService;
    
    @Override
    public void init(){
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            HttpSession session = request.getSession(false); //truyen false de check, if exist return it, else return null
            if(session != null && session.getAttribute("user") != null){
                response.sendRedirect(request.getContextPath() + "/chat"); 
                return;
            }
            
            request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username  = request.getParameter("username");
        String password = request.getParameter("password");
        
        try{
            User user = userService.autheticateUser(username, password);
            if (user != null){
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getUserID());
                session.setAttribute("username", user.getUsername());
                
                response.sendRedirect(request.getContextPath() + "/chat");
            }else{
                request.setAttribute("errorMessage", "Tài khoản hoặc mật khẩu không đúng!");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        }catch(Exception e){
            request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
      
    }

 
}
