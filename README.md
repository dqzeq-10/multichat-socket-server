# Hướng dẫn Cài đặt và Chạy Project Multichat (JSP/Servlet/WebSocket)

Project này là một ứng dụng chat thời gian thực sử dụng Java Servlets, JSP, WebSocket và SQL Server.

## 1. Yêu cầu phần mềm

Trước khi bắt đầu, vui lòng đảm bảo đã cài đặt các phần mềm sau:

1.  **JDK (Java Development Kit):** Phiên bản 20.
2.  **Maven:** Công cụ quản lý build và dependency cho Java.
3.  **SQL Server:** Hệ quản trị cơ sở dữ liệu.
    *   **SQL Server Management Studio (SSMS):** SSMS20SSMS20
4.  **Apache Tomcat 10.1:** Servlet container để chạy ứng dụng web. Phiên bản 10 là phù hợp.
5.  **IDE:** NetBeans

## 2. Cài đặt Cơ sở dữ liệu (SQL Server)

Đây là bước **quan trọng**. Ứng dụng cần cơ sở dữ liệu để lưu trữ thông tin người dùng, phòng chat và tin nhắn.

1.  **Khởi chạy SQL Server:** Vui lòng đảm bảo dịch vụ SQL Server đang chạy.
2.  **Tạo Database:**
    *   Mở SSMS và kết nối tới SQL Server instance.
    *   Cần có file script `multichat_SQLQuery.sql` chứa các lệnh để:
        *   Tạo database (ví dụ: `CREATE DATABASE MultiChatDB;`)
        *   Tạo các bảng cần thiết (`Users`, `ChatRooms`, `Messages`, `RoomMembers`).
        *   Tạo các Stored Procedures (`sp_GetUserForLogin`, `sp_SaveChatMessage`, `sp_CreateUser`, `sp_GetChatHistory`, v.v.).
    *   Thực thi file script `multichat_SQLQuery.sql` này trong SSMS để tạo cấu trúc database và các stored procedures cần thiết.
3.  **Cấu hình Kết nối Database trong Project:**
    *   Mở file: [`src/main/java/com/multichat/util/DatabaseConnection.java`](src/main/java/com/multichat/util/DatabaseConnection.java)
    *   Tìm đến phương thức `getConnectionDB()`.
    *   **Quan trọng:** Vui lòng chỉnh sửa chuỗi kết nối (`connectionUrl`), `username`, và `password` để khớp với cấu hình SQL Server.
        *   Ví dụ chuỗi kết nối: `jdbc:sqlserver://localhost:1433;databaseName=MultiChatDB;encrypt=true;trustServerCertificate=true;` (Điều chỉnh `localhost`, `1433`, `MultiChatDB` nếu cần).
        *   Đảm bảo `username` và `password` là tài khoản SQL Server có quyền truy cập vào `MultiChatDB`.

## 3. Cài đặt Project

1.  **Lấy mã nguồn:** Giải nén file project.
2.  **Mở Project bằng IDE (Khuyến nghị):**
    *   Mở IDE (NetBeans).
    *   Chọn "Open Project" hoặc "Import Project".
    *   Chọn tùy chọn để mở/import project dưới dạng "Maven Project".
    *   Trỏ đến thư mục chứa file `pom.xml` của project. IDE sẽ tự động nhận diện và tải các dependencies được định nghĩa trong `pom.xml`.

## 4. Chạy ứng dụng
1.  Chuột phải vào project. Sau đó nhấn **run**.
2.  Đăng nhập tài khoản Tomcat.
3.  Ứng dụng sẽ mở trang web **http://localhost:8080/BT_Multichat_Jsp/.**
4.  Có thể tạo tài khoản mới hoặc sử dụng tài khoản đã có (nếu database đã có dữ liệu) để đăng nhập và bắt đầu chat.
5.  Tham gia các phòng chat, tạo phòng mới.

