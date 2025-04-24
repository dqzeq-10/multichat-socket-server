<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Multichat - Kết nối cùng nhau</title>
            <link rel="stylesheet"
                href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
            <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap"
                rel="stylesheet">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
        </head>

        <body class="login-page">
            <div class="container">
                <div class="card">
                    <div class="card-image">
                        <div class="card-image-content">
                            <h1>Multichat</h1>
                            <p>Kết nối, chia sẻ và trò chuyện với bạn bè từ khắp nơi trên thế giới. Trải nghiệm giao
                                tiếp thời gian thực với giao diện hiện đại.</p>

                            <div class="features">
                                <div class="feature">
                                    <i class="fas fa-users"></i>
                                    <p>Nhiều phòng chat</p>
                                </div>
                                <div class="feature">
                                    <i class="fas fa-bolt"></i>
                                    <p>Tốc độ nhanh</p>
                                </div>
                                <div class="feature">
                                    <i class="fas fa-lock"></i>
                                    <p>An toàn & bảo mật</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="login-container">
                        <h2>Tham gia ngay</h2>
                        <form action="${pageContext.request.contextPath}/chat" method="POST">
                            <div class="form-group">
                                <label for="username">Tên hiển thị</label>
                                <input type="text" id="username" name="username" placeholder="Nhập tên của bạn"
                                    required>
                            </div>
                            <div class="form-group">
                                <label for="room">Chọn phòng chat</label>
                                <select id="room" name="room">
                                    <option value="general">Chung</option>
                                    <option value="technology">Công nghệ</option>
                                    <option value="random">Ngẫu nhiên</option>
                                </select>

                            </div>
                            <button type="submit" class="btn">
                                <i class="fas fa-sign-in-alt"></i> Bắt đầu trò chuyện
                            </button>
                        </form>
                    </div>
                </div>


            </div>
            <!-- ${pageContext.request.contextPath} trả về đúng đường dẫn gốc của ứng dụng, giúp bạn tạo URL động, tránh lỗi khi deploy ở các context khác nhau. -->
            <script src="${pageContext.request.contextPath}/resources/js/main.js"></script>
        </body>

        </html>