<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="dao.*,model.*,java.util.*" %><%
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
String[] selected = request.getParameterValues("seatIds");
if (selected == null || selected.length == 0) {
    session.setAttribute("saleError", "Vui lòng chọn ít nhất một ghế.");
    response.sendRedirect("gdSoDoPhongChieu.jsp?showtimeId=" + showtimeId);
    return;
}
Set<Integer> requested = new LinkedHashSet<Integer>();
try {
    for (String id : selected) {
        requested.add(Integer.valueOf(id));
    }
} catch (NumberFormatException e) {
    session.setAttribute("saleError", "Danh sách ghế không hợp lệ.");
    response.sendRedirect("gdSoDoPhongChieu.jsp?showtimeId=" + showtimeId);
    return;
}
if (requested.size() != selected.length) {
    session.setAttribute("saleError", "Danh sách ghế không hợp lệ.");
    response.sendRedirect("gdSoDoPhongChieu.jsp?showtimeId=" + showtimeId);
    return;
}
ArrayList<SeatShowtime5> allSeats = new SeatShowtimeDAO().layDanhSachGhe(showtimeId);
if (allSeats == null) {
    response.sendRedirect("gdSoDoPhongChieu.jsp?showtimeId=" + showtimeId);
    return;
}
Set<Integer> validIds = new HashSet<Integer>();
for (SeatShowtime5 seat : allSeats) {
    if (requested.contains(seat.getId()) && "AVAILABLE".equalsIgnoreCase(seat.getTrangThai())) {
        validIds.add(seat.getId());
    }
}
if (validIds.size() != requested.size()
        || !new SeatShowtimeDAO().khoaGheSuatChieu(selectedIds(requested))) {
    session.setAttribute("saleError", "Ghế đã được bán hoặc đang khóa, vui lòng chọn lại.");
    response.sendRedirect("gdSoDoPhongChieu.jsp?showtimeId=" + showtimeId);
    return;
}
// Bước 35: khóa ghế trong 10 phút rồi lưu lựa chọn vào phiên bán vé.
session.removeAttribute("saleTicketIds");
session.setAttribute("saleSeatIds", new ArrayList<Integer>(requested));
session.setAttribute("saleShowtimeId", showtimeId);
response.sendRedirect("gdXacNhanVe.jsp");
%><%!
private List<Integer> selectedIds(Set<Integer> ids) {
    return new ArrayList<Integer>(ids);
}
%>
