<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="model.*,util.HtmlUtil,java.util.*,java.text.SimpleDateFormat" %><%
Employee5 nv = (Employee5) session.getAttribute("user");
Invoice5 invoice = (Invoice5) session.getAttribute("lastInvoice");
if (nv == null || !"SaleStaff".equalsIgnoreCase(nv.getVaiTro())) {
    response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp?err=timeout");
    return;
}
if (invoice == null) {
    response.sendRedirect("gdNhanVienBanHang.jsp");
    return;
}
%><!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Vé CineMan</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/cineman.css">
</head>
<body>
<h1>Hóa đơn CineMan #<%=invoice.getId()%></h1>
<p>Phương thức thanh toán: <%=HtmlUtil.escape(invoice.getPpThanhToan())%></p>
<p>Tổng giá: <%=invoice.getTongTien()%> VNĐ; giảm giá: <%=invoice.getTienGiamGia()%> VNĐ</p>
<p><strong>Đã thanh toán: <%=invoice.getThuThucTe()%> VNĐ</strong></p>
<h2>Vé</h2>
<table border="1" cellpadding="5">
    <tr><th>Mã vé</th><th>Phim</th><th>Phòng</th><th>Thời gian</th><th>Ghế</th><th>Giá vé</th><th>QR</th></tr>
    <% for (Ticket5 ticket : invoice.getTickets()) { %>
    <tr>
        <td><%=HtmlUtil.escape(ticket.getMaVe())%></td>
        <td><%=HtmlUtil.escape(ticket.getTenPhim())%></td>
        <td><%=HtmlUtil.escape(ticket.getTenPhong())%></td>
        <td><%=HtmlUtil.escape(ticket.getThoiGianChieu())%></td>
        <td><%=HtmlUtil.escape(ticket.getGhe())%></td>
        <td><%=ticket.getGiaVe()%> VNĐ</td>
        <td><code><%=HtmlUtil.escape(ticket.getMaQR())%></code></td>
    </tr>
    <% } %>
</table>
<p><button type="button" onclick="window.print()">In hóa đơn và vé</button></p>
<p><button type="button" onclick="window.location.href='gdNhanVienBanHang.jsp'">Quay về trang chủ</button></p>
</body>
</html>
