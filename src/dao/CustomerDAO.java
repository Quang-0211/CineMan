package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import model.Constants;
import model.Customer5;
import util.PasswordUtil;

public class CustomerDAO extends DAO {
    /**
     * Tìm thành viên đang hoạt động theo số điện thoại.
     * 
     * @param sdt số điện thoại khách hàng
     * @return hồ sơ thành viên hoặc null nếu không tồn tại
     *         // Ứng với bước 21a trong kịch bản v3 UC-03.
     */
    public Customer5 timKhachTheoSDT(String sdt) {
        if (!ketNoiSanSang() || sdt == null) {
            return null;
        }
        String sql = "SELECT u.id, u.username, u.hoTen, u.email, u.sdt, u.dob, "
                + "c.ngayDangKi, c.trangThai, p.tenGoi, mys.year, mys.tongChiTieu "
                + "FROM tblUser u "
                + "JOIN tblCustomer c ON c.tblUserid = u.id "
                + "LEFT JOIN tblMemberYearlySpending mys "
                + "ON mys.tblCustomerid = c.tblUserid AND mys.year = YEAR(CURDATE()) "
                + "LEFT JOIN tblMembershipPlan p ON p.id = mys.tblMembershipPlanid "
                + "WHERE u.sdt = ? AND c.trangThai = 'ACTIVE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sdt.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Customer5 customer = new Customer5();
                customer.setId(rs.getInt("id"));
                customer.setUsername(rs.getString("username"));
                customer.setHoTen(rs.getString("hoTen"));
                customer.setEmail(rs.getString("email"));
                customer.setSdt(rs.getString("sdt"));
                customer.setDob(rs.getDate("dob"));
                customer.setNgayDangKi(rs.getDate("ngayDangKi"));
                customer.setTrangThai(rs.getString("trangThai"));
                customer.setTenHang(rs.getString("tenGoi"));
                customer.setTongChiTieu(rs.getBigDecimal("tongChiTieu"));
                return customer;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Kiểm tra số điện thoại hoặc email đã được dùng hay chưa.
     * 
     * @param sdt   số điện thoại
     * @param email địa chỉ email
     * @return true nếu có thông tin bị trùng
     *         // Ứng với bước 3a trong kịch bản UC-04.
     */
    public boolean kiemTraTrungSDTEmail(String sdt, String email) {
        if (!ketNoiSanSang()) {
            return true;
        }
        String sql = "SELECT COUNT(*) AS soLuong FROM tblUser WHERE sdt = ? OR email = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sdt);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("soLuong") > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Kiểm tra trùng username, số điện thoại hoặc email trong một truy vấn.
     */
    public boolean kiemTraTrungThongTin(String username, String sdt, String email) {
        if (!ketNoiSanSang()) {
            return true;
        }
        String sql = "SELECT 1 FROM tblUser WHERE username=? OR sdt=? OR email=? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, sdt);
            ps.setString(3, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Tạo hồ sơ khách hàng trong một giao dịch đang mở.
     * 
     * @param customer thông tin hồ sơ
     * @param userId   khóa người dùng đã tạo
     * @return true nếu ghi được hồ sơ
     *         // Ứng với bước lưu Customer của UC-04 sau khi tạo tài khoản.
     */
    public boolean addCustomer(Customer5 customer, int userId) {
        if (!ketNoiSanSang() || customer == null) {
            return false;
        }
        String sql = "INSERT INTO tblCustomer(tblUserid, ngayDangKi, trangThai) VALUES(?, CURDATE(), ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, customer.getTrangThai());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Bước 10: CustomerDAO thực thi phương thức luuKhachHang() lưu thông tin khách
     * hàng vào CSDL.
     * Lưu tài khoản và hồ sơ khách hàng đang chờ xác thực OTP trong cùng giao dịch.
     * Mật khẩu trong đối tượng customer được băm trước khi ghi và xóa khỏi đối
     * tượng.
     * 
     * @param customer thông tin đăng ký
     * @return true nếu lưu thành công; đồng thời gán id cho customer
     */
    public boolean luuKhachHang(Customer5 customer) {
        if (!ketNoiSanSang() || customer == null || customer.getPassword() == null) {
            return false;
        }
        String hashedPassword = PasswordUtil.sha256(customer.getPassword());
        customer.setPassword(null);
        boolean owner = false;
        try {
            owner = con.getAutoCommit();
            if (owner) {
                con.setAutoCommit(false);
            }
            String insertUser = "INSERT INTO tblUser(username,hoTen,email,sdt,dob,password) "
                    + "VALUES(?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, customer.getUsername());
                ps.setString(2, customer.getHoTen());
                ps.setString(3, customer.getEmail());
                ps.setString(4, customer.getSdt());
                ps.setDate(5, customer.getDob());
                ps.setString(6, hashedPassword);
                if (ps.executeUpdate() != 1) {
                    ketThucGiaoDich(owner, false);
                    return false;
                }
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        ketThucGiaoDich(owner, false);
                        return false;
                    }
                    customer.setId(keys.getInt(1));
                }
            }
            customer.setTrangThai("PENDING_OTP");
            String insertCustomer = "INSERT INTO tblCustomer(tblUserid,ngayDangKi,trangThai) "
                    + "VALUES(?,CURDATE(),?)";
            try (PreparedStatement ps = con.prepareStatement(insertCustomer)) {
                ps.setInt(1, customer.getId());
                ps.setString(2, customer.getTrangThai());
                if (ps.executeUpdate() != 1) {
                    ketThucGiaoDich(owner, false);
                    return false;
                }
            }
            ketThucGiaoDich(owner, true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ketThucGiaoDich(owner, false);
            return false;
        }
    }

    /**
     * Cập nhật trạng thái tài khoản sau khi OTP đã hợp lệ.
     */
    public boolean capNhatKhachHang(Customer5 customer) {
        if (!ketNoiSanSang() || customer == null
                || !"ACTIVE".equalsIgnoreCase(customer.getTrangThai())) {
            return false;
        }
        String sql = "UPDATE tblCustomer SET trangThai='ACTIVE' "
                + "WHERE tblUserid=? AND trangThai='PENDING_OTP'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customer.getId());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cộng chi tiêu năm và đồng bộ hạng thành viên trong giao dịch bán vé.
     * 
     * @param customerId khách hàng thành viên
     * @param amount     số tiền thực thu
     * @return true nếu cập nhật thành công
     *         // Ứng với bước 25 trong kịch bản v3 UC-03.
     */
    public boolean congChiTieuNam(int customerId, BigDecimal amount) {
        if (!ketNoiSanSang()) {
            return true;
        }
        String select = "SELECT tongChiTieu FROM tblMemberYearlySpending "
                + "WHERE tblCustomerid=? AND year=YEAR(CURDATE()) FOR UPDATE";
        String insert = "INSERT INTO tblMemberYearlySpending(year,tongChiTieu,tblCustomerid,tblMembershipPlanid) "
                + "VALUES(YEAR(CURDATE()),?,?," + "(SELECT id FROM tblMembershipPlan WHERE tenGoi=?))";
        String update = "UPDATE tblMemberYearlySpending SET tongChiTieu=?, "
                + "tblMembershipPlanid=(SELECT id FROM tblMembershipPlan WHERE tenGoi=?) "
                + "WHERE tblCustomerid=? AND year=YEAR(CURDATE())";
        try (PreparedStatement selectPs = con.prepareStatement(select)) {
            selectPs.setInt(1, customerId);
            try (ResultSet rs = selectPs.executeQuery()) {
                if (!rs.next()) {
                    try (PreparedStatement insertPs = con.prepareStatement(insert)) {
                        insertPs.setBigDecimal(1, amount);
                        insertPs.setInt(2, customerId);
                        insertPs.setString(3, Constants.xepHang(amount));
                        return insertPs.executeUpdate() == 1;
                    }
                }
                BigDecimal total = rs.getBigDecimal("tongChiTieu").add(amount);
                try (PreparedStatement updatePs = con.prepareStatement(update)) {
                    updatePs.setBigDecimal(1, total);
                    updatePs.setString(2, Constants.xepHang(total));
                    updatePs.setInt(3, customerId);
                    return updatePs.executeUpdate() == 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void ketThucGiaoDich(boolean owner, boolean success) {
        if (!owner || con == null) {
            return;
        }
        try {
            if (success) {
                con.commit();
            } else {
                con.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
