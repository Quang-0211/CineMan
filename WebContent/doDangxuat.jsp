<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
// Bước 1: kết thúc phiên đăng nhập.
session.invalidate();
response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp");
%>
