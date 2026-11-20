<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="dao.*,model.*,util.*,java.util.*" %><%
Employee5 nv = (Employee5) session.getAttribute("user");
if (nv == null || !"SaleStaff".equalsIgnoreCase(nv.getVaiTro())) {
    response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp?err=timeout");
    return;
}
int showtimeId;
try {
    showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
} catch (Exception e) {
    response.sendRedirect("gdDSPhimDangChieu.jsp");
    return;
}
String saleError = (String) session.getAttribute("saleError");
session.removeAttribute("saleError");
// Bước 14-18: GDSoDoPhongChieu gọi SeatShowtimeDAO.layDanhSachGhe().
ArrayList<SeatShowtime5> seats = new SeatShowtimeDAO().layDanhSachGhe(showtimeId);
if (seats == null || seats.isEmpty()) {
    session.setAttribute("saleError", "Không tìm thấy ghế cho suất chiếu đã chọn.");
    Integer movieId = new ShowtimeDAO().getMovieIdByShowtimeId(showtimeId);
    if (movieId != null) {
        response.sendRedirect("gdLichChieu.jsp?movieId=" + movieId);
    } else {
        response.sendRedirect("gdDSPhimDangChieu.jsp");
    }
    return;
}
SeatShowtime5 first = seats.get(0);
TreeMap<String, TreeMap<Integer, SeatShowtime5>> byRow =
        new TreeMap<String, TreeMap<Integer, SeatShowtime5>>();
for (SeatShowtime5 seat : seats) {
    if (!byRow.containsKey(seat.getSeatRow())) {
        byRow.put(seat.getSeatRow(), new TreeMap<Integer, SeatShowtime5>());
    }
    TreeMap<Integer, SeatShowtime5> row = byRow.get(seat.getSeatRow());
    SeatShowtime5 existing = row.get(Integer.valueOf(seat.getSeatNumber()));
    if (existing == null || ("AVAILABLE".equalsIgnoreCase(existing.getTrangThai())
            && !"AVAILABLE".equalsIgnoreCase(seat.getTrangThai()))) {
        row.put(Integer.valueOf(seat.getSeatNumber()), seat);
    }
}
%><!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Sơ đồ phòng chiếu</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/cineman.css">
</head>
<body>
<main class="page-shell">
<h1 class="page-heading">Sơ đồ phòng chiếu</h1>
<% if (saleError != null) { %><div class="error"><%=HtmlUtil.escape(saleError)%></div><% } %>
<section class="panel movie-info" aria-label="Thông tin suất chiếu">
    <div class="info-item"><strong>Phim</strong><span><%=HtmlUtil.escape(first.getTenPhim())%></span></div>
    <div class="info-item"><strong>Suất chiếu</strong><span><%=first.getThoiGianBatDau()%></span></div>
    <div class="info-item"><strong>Phòng</strong><span><%=HtmlUtil.escape(first.getTenPhong())%></span></div>
    <div class="info-item"><strong>Định dạng</strong><span><%=HtmlUtil.escape(first.getTenDinhDang())%></span></div>
</section>
<form method="post" action="doKhoaGhe.jsp">
<input type="hidden" name="showtimeId" value="<%=showtimeId%>">
<section class="panel">
    <div class="screen">MÀN HÌNH</div>
    <div class="seat-map">
    <% for (Map.Entry<String, TreeMap<Integer, SeatShowtime5>> row : byRow.entrySet()) { %>
        <div class="seat-row">
            <span class="row-name">Hàng <%=HtmlUtil.escape(row.getKey())%></span>
            <div class="seat-items">
        <% for (SeatShowtime5 seat : row.getValue().values()) {
            String state = seat.getTrangThai();
            boolean available = "AVAILABLE".equalsIgnoreCase(state);
            java.math.BigDecimal price = PriceCalculator.tinhGiaCoBan(seat);
        %>
            <% if (available) { %>
            <label class="seat-choice">
                <input type="checkbox" name="seatIds" value="<%=seat.getId()%>"
                       data-price="<%=price.toPlainString()%>" onchange="tinhTong()">
                <span class="seat-face">
                    <strong><%=HtmlUtil.escape(seat.getSeatLabel())%></strong>
                    <small><%=HtmlUtil.escape(seat.getLoaiGhe())%></small>
                </span>
            </label>
            <% } else { %>
            <div class="seat-choice unavailable" aria-disabled="true">
                <span class="seat-face">
                    <strong><%=HtmlUtil.escape(seat.getSeatLabel())%></strong>
                    <small><%=HtmlUtil.escape(state)%></small>
                </span>
            </div>
            <% } %>
        <% } %>
            </div>
        </div>
    <% } %>
    </div>
    <div class="legend">
        <span class="legend-item"><span class="legend-swatch"></span>Ghế có thể chọn</span>
        <span class="legend-item"><span class="legend-swatch unavailable"></span>Ghế không khả dụng</span>
    </div>
</section>
<section class="panel">
    <div class="total-box"><span>Tổng tạm tính</span><strong id="tongTamTinh">0 VNĐ</strong></div>
    <div class="actions">
        <button class="button" type="submit">Tiếp theo</button>
        <a class="button secondary" href="gdLichChieu.jsp?movieId=<%=first.getMovieId()%>">Quay lại</a>
    </div>
</section>
</form>
</main>
<script>
function tinhTong() {
    var total = 0;
    document.querySelectorAll('input[name="seatIds"]:checked').forEach(function (item) {
        total += Number(item.getAttribute('data-price'));
    });
    document.getElementById('tongTamTinh').textContent =
        total.toLocaleString('vi-VN') + ' VNĐ';
}
</script>
</body>
</html>
