package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.Customer5;
import model.Employee5;
import model.User5;
import util.PasswordUtil;

public class UserDAO extends DAO {
    /**
     * Xác thực tài khoản và giới hạn quyền vào đúng trang theo vai trò.
     * 
     * @param username tên đăng nhập
     * @param password mật khẩu rõ do người dùng nhập
     * @return người dùng đã xác thực hoặc null nếu sai/không được phép
     *         // Ứng với bước đăng nhập trong kịch bản hỗ trợ hai module.
     */
    public User5 kiemTraDangNhap(String username, String password) {
        if (!ketNoiSanSang() || username == null || password == null) {
            return null;
        }
        String sql = "SELECT u.id, u.username, u.hoTen, u.email, u.sdt, u.dob, u.password, "
                + "CASE WHEN e.tblUserid IS NOT NULL THEN 'SaleStaff' ELSE 'Customer' END AS vaiTro, "
                + "e.chucVu, c.trangThai "
                + "FROM tblUser u "
                + "LEFT JOIN tblEmployee e ON e.tblUserid = u.id "
                + "LEFT JOIN tblCustomer c ON c.tblUserid = u.id "
                + "WHERE u.username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !PasswordUtil.sha256(password).equalsIgnoreCase(rs.getString("password"))) {
                    return null;
                }
                String role = rs.getString("vaiTro");
                if ("SaleStaff".equalsIgnoreCase(role)) {
                    String job = rs.getString("chucVu");
                    if (!"SaleStaff".equalsIgnoreCase(job)
                            && !"Nhân viên bán hàng".equalsIgnoreCase(job)) {
                        return null;
                    }
                    Employee5 employee = new Employee5();
                    ganThongTin(employee, rs, role);
                    employee.setChucVu(job);
                    return employee;
                }
                if ("Customer".equalsIgnoreCase(role)
                        && "ACTIVE".equalsIgnoreCase(rs.getString("trangThai"))) {
                    Customer5 customer = new Customer5();
                    ganThongTin(customer, rs, role);
                    customer.setTrangThai(rs.getString("trangThai"));
                    return customer;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Kiểm tra trùng username trước khi tạo tài khoản.
     * 
     * @param username tên đăng nhập cần kiểm tra
     * @return true nếu đã tồn tại
     *         // Ứng với bước kiểm tra dữ liệu UC-04 trước khi phát OTP.
     */
    public boolean kiemTraTrungTaiKhoan(String username) {
        if (!ketNoiSanSang()) {
            return true;
        }
        String sql = "SELECT COUNT(*) AS soLuong FROM tblUser WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("soLuong") > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private void ganThongTin(User5 user, ResultSet rs, String role) throws Exception {
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setHoTen(rs.getString("hoTen"));
        user.setEmail(rs.getString("email"));
        user.setSdt(rs.getString("sdt"));
        user.setDob(rs.getDate("dob"));
        user.setVaiTro(role);
    }
}
