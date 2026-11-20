<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="dao.CustomerDAO,dao.OtpVerificationDAO,model.Customer5,model.OtpVerification5" %><%!
private String xacThucOtp(OtpVerification5 otp, String code) {
    if (otp == null || otp.getExpiredAt() == null
            || otp.getExpiredAt().getTime() <= System.currentTimeMillis()) {
        return "EXPIRED";
    }
    return code != null && code.equals(otp.getCode()) ? "VALID" : "INVALID";
}
%><%
request.setCharacterEncoding("UTF-8");
if (!"POST".equalsIgnoreCase(request.getMethod())) {
    response.sendRedirect(request.getContextPath() + "/kh/gdXacThuc.jsp");
    return;
}

Integer customerId = (Integer) session.getAttribute("pendingCustomerId");
Customer5 customer = (Customer5) session.getAttribute("pendingCustomer");
if (customerId == null || customer == null || customer.getId() != customerId.intValue()) {
    response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp?err=timeout");
    return;
}

OtpVerificationDAO otpDao = new OtpVerificationDAO();
OtpVerification5 otp = otpDao.layOtp(customerId);
String result = xacThucOtp(otp, request.getParameter("otp"));
if (!"VALID".equals(result)) {
    session.setAttribute("otpError",
            "EXPIRED".equals(result) ? "Mã OTP đã hết hạn." : "Mã OTP không chính xác.");
    response.sendRedirect(request.getContextPath() + "/kh/gdXacThuc.jsp");
    return;
}

customer.setTrangThai("ACTIVE");
CustomerDAO customerDao = new CustomerDAO();
if (!customerDao.capNhatKhachHang(customer)) {
    session.setAttribute("otpError", "Không thể cập nhật trạng thái tài khoản.");
    response.sendRedirect(request.getContextPath() + "/kh/gdXacThuc.jsp");
    return;
}

session.setAttribute("user", customer);
session.setAttribute("registrationSuccess", Boolean.TRUE);
session.removeAttribute("pendingCustomerId");
session.removeAttribute("pendingCustomer");
session.removeAttribute("otpDemoCode");
session.removeAttribute("otpError");
response.sendRedirect(request.getContextPath() + "/kh/gdXacThuc.jsp?status=success");
%>
