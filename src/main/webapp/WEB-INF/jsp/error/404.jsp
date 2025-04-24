<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>404 - Page Not Found</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
        <style>
            .error-container {
                text-align: center;
                margin: 50px auto;
                max-width: 600px;
                padding: 30px;
                background: #fff;
                border-radius: 5px;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            }

            .error-code {
                font-size: 72px;
                margin-bottom: 20px;
                color: #e74c3c;
            }

            .error-message {
                font-size: 24px;
                margin-bottom: 30px;
            }

            .home-link {
                display: inline-block;
                background: #4267B2;
                color: #fff;
                text-decoration: none;
                padding: 10px 20px;
                border-radius: 4px;
            }

            .home-link:hover {
                background: #3b5998;
            }
        </style>
    </head>

    <body>
        <div class="container">
            <header>
                <h1>Multichat Application</h1>
            </header>

            <main>
                <div class="error-container">
                    <div class="error-code">404</div>
                    <div class="error-message">Page Not Found</div>
                    <p>Sorry, the page you are looking for does not exist or has been moved.</p>
                    <a href="${pageContext.request.contextPath}/" class="home-link">Back to Home</a>
                </div>
            </main>

            <footer>
                <p>&copy; 2025 Multichat Application</p>
            </footer>
        </div>
    </body>

    </html>