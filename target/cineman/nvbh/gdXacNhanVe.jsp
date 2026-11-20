<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="dao.*,model.*,util.*,java.util.*,java.text.SimpleDateFormat" %><%
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
ArrayList<SeatShowtime5> allSeats = new SeatShowtimeDAO().layDanhSachGhe(showtimeId);
Map<Integer, SeatShowtime5> byId = new HashMap<Integer, SeatShowtime5>();
if (allSeats != null) for (SeatShowtime5 seat : allSeats) byId.put(seat.getId(), seat);
ArrayList<Ticket5> ticketPreviews = new ArrayList<Ticket5>();
String ticketError = null;
java.math.BigDecimal total = java.math.BigDecimal.ZERO;
for (Integer id : selected) {
    SeatShowtime5 seat = byId.get(id);
    if (seat != null) {
        Ticket5 ticket = new Ticket5();
        ticket.setSeatShowtimeId(seat.getId());
        ticket.setGiaVe(PriceCalculator.tinhGiaCoBan(seat));
        ticket.setTrangThai("PENDING_PAYMENT");
        ticket.setTenPhim(seat.getTenPhim());
        ticket.setTenPhong(seat.getTenPhong());
        ticket.setThoiGianChieu(new SimpleDateFormat("dd/MM/yyyy HH:mm")
                .format(seat.getThoiGianBatDau()));
        ticket.setGhe(seat.getSeatLabel());
        ticketPreviews.add(ticket);
        total = total.add(ticket.getGiaVe());
    }
}
@SuppressWarnings("unchecked")
Map<Integer, Integer> saleTicketIds =
        (Map<Integer, Integer>) session.getAttribute("saleTicketIds");
Set<Integer> selectedSeatIds = new HashSet<Integer>(selected);
TicketDAO ticketDAO = new TicketDAO();
if (ticketPreviews.size() != selected.size()) {
    ticketError = "Không tìm thấy đầy đủ thông tin ghế. Vui lòng chọn lại.";
}
if (ticketError == null && (saleTicketIds == null
        || !saleTicketIds.keySet().equals(selectedSeatIds))) {
    if (saleTicketIds != null
            && !ticketDAO.deletePendingTickets(new ArrayList<Integer>(saleTicketIds.values()))) {
        ticketError = "Không thể dọn các vé chờ thanh toán cũ.";
    } else if (ticketError == null) {
        LinkedHashMap<Integer, Integer> newTicketIds = new LinkedHashMap<Integer, Integer>();
        for (Ticket5 ticket : ticketPreviews) {
            Ticket5 savedTicket = ticketDAO.themVe(ticket.getSeatShowtimeId(), ticket.getGiaVe());
            if (savedTicket == null) {
                ticketError = "Không lưu được vé chờ thanh toán. Vui lòng chọn lại ghế.";
                ticketDAO.deletePendingTickets(new ArrayList<Integer>(newTicketIds.values()));
                break;
            }
            ticket.setId(savedTicket.getId());
            ticket.setMaVe(savedTicket.getMaVe());
            ticket.setMaQR(savedTicket.getMaQR());
            newTicketIds.put(ticket.getSeatShowtimeId(), savedTicket.getId());
        }
        if (ticketError == null) {
            saleTicketIds = newTicketIds;
            session.setAttribute("saleTicketIds", saleTicketIds);
        } else {
            session.removeAttribute("saleTicketIds");
        }
    }
} else if (ticketError == null) {
    for (Ticket5 ticket : ticketPreviews) {
        Integer ticketId = saleTicketIds.get(ticket.getSeatShowtimeId());
        if (ticketId == null) {
            ticketError = "Không tìm thấy mã vé đã lưu. Vui lòng chọn lại ghế.";
            break;
        }
        ticket.setId(ticketId);
        ticket.setMaVe("CM-" + ticketId);
        ticket.setMaQR("CINEMAN:TICKET:" + ticketId);
    }
}
%><!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Xác nhận vé</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/cineman.css">
</head>
<body>
<h1>GDXacNhanVe - Xác nhận thông tin vé</h1>
<p>Nhân viên vui lòng đọc lại thông tin để khách kiểm tra.</p>
<% if (ticketError != null) { %><h4><%=HtmlUtil.escape(ticketError)%></h4><% } %>
<table border="1" cellpadding="5">
    <tr><th>STT</th><th>Mã vé</th><th>Phim</th><th>Phòng</th><th>Suất chiếu</th><th>Ghế</th><th>Loại ghế</th><th>Giá vé</th><th>QR</th></tr>
    <% int stt = 0; for (Ticket5 ticket : ticketPreviews) { SeatShowtime5 seat = byId.get(ticket.getSeatShowtimeId()); %>
    <tr>
        <td><%=++stt%></td><td><%=HtmlUtil.escape(ticket.getMaVe())%></td>
        <td><%=HtmlUtil.escape(ticket.getTenPhim())%></td><td><%=HtmlUtil.escape(ticket.getTenPhong())%></td>
        <td><%=HtmlUtil.escape(ticket.getThoiGianChieu())%></td>
        <td><%=HtmlUtil.escape(ticket.getGhe())%></td>
        <td><%=HtmlUtil.escape(seat.getLoaiGhe())%></td>
        <td><%=ticket.getGiaVe()%> VNĐ</td>
        <td><code><%=HtmlUtil.escape(ticket.getMaQR())%></code></td>
    </tr>
    <% } %>
</table>
<p>Số lượng vé: <%=ticketPreviews.size()%>; tổng tạm tính: <strong><%=total%> VNĐ</strong></p>
<p><a href="gdSoDoPhongChieu.jsp?showtimeId=<%=showtimeId%>">Quay lại</a></p>
<% if (ticketError == null) { %>
<p><a href="gdThanhToan.jsp">Thanh toán</a></p>
<% } %>
</body>
</html>
