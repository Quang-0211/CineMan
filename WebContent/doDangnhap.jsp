<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="dao.UserDAO,dao.CustomerDAO,model.User5,model.Employee5,model.Customer5" %><%
request.setCharacterEncoding("UTF-8");
String username = request.getParameter("username");
String password = request.getParameter("password");
// Bước 1: doDangnhap gọi UserDAO kiểm tra tài khoản.
User5 user = new UserDAO().kiemTraDangNhap(username, password);
if (user == null) {
    response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp?err=fail");
    return;
}
if (user instanceof Customer5) {
    Customer5 member = new CustomerDAO().timKhachTheoSDT(user.getSdt());
    if (member != null) {
        member.setPassword(null);
        member.setVaiTro("Customer");
        user = member;
    }
}
session.setAttribute("user", user);
if (user instanceof Employee5 && "SaleStaff".equalsIgnoreCase(user.getVaiTro())) {
    response.sendRedirect(request.getContextPath() + "/nvbh/gdNhanVienBanHang.jsp");
} else if (user instanceof Customer5) {
    response.sendRedirect(request.getContextPath() + "/kh/gdKhachHang.jsp");
} else {
    session.invalidate();
    response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp?err=fail");
}
%>
