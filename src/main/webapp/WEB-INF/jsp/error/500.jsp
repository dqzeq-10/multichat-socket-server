<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>500 - Server Error</title>
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

            .error-details {
                margin-top: 20px;
                text-align: left;
                background: #f8f8f8;
                padding: 15px;
                border-radius: 4px;
                max-height: 200px;
                overflow: auto;
                font-family: monospace;
                font-size: 14px;
                color: #555;
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
                    <div class="error-code">500</div>
                    <div class="error-message">Internal Server Error</div>
                    <p>Sorry, something went wrong on our end. Please try again later.</p>
                    <a href="${pageContext.request.contextPath}/" class="home-link">Back to Home</a>

                    <% if (exception !=null) { %>
                        <div class="error-details">
                            <p><strong>Error Details:</strong></p>
                            <p>
                                <%= exception.getMessage() %>
                            </p>
                        </div>
                        <% } %>
                </div>
            </main>

            <footer>
                <p>&copy; 2025 Multichat Application</p>
            </footer>
        </div>
    </body>

    </html>