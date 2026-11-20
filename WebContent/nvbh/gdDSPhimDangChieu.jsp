<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="dao.*,model.*,util.HtmlUtil,java.util.*" %><%
Employee5 nv = (Employee5) session.getAttribute("user");
if (nv == null || !"SaleStaff".equalsIgnoreCase(nv.getVaiTro())) {
    response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp?err=timeout");
    return;
}
ArrayList<Genre5> genres = new GenreDAO().getAll();
String keyword = request.getParameter("tenPhim");
Integer genreId = null;
try {
    if (request.getParameter("genreId") != null && !request.getParameter("genreId").isEmpty()) {
        genreId = Integer.valueOf(request.getParameter("genreId"));
    }
} catch (NumberFormatException e) {
    genreId = null;
}
// Bước 3: GDDSPhimDangChieu gọi MovieDAO.layDanhSachPhimDangChieu().
ArrayList<Movie5> movies = new MovieDAO().layDanhSachPhimDangChieu(keyword, genreId);
String saleError = (String) session.getAttribute("saleError");
session.removeAttribute("saleError");
%><!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Danh sách phim đang chiếu</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/cineman.css">
</head>
<body>
<h1>GDDSPhimDangChieu - Danh sách phim đang chiếu</h1>
<% if (saleError != null) { %><h4><%=HtmlUtil.escape(saleError)%></h4><% } %>
<form method="get" action="gdDSPhimDangChieu.jsp">
    <label>Tên phim: <input name="tenPhim" value="<%=HtmlUtil.escape(keyword)%>"></label>
    <label>Thể loại:
        <select name="genreId">
            <option value="">Tất cả</option>
            <% if (genres != null) for (Genre5 genre : genres) { %>
            <option value="<%=genre.getId()%>" <%=genreId != null && genreId.intValue() == genre.getId() ? "selected" : ""%>><%=HtmlUtil.escape(genre.getGenreName())%></option>
            <% } %>
        </select>
    </label>
    <button type="submit">Tìm kiếm</button>
    <!-- <a href="gdDSPhimDangChieu.jsp">Đặt lại</a> -->
</form>
<% if (movies == null) { %>
<h4>Không thể tải danh sách phim.</h4>
<% } else if (movies.isEmpty()) { %>
<h4>Hiện không có phim đang chiếu phù hợp.</h4>
<% } else { %>
<table border="1" cellpadding="5">
    <tr><th>TT</th><th>Tên phim</th><th>Ngày khởi chiếu</th><th>Thể loại</th><th>Thời lượng</th><th>Định dạng</th><th>Nhà sản xuất</th><th>Nhãn tuổi</th><th>Chọn</th></tr>
    <% int stt = 0; for (Movie5 movie : movies) { %>
    <tr>
        <td><%=++stt%></td>
        <td><%=HtmlUtil.escape(movie.getTenPhim())%></td>
        <td><%=movie.getNgayKhoiChieu() == null ? "" : movie.getNgayKhoiChieu()%></td>
        <td><%=HtmlUtil.escape(movie.getTheLoai())%></td>
        <td><%=movie.getThoiGianChieu()%> phút</td>
        <td><%=HtmlUtil.escape(movie.getDinhDang())%></td>
        <td><%=HtmlUtil.escape(movie.getNhaSanXuat())%></td>
        <td><%=HtmlUtil.escape(movie.getNhanTuoi())%></td>
        <td><a href="gdLichChieu.jsp?movieId=<%=movie.getId()%>">Chọn</a></td>
    </tr>
    <% } %>
</table>
<% } %>
<p><a href="gdNhanVienBanHang.jsp">Về trang chủ</a></p>
</body>
</html>
