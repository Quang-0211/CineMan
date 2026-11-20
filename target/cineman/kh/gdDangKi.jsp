<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng ký thành viên CineMan</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/cineman.css">
</head>
<body>
<h1>Đăng ký thành viên</h1>
<% if ("invalid".equals(request.getAttribute("registrationError"))) { %>
<h4>Vui lòng kiểm tra các trường bắt buộc và định dạng dữ liệu.</h4>
<% } else if ("duplicate".equals(request.getAttribute("registrationError"))) { %>
<h4>Tên đăng nhập, số điện thoại hoặc email đã được sử dụng.</h4>
<% } else if ("save".equals(request.getAttribute("registrationError"))) { %>
<h4>Không thể lưu yêu cầu đăng ký. Vui lòng thử lại.</h4>
<% } %>
<form method="post" action="<%=request.getContextPath()%>/kh/doDangKi.jsp">
    <p><label>Họ và tên: <input type="text" name="hoTen" required maxlength="100"></label></p>
    <p><label>Ngày sinh: <input type="date" name="dob" required></label></p>
    <p><label>Email: <input type="email" name="email" required maxlength="100"></label></p>
    <p><label>Số điện thoại: <input type="tel" name="sdt" required pattern="0[0-9]{9}" maxlength="10"></label></p>
    <p><label>Tên đăng nhập: <input type="text" name="username" required maxlength="50"></label></p>
    <p><label>Mật khẩu: <input type="password" name="password" required minlength="8"></label></p>
    <p><label>Nhập lại mật khẩu: <input type="password" name="confirmPassword" required minlength="8"></label></p>
    <p><button type="submit">Đăng ký</button>
       <a href="<%=request.getContextPath()%>/gddangnhap.jsp">Quay lại</a></p>
</form>
</body>
</html>
