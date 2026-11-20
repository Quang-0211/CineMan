<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="model.Customer5,util.HtmlUtil" %>
    <% Customer5 kh=(Customer5) session.getAttribute("user"); if (kh==null ||
        !"Customer".equalsIgnoreCase(kh.getVaiTro()) || !"ACTIVE".equalsIgnoreCase(kh.getTrangThai())) {
        response.sendRedirect(request.getContextPath() + "/gddangnhap.jsp?err=timeout" ); return; } %>
        <% session.removeAttribute("registrationSuccess"); %>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>Khách hàng CineMan</title>
            <link rel="stylesheet" href="<%=request.getContextPath()%>/css/cineman.css">
        </head>

        <body>
            <h1>CineMan - Trang chủ khách hàng</h1>
            <p>Xin chào, <%=HtmlUtil.escape(kh.getHoTen())%>
            </p>
            <p><a href="<%=request.getContextPath()%>/doDangxuat.jsp">Đăng xuất</a></p>
            <p>Module đăng ký thành viên đã hoàn tất.</p>
        </body>

        </html>