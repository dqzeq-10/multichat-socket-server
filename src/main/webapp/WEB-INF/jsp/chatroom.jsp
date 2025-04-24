<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Multichat - ${room}</title>
            <link rel="stylesheet"
                href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
            <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap"
                rel="stylesheet">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
        </head>

        <body class="chat-page">
            <div class="container">
                <header>
                    <h1><i class="fas fa-comments"></i> Multichat - ${room}</h1>
                </header>

                <main>
                    <div class="chat-container">
                        <div class="room-sidebar">
                            <h2>Các phòng chat</h2>
                            <ul>
                                <c:forEach items="${chatRooms}" var="chatRoom">
                                    <c:choose>
                                        <c:when test="${room eq chatRoom.roomName}">
                                            <li class="active disabled">
                                                <i class="fas fa-comment-dots"></i> ${chatRoom.roomName}
                                            </li>
                                        </c:when>
                                        <c:otherwise>
                                            <li onclick="switchRoom('${chatRoom.roomName}')">
                                                <i class="fas fa-comment-dots"></i> ${chatRoom.roomName}
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <!-- Giữ lại các phòng mặc định nếu không có phòng nào từ database -->

                                <c:if test="${empty chatRooms}">
                                    <c:choose>
                                        <c:when test="${room eq 'general'}">
                                            <li class="active disabled">
                                                <i class="fas fa-globe"></i> Chung DF
                                            </li>
                                        </c:when>
                                        <c:otherwise>
                                            <li onclick="switchRoom('general')">
                                                <i class="fas fa-globe"></i> Chung DF
                                            </li>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:choose>
                                        <c:when test="${room eq 'technology'}">
                                            <li class="active disabled">
                                                <i class="fas fa-laptop-code"></i> Công nghệ DF
                                            </li>
                                        </c:when>
                                        <c:otherwise>
                                            <li onclick="switchRoom('technology')">
                                                <i class="fas fa-laptop-code"></i> Công nghệ DF
                                            </li>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:choose>
                                        <c:when test="${room eq 'random'}">
                                            <li class="active disabled">
                                                <i class="fas fa-random"></i> Ngẫu nhiên DF
                                            </li>
                                        </c:when>
                                        <c:otherwise>
                                            <li onclick="switchRoom('random')">
                                                <i class="fas fa-random"></i> Ngẫu nhiên DF
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </ul>
                            <div class="create-room">
                                <h3>Tạo phòng mới</h3>
                                <form id="create-room-form" action="chat" method="post">
                                    <input type="hidden" name="action" value="createRoom">
                                    <div class="form-group">
                                        <input type="text" name="roomName" placeholder="Tên phòng..." required>
                                    </div>
                                    <div class="form-group">
                                        <input type="text" name="roomDescription" placeholder="Mô tả phòng...">
                                    </div>
                                    <button type="submit" class="btn btn-create">
                                        <i class="fas fa-plus-circle"></i> Tạo phòng
                                    </button>
                                </form>
                            </div>
                            <div class="user-info">
                                <p><i class="fas fa-user-circle"></i> Tên hiện tại: <strong>${username}</strong>
                                </p>
                                <button onclick="clearCurrentRoomHistory()" class="btn btn-secondary"
                                    style="margin-top: 10px; background: rgba(255, 255, 255, 0.2);">
                                    <i class="fas fa-trash"></i> Xóa lịch sử
                                </button>
                                <a href="chat?action=logout" class="btn">
                                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                                </a>
                            </div>
                        </div>

                        <div class="chat-main">
                            <div class="chat-header">
                                <i class="fas fa-comment-dots"></i>
                                <h2>Phòng chat ${room}</h2>
                            </div>

                            <div class="chat-messages">
                                <!-- Messages will appear here dynamically -->
                            </div>

                            <form id="chat-form" class="chat-form">
                                <input type="text" id="message-input" placeholder="Nhập tin nhắn của bạn..."
                                    autocomplete="off">
                                <button type="submit"><i class="fas fa-paper-plane"></i></button>
                            </form>
                        </div>
                    </div>
                </main>


            </div>

            <!-- Hidden data elements for JavaScript -->
            <div id="current-user" data-username="${username}" style="display: none;"></div>
            <div id="current-room" data-room="${room}" style="display: none;"></div>

            <script src="${pageContext.request.contextPath}/resources/js/main.js"></script>
        </body>

        </html>