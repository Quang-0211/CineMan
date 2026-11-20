<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="dao.*,model.*,util.HtmlUtil,java.util.*,java.text.SimpleDateFormat" %><%
Employee5 nv = (Employee5) session.getAttribute("user");
if (nv == null || !"SaleStaff".equalsIgnoreCase(nv.getVaiTro())) {
    response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp?err=timeout");
    return;
}
int movieId;
try {
    movieId = Integer.parseInt(request.getParameter("movieId"));
} catch (Exception e) {
    response.sendRedirect("gdDSPhimDangChieu.jsp");
    return;
}
// Bước 9: GDLichChieu gọi ShowtimeDAO.layLichChieuTheoPhim().
ArrayList<Showtime5> showtimes = new ShowtimeDAO().layLichChieuTheoPhim(movieId);
String saleError = (String) session.getAttribute("saleError");
session.removeAttribute("saleError");
%><!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Lịch chiếu</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/cineman.css">
</head>
<body>
<h1>GDLichChieu - Lịch chiếu</h1>
<% if (saleError != null) { %><h4><%=HtmlUtil.escape(saleError)%></h4><% } %>
<% if (showtimes == null) { %><h4>Không thể tải lịch chiếu.</h4>
<% } else if (showtimes.isEmpty()) { %><h4>Phim hiện không có suất chiếu sắp tới.</h4>
<% } else { %>
<p>Phim: <%=HtmlUtil.escape(showtimes.get(0).getTenPhim())%></p>
<table border="1" cellpadding="5">
    <tr><th>TT</th><th>Ngày chiếu</th><th>Giờ bắt đầu</th><th>Định dạng</th><th>Phòng</th><th>Giá gốc</th><th>Chọn</th></tr>
    <% int stt = 0; SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
       for (Showtime5 showtime : showtimes) { %>
    <tr>
        <td><%=++stt%></td>
        <td><%=dateFormat.format(showtime.getThoiGianBatDau())%></td>
        <td><%=timeFormat.format(showtime.getThoiGianBatDau())%></td>
        <td><%=HtmlUtil.escape(showtime.getTenDinhDang())%></td>
        <td><%=HtmlUtil.escape(showtime.getTenPhong())%></td>
        <td><%=showtime.getGiaVeTieuChuan()%> VNĐ</td>
        <td><a href="gdSoDoPhongChieu.jsp?showtimeId=<%=showtime.getId()%>">Chọn</a></td>
    </tr>
    <% } %>
</table>
<% } %>
<p><a href="gdDSPhimDangChieu.jsp">Quay lại</a></p>
</body>
</html>
