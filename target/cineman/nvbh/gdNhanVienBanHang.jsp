<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.Employee5,util.HtmlUtil" %><%
Employee5 nv = (Employee5) session.getAttribute("user");
if (nv == null || !"SaleStaff".equalsIgnoreCase(nv.getVaiTro())) {
    response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp?err=timeout");
    return;
}
%><!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Nhân viên bán hàng</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/cineman.css">
</head>
<body>
<h1>CineMan - Trang chủ nhân viên bán hàng</h1>
<p>Xin chào, <%=HtmlUtil.escape(nv.getHoTen())%></p>
<p><a href="<%=request.getContextPath()%>/doDangxuat.jsp">Đăng xuất</a></p>
<hr>
<h2>GDNhanVienBanHang</h2>
<p><a href="<%=request.getContextPath()%>/nvbh/gdDSPhimDangChieu.jsp">Bán vé tại quầy</a></p>
</body>
</html>
