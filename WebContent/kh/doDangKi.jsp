<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="dao.CustomerDAO,dao.OtpVerificationDAO,model.Constants,model.Customer5,model.OtpVerification5,java.security.SecureRandom,java.sql.Date,java.sql.Timestamp,java.util.Calendar" %><%!
private static final SecureRandom OTP_RANDOM = new SecureRandom();

private boolean kiemTraHopLe(String hoTen, Date dob, String email, String sdt,
        String username, String password, String confirmPassword) {
    return hoTen != null && !hoTen.trim().isEmpty()
            && dob != null && !dob.after(new Date(System.currentTimeMillis()))
            && email != null && email.trim().matches(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            && sdt != null && sdt.trim().matches("0[0-9]{9}")
            && username != null && !username.trim().isEmpty()
            && password != null && password.length() >= Constants.MAT_KHAU_TOI_THIEU
            && password.equals(confirmPassword);
}

private OtpVerification5 genOtp() {
    OtpVerification5 otp = new OtpVerification5();
    otp.setCode(String.format("%06d", OTP_RANDOM.nextInt(1000000)));
    Calendar expiration = Calendar.getInstance();
    expiration.add(Calendar.MINUTE, Constants.THOI_GIAN_OTP_PHUT);
    otp.setExpiredAt(new Timestamp(expiration.getTimeInMillis()));
    return otp;
}

private String sendOtp(OtpVerification5 otp, String email) {
    if (otp == null || otp.getCode() == null || email == null || email.trim().isEmpty()) {
        return null;
    }
    System.out.println("[CineMan OTP demo] email=" + email + ", code=" + otp.getCode());
    return otp.getCode();
}
%>
<%
request.setCharacterEncoding("UTF-8");
if (!"POST".equalsIgnoreCase(request.getMethod())) {
    request.setAttribute("registrationError", request.getParameter("err"));
    request.getRequestDispatcher("/kh/gdDangKi.jsp").forward(request, response);
    return;
}

String hoTen = request.getParameter("hoTen");
String dobText = request.getParameter("dob");
String email = request.getParameter("email");
String sdt = request.getParameter("sdt");
String username = request.getParameter("username");
String password = request.getParameter("password");
String confirmPassword = request.getParameter("confirmPassword");

Date dob = null;
try {
    if (dobText != null && !dobText.trim().isEmpty()) {
        dob = Date.valueOf(dobText);
    }
} catch (IllegalArgumentException e) {
    response.sendRedirect(request.getContextPath() + "/kh/doDangKi.jsp?err=invalid");
    return;
}

if (!kiemTraHopLe(hoTen, dob, email, sdt, username, password, confirmPassword)) {
    response.sendRedirect(request.getContextPath() + "/kh/doDangKi.jsp?err=invalid");
    return;
}

CustomerDAO customerDao = new CustomerDAO();
String normalizedUsername = username.trim();
String normalizedPhone = sdt.trim();
String normalizedEmail = email.trim();
if (customerDao.kiemTraTrungThongTin(normalizedUsername, normalizedPhone, normalizedEmail)) {
    response.sendRedirect(request.getContextPath() + "/kh/doDangKi.jsp?err=duplicate");
    return;
}

Customer5 customer = new Customer5();
customer.setHoTen(hoTen.trim());
customer.setDob(dob);
customer.setEmail(normalizedEmail);
customer.setSdt(normalizedPhone);
customer.setUsername(normalizedUsername);
customer.setPassword(password);
customer.setVaiTro("Customer");
if (!customerDao.luuKhachHang(customer)) {
    response.sendRedirect(request.getContextPath() + "/kh/doDangKi.jsp?err=save");
    return;
}

OtpVerification5 otp = genOtp();
otp.setCustomerId(customer.getId());
OtpVerificationDAO otpDao = new OtpVerificationDAO();
if (!otpDao.luuOtp(otp) || sendOtp(otp, customer.getEmail()) == null) {
    response.sendRedirect(request.getContextPath() + "/kh/doDangKi.jsp?err=save");
    return;
}

session.setAttribute("pendingCustomerId", customer.getId());
session.setAttribute("pendingCustomer", customer);
session.setAttribute("otpDemoCode", otp.getCode());
response.sendRedirect(request.getContextPath() + "/kh/gdXacThuc.jsp");
%>
