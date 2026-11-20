package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import model.OtpVerification5;

public class OtpVerificationDAO extends DAO {
    /**
     * Lưu OTP đã được sinh từ giao diện đăng ký.
     * @return true nếu lưu thành công và gán được id cho OTP
     */
    public boolean luuOtp(OtpVerification5 otp) {
        if (!ketNoiSanSang() || otp == null || otp.getCode() == null
                || otp.getExpiredAt() == null || otp.getCustomerId() <= 0) {
            return false;
        }
        String sql = "INSERT INTO tblOtpVerification(code,expired_at,soLanSai,tblCustomerid) "
                + "VALUES(?,?,0,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, otp.getCode());
            ps.setTimestamp(2, otp.getExpiredAt());
            ps.setInt(3, otp.getCustomerId());
            if (ps.executeUpdate() != 1) {
                return false;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    return false;
                }
                otp.setId(keys.getInt(1));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy OTP mới nhất của khách hàng đang đăng ký.
     */
    public OtpVerification5 layOtp(int customerId) {
        if (!ketNoiSanSang() || customerId <= 0) {
            return null;
        }
        String sql = "SELECT id,code,expired_at,tblCustomerid "
                + "FROM tblOtpVerification WHERE tblCustomerid=? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                OtpVerification5 otp = new OtpVerification5();
                otp.setId(rs.getInt("id"));
                otp.setCode(rs.getString("code"));
                otp.setExpiredAt(rs.getTimestamp("expired_at"));
                otp.setCustomerId(rs.getInt("tblCustomerid"));
                return otp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
