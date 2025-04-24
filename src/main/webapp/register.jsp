<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Multichat - Đăng ký</title>
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
                            <p>Tạo tài khoản để kết nối và chia sẻ với bạn bè. Trải nghiệm giao tiếp thời gian thực với giao diện hiện đại.</p>

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
                        <h2>Đăng ký tài khoản</h2>
                        <c:if test="${not empty errorMessage}">
                            <div class="error-message"
                                style="color: #e74c3c; margin-bottom: 15px; text-align: center; background: rgba(231, 76, 60, 0.1); padding: 10px; border-radius: 5px;">
                                <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                            </div>
                        </c:if>
                        <form action="${pageContext.request.contextPath}/register" method="POST">
                            <div class="form-group">
                                <label for="username">Tên đăng nhập</label>
                                <input type="text" id="username" name="username" placeholder="Nhập tên đăng nhập"
                                    required>
                            </div>
                            <div class="form-group">
                                <label for="fullname">Họ và tên</label>
                                <input type="text" id="fullname" name="fullname" placeholder="Nhập họ và tên"
                                    required>
                            </div>
                            <div class="form-group">
                                <label for="email">Email</label>
                                <input type="email" id="email" name="email" placeholder="Nhập địa chỉ email"
                                    required>
                            </div>
                            <div class="form-group">
                                <label for="password">Mật khẩu</label>
                                <input type="password" id="password" name="password" placeholder="Nhập mật khẩu"
                                    required>
                            </div>
                            <button type="submit" class="btn">
                                <i class="fas fa-user-plus"></i> Đăng ký
                            </button>
                        </form>
                        <br>
                        <div class="login-link">
                            <p>Đã có tài khoản? <a href="${pageContext.request.contextPath}/login.jsp">Đăng nhập ngay</a></p>
                        </div>
                    </div>
                </div>
            </div>
            <script src="${pageContext.request.contextPath}/resources/js/main.js"></script>
        </body>

        </html>