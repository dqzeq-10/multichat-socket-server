# Hướng dẫn Cài đặt và Chạy Project Multichat (JSP/Servlet/WebSocket)

Project này là một ứng dụng chat thời gian thực sử dụng Java Servlets, JSP, WebSocket và SQL Server.

## 1. Yêu cầu phần mềm

Trước khi bắt đầu, vui lòng đảm bảo đã cài đặt các phần mềm sau:

1.  **JDK (Java Development Kit):** Phiên bản 8 trở lên.
2.  **Maven:** Công cụ quản lý build và dependency cho Java.
3.  **SQL Server:** Hệ quản trị cơ sở dữ liệu.
    *   **SQL Server Management Studio (SSMS):**
4.  **Apache Tomcat:** Servlet container để chạy ứng dụng web. Phiên bản 9 hoặc 10 là phù hợp.
5.  **IDE (Tùy chọn nhưng khuyến nghị):** NetBeans, IntelliJ IDEA hoặc Eclipse

## 2. Cài đặt Cơ sở dữ liệu (SQL Server)

Đây là bước **quan trọng**. Ứng dụng cần cơ sở dữ liệu để lưu trữ thông tin người dùng, phòng chat và tin nhắn.

1.  **Khởi chạy SQL Server:** Vui lòng đảm bảo dịch vụ SQL Server đang chạy.
2.  **Tạo Database:**
    *   Mở SSMS và kết nối tới SQL Server instance.
    *   Cần có file script `multichat_SQLQuery.sql` chứa các lệnh để:
        *   Tạo database (ví dụ: `CREATE DATABASE MultiChatDB;`)
        *   Tạo các bảng cần thiết (`Users`, `ChatRooms`, `Messages`, `RoomMembers`).
        *   Tạo các Stored Procedures (`sp_GetUserForLogin`, `sp_SaveChatMessage`, `sp_CreateUser`, `sp_GetChatHistory`, v.v.).
    *   Thực thi file script `.sql` này trong SSMS để tạo cấu trúc database và các stored procedures cần thiết.
3.  **Cấu hình Kết nối Database trong Project:**
    *   Mở file: [`src/main/java/com/multichat/util/DatabaseConnection.java`](src/main/java/com/multichat/util/DatabaseConnection.java)
    *   Tìm đến phương thức `getConnectionDB()`.
    *   **Quan trọng:** Vui lòng chỉnh sửa chuỗi kết nối (`connectionUrl`), `username`, và `password` để khớp với cấu hình SQL Server.
        *   Ví dụ chuỗi kết nối: `jdbc:sqlserver://localhost:1433;databaseName=MultiChatDB;encrypt=true;trustServerCertificate=true;` (Điều chỉnh `localhost`, `1433`, `MultiChatDB` nếu cần).
        *   Đảm bảo `username` và `password` là tài khoản SQL Server có quyền truy cập vào `MultiChatDB`.

## 3. Cài đặt Project

1.  **Lấy mã nguồn:** Giải nén file project.
2.  **Mở Project bằng IDE (Khuyến nghị):**
    *   Mở IDE (NetBeans, IntelliJ, Eclipse).
    *   Chọn "Open Project" hoặc "Import Project".
    *   Chọn tùy chọn để mở/import project dưới dạng "Maven Project".
    *   Trỏ đến thư mục chứa file `pom.xml` của project. IDE sẽ tự động nhận diện và tải các dependencies được định nghĩa trong `pom.xml`.

## 4. Build Project bằng Maven

Maven sẽ biên dịch code và đóng gói ứng dụng thành file `.war`.

1.  **Sử dụng IDE:**
    *   Hầu hết các IDE đều có tích hợp Maven. Tìm đến cửa sổ Maven (thường ở bên phải hoặc trong menu View/Tools).
    *   Trong mục "Lifecycle", chạy lần lượt các goal: `clean` rồi đến `install` (hoặc `package`).
2.  **Sử dụng Dòng lệnh (Terminal/Command Prompt):**
    *   Mở terminal hoặc command prompt.
    *   Di chuyển đến thư mục gốc của project (thư mục chứa file `pom.xml`).
    *   Chạy lệnh: `mvn clean install`
3.  **Kết quả:** Sau khi build thành công, sẽ thấy một file `.war` (ví dụ: `multichat.war`) được tạo ra trong thư mục `target`.

## 5. Deploy ứng dụng lên Tomcat

1.  **Khởi động Tomcat:** Đảm bảo server Tomcat đang chạy.
2.  **Deploy file .war:**
    *   Sao chép file `.war` (ví dụ: `target/multichat.war`) vào thư mục `webapps` trong thư mục cài đặt Tomcat.
    *   Tomcat sẽ tự động giải nén và deploy ứng dụng. Tên của thư mục được giải nén (context path) thường sẽ giống tên file `.war` (không có đuôi `.war`, ví dụ: `multichat`).

## 6. Chạy ứng dụng

1.  Mở trình duyệt web.
2.  Truy cập vào địa chỉ URL của ứng dụng. Cấu trúc URL thường là:
    `http://localhost:{port}/{context-path}/`
    *   `{port}`: Cổng mà Tomcat đang chạy (mặc định thường là `8080`).
    *   `{context-path}`: Tên thư mục ứng dụng trong `webapps` (ví dụ: `multichat`).
    *   **URL ví dụ:** `http://localhost:8080/multichat/`
3.  Ứng dụng sẽ chuyển hướng đến trang đăng nhập ([`login.jsp`](src/main/webapp/login.jsp)) hoặc trang đăng ký ([`register.jsp`](src/main/webapp/register.jsp)).
4.  Có thể tạo tài khoản mới hoặc sử dụng tài khoản đã có (nếu database đã có dữ liệu) để đăng nhập và bắt đầu chat.

