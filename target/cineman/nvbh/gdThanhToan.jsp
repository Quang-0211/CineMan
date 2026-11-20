<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="dao.*,model.*,util.*,java.util.*" %><%
Employee5 nv = (Employee5) session.getAttribute("user");
@SuppressWarnings("unchecked")
ArrayList<Integer> selected = (ArrayList<Integer>) session.getAttribute("saleSeatIds");
Integer showtimeId = (Integer) session.getAttribute("saleShowtimeId");
if (nv == null || !"SaleStaff".equalsIgnoreCase(nv.getVaiTro())) {
    response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp?err=timeout");
    return;
}
if (selected == null || selected.isEmpty() || showtimeId == null) {
    response.sendRedirect("gdDSPhimDangChieu.jsp");
    return;
}
String error = null;
if ("POST".equalsIgnoreCase(request.getMethod())) {
    request.setCharacterEncoding("UTF-8");
    String action = request.getParameter("action");
    if ("customer".equals(action)) {
        Customer5 customer = new CustomerDAO().timKhachTheoSDT(request.getParameter("sdt"));
        if (customer == null) {
            session.removeAttribute("saleCustomer");
            error = "Không tìm thấy thành viên đang hoạt động. Có thể tiếp tục với khách vãng lai.";
        } else {
            session.setAttribute("saleCustomer", customer);
        }
    } else if ("voucher".equals(action)) {
        Voucher5 voucher = new VoucherDAO().kiemTraVoucher(request.getParameter("code"));
        if (voucher == null) {
            session.removeAttribute("saleVoucher");
            error = "Mã voucher không hợp lệ, hết hạn hoặc đã hết lượt.";
        } else {
            session.setAttribute("saleVoucher", voucher);
        }
    }
}
@SuppressWarnings("unchecked")
ArrayList<SeatShowtime5> allSeats = new SeatShowtimeDAO().layDanhSachGhe(showtimeId);
Map<Integer, SeatShowtime5> byId = new HashMap<Integer, SeatShowtime5>();
if (allSeats != null) for (SeatShowtime5 seat : allSeats) byId.put(seat.getId(), seat);
java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
for (Integer id : selected) {
    SeatShowtime5 seat = byId.get(id);
    if (seat != null) subtotal = subtotal.add(PriceCalculator.tinhGiaCoBan(seat));
}
Customer5 customer = (Customer5) session.getAttribute("saleCustomer");
Voucher5 voucher = (Voucher5) session.getAttribute("saleVoucher");
java.math.BigDecimal memberDiscount = PriceCalculator.tinhGiamTheoHang(
        subtotal, customer == null ? null : customer.getTenHang());
java.math.BigDecimal afterMember = subtotal.subtract(memberDiscount);
java.math.BigDecimal voucherDiscount = PriceCalculator.tinhGiamVoucher(voucher, afterMember);
java.math.BigDecimal total = afterMember.subtract(voucherDiscount).max(java.math.BigDecimal.ZERO);
String errorFromSession = (String) session.getAttribute("saleError");
session.removeAttribute("saleError");
if (error == null) error = errorFromSession;
%><!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Thanh toán</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/cineman.css">
</head>
<body>
<h1>GDThanhToan - Thanh toán</h1>
<% if (error != null) { %><h4><%=HtmlUtil.escape(error)%></h4><% } %>
<h2>Thông tin vé</h2>
<table border="1" cellpadding="5">
    <tr><th>Phim</th><th>Suất</th><th>Phòng</th><th>Ghế</th><th>Loại ghế</th><th>Giá</th></tr>
    <% for (Integer id : selected) { SeatShowtime5 seat = byId.get(id); if (seat != null) { %>
    <tr><td><%=HtmlUtil.escape(seat.getTenPhim())%></td>
        <td><%=seat.getThoiGianBatDau()%></td><td><%=HtmlUtil.escape(seat.getTenPhong())%></td>
        <td><%=HtmlUtil.escape(seat.getSeatLabel())%></td><td><%=HtmlUtil.escape(seat.getLoaiGhe())%></td>
        <td><%=PriceCalculator.tinhGiaCoBan(seat)%> VNĐ</td></tr>
    <% } } %>
</table>
<h3>Ưu đãi</h3>
<form method="post" action="gdThanhToan.jsp">
    <label>SĐT thành viên:
        <input type="tel" name="sdt" pattern="0[0-9]{9}" maxlength="10"
               value="<%=customer == null ? "" : HtmlUtil.escape(customer.getSdt())%>">
    </label>
    <button type="submit" name="action" value="customer">Kiểm tra thành viên</button>
</form>
<h3>Chi tiết giá</h3>
<p>Thành tiền: <%=subtotal%> VNĐ</p>
<form method="post" action="doThanhToan.jsp">
    <p>Phương thức:
        <label><input type="radio" name="ppThanhToan" value="CASH" checked> Tiền mặt</label>
        <label><input type="radio" name="ppThanhToan" value="QR_DEMO"> QR / ví điện tử (demo)</label>
    </p>
    <p><label><input type="checkbox" name="received" value="yes" required>
        Nhân viên đã xác nhận nhận đủ thanh toán (QR demo chưa kết nối ngân hàng)</label></p>
    <p><button type="submit" name="action" value="pay">Xác nhận đã nhận tiền</button></p>
</form>
<p><a href="gdXacNhanVe.jsp">Quay lại</a></p>
</body>
</html>
