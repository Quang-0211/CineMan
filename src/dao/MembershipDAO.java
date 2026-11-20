package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.security.SecureRandom;
import java.util.Calendar;

import model.Constants;
import model.Customer5;
import model.OtpVerification5;
import util.PasswordUtil;

public class MembershipDAO extends DAO {
    private static final SecureRandom OTP_RANDOM = new SecureRandom();

    /**
     * Tạo tài khoản chờ OTP, hồ sơ khách và yêu cầu OTP trong cùng giao dịch.
     * @param customer thông tin đăng ký
     * @param password mật khẩu rõ để băm trước khi lưu
     * @return OTP mới cùng id khách hàng hoặc null nếu dữ liệu bị trùng/lỗi
     * // Ứng với bước 3-16 trong biểu đồ tuần tự UC-04.
     */
    public OtpVerification5 taoYeuCauDangKi(Customer5 customer, String password) {
        if (!ketNoiSanSang() || customer == null) {
            return null;
        }
        boolean owner = false;
        try {
            owner = con.getAutoCommit();
            if (owner) {
                con.setAutoCommit(false);
            }
            String check = "SELECT id FROM tblUser WHERE username=? OR email=? OR sdt=? FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(check)) {
                ps.setString(1, customer.getUsername());
                ps.setString(2, customer.getEmail());
                ps.setString(3, customer.getSdt());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ketThucGiaoDich(owner, false);
                        return null;
                    }
                }
            }
            String insertUser = "INSERT INTO tblUser(username,hoTen,email,sdt,dob,password) "
                    + "VALUES(?,?,?,?,?,?)";
            int customerId;
            try (PreparedStatement ps = con.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, customer.getUsername());
                ps.setString(2, customer.getHoTen());
                ps.setString(3, customer.getEmail());
                ps.setString(4, customer.getSdt());
                ps.setDate(5, customer.getDob());
                ps.setString(6, PasswordUtil.sha256(password));
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        ketThucGiaoDich(owner, false);
                        return null;
                    }
                    customerId = keys.getInt(1);
                }
            }
            customer.setTrangThai("PENDING_OTP");
            if (!new CustomerDAO().addCustomer(customer, customerId)) {
                ketThucGiaoDich(owner, false);
                return null;
            }

            OtpVerification5 otp = taoOtp();
            otp.setCustomerId(customerId);
            String insertOtp = "INSERT INTO tblOtpVerification(code,expired_at,soLanSai,tblCustomerid) "
                    + "VALUES(?,?,0,?)";
            try (PreparedStatement ps = con.prepareStatement(insertOtp, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, otp.getCode());
                ps.setTimestamp(2, otp.getExpiredAt());
                ps.setInt(3, customerId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        otp.setId(keys.getInt(1));
                    }
                }
            }
            ketThucGiaoDich(owner, true);
            return otp;
        } catch (Exception e) {
            e.printStackTrace();
            ketThucGiaoDich(owner, false);
            return null;
        }
    }

    /**
     * Thay mã OTP đã hết hạn bằng mã mới và đặt lại số lần nhập sai.
     * @param customerId khách hàng đang chờ xác thực
     * @return OTP mới hoặc null nếu tài khoản không còn chờ
     * // Ứng với luồng gửi lại mã ở UC-04.
     */
    public OtpVerification5 guiLaiOTP(int customerId) {
        if (!ketNoiSanSang()) {
            return null;
        }
        boolean owner = false;
        try {
            owner = con.getAutoCommit();
            if (owner) {
                con.setAutoCommit(false);
            }
            String check = "SELECT trangThai FROM tblCustomer WHERE tblUserid=? FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(check)) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || !"PENDING_OTP".equalsIgnoreCase(rs.getString("trangThai"))) {
                        ketThucGiaoDich(owner, false);
                        return null;
                    }
                }
            }
            OtpVerification5 otp = taoOtp();
            otp.setCustomerId(customerId);
            String sql = "INSERT INTO tblOtpVerification(code,expired_at,soLanSai,tblCustomerid) "
                    + "VALUES(?,?,0,?)";
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, otp.getCode());
                ps.setTimestamp(2, otp.getExpiredAt());
                ps.setInt(3, customerId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        otp.setId(keys.getInt(1));
                    }
                }
            }
            ketThucGiaoDich(owner, true);
            return otp;
        } catch (Exception e) {
            e.printStackTrace();
            ketThucGiaoDich(owner, false);
            return null;
        }
    }

    /**
     * Đối chiếu OTP và kích hoạt tài khoản, hạng ban đầu và chi tiêu năm.
     * @param customerId khách hàng đăng ký
     * @param code mã nhập từ giao diện
     * @return ACTIVE, WRONG, EXPIRED, LOCKED hoặc ERROR
     * // Ứng với bước 19-25 trong biểu đồ tuần tự UC-04.
     */
    public String kichHoatThanhVien(int customerId, String code) {
        if (!ketNoiSanSang()) {
            return "ERROR";
        }

        boolean owner = false;
        try {
            owner = con.getAutoCommit();
            if (owner) {
                con.setAutoCommit(false);
            }
            String select = "SELECT id,code,expired_at,soLanSai FROM tblOtpVerification "
                    + "WHERE tblCustomerid=? ORDER BY id DESC LIMIT 1 FOR UPDATE";
            int otpId;
            String expected;
            Timestamp expiredAt;
            int attempts;
            try (PreparedStatement ps = con.prepareStatement(select)) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        ketThucGiaoDich(owner, false);
                        return "EXPIRED";
                    }
                    otpId = rs.getInt("id");
                    expected = rs.getString("code");
                    expiredAt = rs.getTimestamp("expired_at");
                    attempts = rs.getInt("soLanSai");
                }
            }
            if (attempts >= Constants.SO_LAN_NHAP_OTP_TOI_DA) {
                ketThucGiaoDich(owner, false);
                return "LOCKED";
            }
            if (expiredAt == null || expiredAt.getTime() <= System.currentTimeMillis()) {
                ketThucGiaoDich(owner, false);
                return "EXPIRED";
            }
            if (code == null || !expected.equals(code.trim())) {
                String wrong = "UPDATE tblOtpVerification SET soLanSai=soLanSai+1 WHERE id=?";
                try (PreparedStatement ps = con.prepareStatement(wrong)) {
                    ps.setInt(1, otpId);
                    ps.executeUpdate();
                }
                if (attempts + 1 >= Constants.SO_LAN_NHAP_OTP_TOI_DA) {
                    String expire = "UPDATE tblCustomer SET trangThai='EXPIRED' WHERE tblUserid=?";
                    try (PreparedStatement ps = con.prepareStatement(expire)) {
                        ps.setInt(1, customerId);
                        ps.executeUpdate();
                    }
                }
                ketThucGiaoDich(owner, true);
                return attempts + 1 >= Constants.SO_LAN_NHAP_OTP_TOI_DA ? "LOCKED" : "WRONG";
            }
            String activate = "UPDATE tblCustomer SET trangThai='ACTIVE' "
                    + "WHERE tblUserid=? AND trangThai='PENDING_OTP'";
            try (PreparedStatement ps = con.prepareStatement(activate)) {
                ps.setInt(1, customerId);
                if (ps.executeUpdate() != 1) {
                    ketThucGiaoDich(owner, false);
                    return "ERROR";
                }
            }
            String spending = "INSERT INTO tblMemberYearlySpending"
                    + "(year,tongChiTieu,tblCustomerid,tblMembershipPlanid) "
                    + "VALUES(YEAR(CURDATE()),0,?,"
                    + "(SELECT id FROM tblMembershipPlan WHERE tenGoi=?))";
            try (PreparedStatement ps = con.prepareStatement(spending)) {
                ps.setInt(1, customerId);
                ps.setString(2, Constants.HANG_BAN_DAU);
                if (ps.executeUpdate() != 1) {
                    ketThucGiaoDich(owner, false);
                    return "ERROR";
                }
            }
            String consume = "UPDATE tblOtpVerification SET code='',expired_at=NOW() WHERE id=?";
            try (PreparedStatement ps = con.prepareStatement(consume)) {
                ps.setInt(1, otpId);
                ps.executeUpdate();
            }
            ketThucGiaoDich(owner, true);
            return "ACTIVE";
        } catch (Exception e) {
            e.printStackTrace();
            ketThucGiaoDich(owner, false);
            return "ERROR";
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

    private OtpVerification5 taoOtp() {
        OtpVerification5 otp = new OtpVerification5();
        otp.setCode(String.format("%06d", OTP_RANDOM.nextInt(1000000)));
        Calendar expiration = Calendar.getInstance();
        expiration.add(Calendar.MINUTE, Constants.THOI_GIAN_OTP_PHUT);
        otp.setExpiredAt(new Timestamp(expiration.getTimeInMillis()));
        return otp;
    }
}
