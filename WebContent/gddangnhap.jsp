<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="util.HtmlUtil" %><%
String error = request.getParameter("err");
%><!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>CineMan - Đăng nhập</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/cineman.css">
</head>
<body>
<h1>CineMan - Đăng nhập</h1>
<% if ("timeout".equals(error)) { %><h4>Phiên làm việc đã hết. Vui lòng đăng nhập lại.</h4><% } %>
<% if ("fail".equals(error)) { %><h4>Sai tài khoản, mật khẩu hoặc tài khoản chưa được kích hoạt.</h4><% } %>
<% if ("error".equals(error)) { %><h4>Không thể xử lý yêu cầu. Vui lòng thử lại.</h4><% } %>
<form method="post" action="<%=request.getContextPath()%>/doDangnhap.jsp">
    <p><label>Tên đăng nhập: <input type="text" name="username" required maxlength="50"></label></p>
    <p><label>Mật khẩu: <input type="password" name="password" required></label></p>
    <p><button type="submit">Đăng nhập</button></p>
</form>
<p>Khách hàng mới? <a href="<%=request.getContextPath()%>/kh/doDangKi.jsp">Đăng ký thành viên</a></p>
</body>
</html>
