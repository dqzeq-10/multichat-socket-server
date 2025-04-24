package com.multichat.controller;

import com.multichat.model.ChatRoom;
import com.multichat.service.ChatService;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet điều khiển xử lý các yêu cầu liên quan đến chat Servlet xử lý yêu cầu
 * HTTP
 */
@WebServlet("/chat")
public class ChatServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(ChatServlet.class);

    private ChatService chatService;

    @Override
    public void init() {
        chatService = ChatService.getInstance();
    }

    /**
     * Xử lý các yêu cầu GET - hiển thị phòng chat
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String room = request.getParameter("room");

        // Nếu người dùng chưa đăng nhập, chuyển hướng về trang đăng nhập
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Nếu chưa chọn phòng, sử dụng phòng mặc định
        if (room == null || room.trim().isEmpty()) {
            room = "general";
        }
        // Lấy danh sách phòng chat từ database
        ChatService chatService = ChatService.getInstance();
        List<ChatRoom> chatRooms = chatService.getAllChatRooms();

        // Thêm vào request attribute để JSP có thể truy cập
        request.setAttribute("chatRooms", chatRooms);

        // Thiết lập thuộc tính cho JSP
        request.setAttribute("username", username);
        request.setAttribute("room", room);

        // Chuyển tiếp đến trang JSP phòng chat
        request.getRequestDispatcher("/WEB-INF/jsp/chatroom.jsp").forward(request, response);
    }

    /**
     * Xử lý các yêu cầu POST - xử lý đăng nhập
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("createRoom".equals(action)) {
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("username");
            if (username == null || username.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            String roomName = request.getParameter("roomName");
            String roomDescription = request.getParameter("roomDescription");

            if (roomName == null || roomName.trim().isEmpty()) {
                request.setAttribute("error", "Tên phòng không được để trống");
                doGet(request, response); // Gọi lại doGet để hiển thị trang với thông báo lỗi
                return;
            }

            if (roomName.length() > 50) {
                request.setAttribute("error", "Tên phòng không được quá 50 kí tự ");
                doGet(request, response); // Gọi lại doGet để hiển thị trang với thông báo lỗi
                return;
            }

            try {
                int roomid = chatService.createNewRoom(roomName, roomDescription, chatService.getUserIDByUsername(username));
                if (roomid > 0) {
                    response.sendRedirect(request.getContextPath() + "/chat?room=" + roomName);
                    return;
                } else {
                    request.setAttribute("error", "Không thể tạo phòng mới");
                    doGet(request, response);// Gọi lại doGet để hiển thị trang với thông báo lỗi
                    return;
                }
            } catch (SQLException ex) {
                logger.error("Lỗi khi tạo phòng mới: " + ex.getMessage(), ex);
                request.setAttribute("error", "Đã xảy ra lỗi: " + ex.getMessage());
                doGet(request, response);// Gọi lại doGet để hiển thị trang với thông báo lỗi
                return;
            }
        } else {

            String username = request.getParameter("username");
            String room = request.getParameter("room");

            // Kiểm tra dữ liệu đầu vào
            if (username == null || username.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            // Nếu chưa chọn phòng, sử dụng phòng mặc định
            if (room == null || room.trim().isEmpty()) {
                room = "general";
            }

            // Lưu tên người dùng vào session
            HttpSession session = request.getSession();
            session.setAttribute("username", username);

            logger.info("Người dùng '" + username + "' đã tham gia phòng chat: " + room);

            // Chuyển hướng đến phòng chat
            response.sendRedirect(request.getContextPath() + "/chat?room=" + room);

        }
    }
}
