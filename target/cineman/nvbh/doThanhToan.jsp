<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="dao.*,model.*,java.util.*" %><%
request.setCharacterEncoding("UTF-8");
Employee5 nv = (Employee5) session.getAttribute("user");
@SuppressWarnings("unchecked")
ArrayList<Integer> selected = (ArrayList<Integer>) session.getAttribute("saleSeatIds");
Integer showtimeId = (Integer) session.getAttribute("saleShowtimeId");
if (nv == null || !"SaleStaff".equalsIgnoreCase(nv.getVaiTro())) {
    response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp?err=timeout");
    return;
}
if (!"POST".equalsIgnoreCase(request.getMethod())
        || !"pay".equals(request.getParameter("action"))
        || !"yes".equals(request.getParameter("received"))) {
    session.setAttribute("saleError", "Cần xác nhận đã nhận thanh toán.");
    response.sendRedirect("gdThanhToan.jsp");
    return;
}
if (selected == null || selected.isEmpty() || showtimeId == null) {
    session.setAttribute("saleError", "Không có ghế được chọn.");
    response.sendRedirect("gdDSPhimDangChieu.jsp");
    return;
}
@SuppressWarnings("unchecked")
Map<Integer, Integer> saleTicketIds =
        (Map<Integer, Integer>) session.getAttribute("saleTicketIds");
if (saleTicketIds == null || !saleTicketIds.keySet().equals(new HashSet<Integer>(selected))) {
    session.setAttribute("saleError", "Không tìm thấy vé đã xác nhận. Vui lòng xác nhận lại vé.");
    response.sendRedirect("gdXacNhanVe.jsp");
    return;
}

Customer5 customer = (Customer5) session.getAttribute("saleCustomer");
Voucher5 voucher = (Voucher5) session.getAttribute("saleVoucher");
Invoice5 invoice = new Invoice5();
invoice.setSeatShowtimeIds(selected);
invoice.setEmployeeId(nv.getId());
invoice.setCustomerId(customer == null ? null : Integer.valueOf(customer.getId()));
invoice.setVoucherId(voucher == null ? null : Integer.valueOf(voucher.getId()));
String payment = request.getParameter("ppThanhToan");
invoice.setPpThanhToan("QR_DEMO".equals(payment) ? "QR_DEMO" : "CASH");
invoice.setLoaiHoanDon("COUNTER");

InvoiceDAO invoiceDAO = new InvoiceDAO();
if (!invoiceDAO.themHoaDon(invoice)) {
    session.setAttribute("saleError", invoiceDAO.getLastError());
    response.sendRedirect("gdThanhToan.jsp");
    return;
}

TicketDAO ticketDAO = new TicketDAO();
ArrayList<Ticket5> savedTickets = new ArrayList<Ticket5>();
for (Ticket5 preview : invoice.getTickets()) {
    Integer ticketId = saleTicketIds.get(preview.getSeatShowtimeId());
    if (ticketId == null || !ticketDAO.capNhatVe(ticketId, "ACTIVE", invoice.getId())) {
        session.setAttribute("saleError", "Không cập nhật được trạng thái vé.");
        response.sendRedirect("gdThanhToan.jsp");
        return;
    }
    Ticket5 ticket = new Ticket5();
    ticket.setId(ticketId);
    ticket.setMaVe("CM-" + ticketId);
    ticket.setMaQR("CINEMAN:TICKET:" + ticketId);
    ticket.setGiaVe(preview.getGiaVe());
    ticket.setSeatShowtimeId(preview.getSeatShowtimeId());
    ticket.setTrangThai("ACTIVE");
    ticket.setTenPhim(preview.getTenPhim());
    ticket.setTenPhong(preview.getTenPhong());
    ticket.setThoiGianChieu(preview.getThoiGianChieu());
    ticket.setGhe(preview.getGhe());
    savedTickets.add(ticket);
}

if (!new SeatShowtimeDAO().capNhatGheSuatChieu(selected)) {
    session.setAttribute("saleError",
            "Ghế đã được bán hoặc hết thời gian giữ, vui lòng chọn lại.");
    response.sendRedirect("gdThanhToan.jsp");
    return;
}
invoice.setTickets(savedTickets);
if (!invoiceDAO.completeInvoice(invoice)) {
    session.setAttribute("saleError", invoiceDAO.getLastError());
    response.sendRedirect("gdThanhToan.jsp");
    return;
}

session.removeAttribute("saleSeatIds");
session.removeAttribute("saleShowtimeId");
session.removeAttribute("saleCustomer");
session.removeAttribute("saleVoucher");
session.removeAttribute("saleTicketIds");
session.setAttribute("lastInvoice", invoice);
response.sendRedirect("gdNhanVienBanHang.jsp");
%>
