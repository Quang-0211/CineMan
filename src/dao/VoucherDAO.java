package dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import model.Voucher5;

public class VoucherDAO extends DAO {
    /**
     * Tìm voucher theo mã và kiểm tra hạn dùng, trạng thái, số lượng.
     * @param code mã voucher
     * @return voucher hợp lệ hoặc null
     * // Ứng với bước 21a trong kịch bản v3 UC-03.
     */
    public Voucher5 kiemTraVoucher(String code) {
        if (!ketNoiSanSang() || code == null || code.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT id, code, mucGiamGia, giamToiDa, ngayHetHan, giaToiThieu, "
                + "loaiGiam, trangThai, ngayBatDau, soLuong, tblMembershipPlanid "
                + "FROM tblVoucher WHERE code = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Voucher5 voucher = new Voucher5();
                voucher.setId(rs.getInt("id"));
                voucher.setCode(rs.getString("code"));
                voucher.setMucGiamGia(rs.getString("mucGiamGia"));
                voucher.setGiamToiDa(rs.getBigDecimal("giamToiDa"));
                voucher.setNgayHetHan(rs.getDate("ngayHetHan"));
                voucher.setGiaToiThieu(rs.getBigDecimal("giaToiThieu"));
                voucher.setLoaiGiam(rs.getString("loaiGiam"));
                voucher.setTrangThai(rs.getString("trangThai"));
                voucher.setNgayBatDau(rs.getDate("ngayBatDau"));
                voucher.setSoLuong(rs.getInt("soLuong"));
                voucher.setMembershipPlanId(rs.getInt("tblMembershipPlanid"));
                if (!"ACTIVE".equalsIgnoreCase(voucher.getTrangThai())
                        || voucher.getSoLuong() <= 0
                        || !isTrongHan(voucher.getNgayBatDau(), voucher.getNgayHetHan())) {
                    return null;
                }
                return voucher;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isTrongHan(Date batDau, Date ketThuc) {
        LocalDate today = LocalDate.now();
        return batDau != null && ketThuc != null
                && !today.isBefore(batDau.toLocalDate())
                && !today.isAfter(ketThuc.toLocalDate());
    }
}
