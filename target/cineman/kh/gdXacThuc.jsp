<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    Boolean registrationSuccess =
            (Boolean) session.getAttribute("registrationSuccess");

    Object user = session.getAttribute("user");

    boolean isSuccess = Boolean.TRUE.equals(registrationSuccess)
                       && user != null;
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Xác thực tài khoản</title>

    <link rel="stylesheet"
          href="<%=request.getContextPath()%>/css/cineman.css">
</head>
<body>

<% if (isSuccess) { %>

    <h1>Đăng ký thành công</h1>
    <p>Tài khoản của bạn đã được xác thực.</p>

    <form method="get"
          action="<%=request.getContextPath()%>/kh/gdKhachHang.jsp">
        <button type="submit">OK</button>
    </form>

<% } else { %>

    <h1>Xác thực OTP</h1>
    <p>Mã xác thực đã được gửi đến email đăng ký.</p>

    <% if (request.getAttribute("otpError") != null) { %>
        <h4>
            <%=request.getAttribute("otpError")%>
        </h4>
    <% } %>

    <% if (request.getAttribute("demoCode") != null) { %>
        <p>
            Mã OTP demo:
            <strong><%=request.getAttribute("demoCode")%></strong>
        </p>
    <% } %>

    <form method="post"
          action="<%=request.getContextPath()%>/kh/doXacThuc.jsp">

        <p>
            <label>
                Mã OTP:
                <input type="text"
                       name="otp"
                       required
                       inputmode="numeric"
                       pattern="[0-9]{6}"
                       maxlength="6">
            </label>
        </p>

        <button type="submit">Xác thực</button>
    </form>

<% } %>

</body>
</html>