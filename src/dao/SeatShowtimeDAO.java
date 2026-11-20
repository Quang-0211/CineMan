package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import model.Constants;
import model.SeatShowtime5;

public class SeatShowtimeDAO extends DAO {
    /**
     * Lấy sơ đồ ghế, coi LOCKED đã hết hạn như AVAILABLE.
     * @param showtimeId mã suất chiếu
     * @return danh sách ghế theo hàng và số ghế
     * // Ứng với bước 14-18 trong biểu đồ tuần tự UC-03.
     */
    public ArrayList<SeatShowtime5> layDanhSachGhe(int showtimeId) {
        ArrayList<SeatShowtime5> seats = new ArrayList<SeatShowtime5>();
        if (!ketNoiSanSang()) {
            return seats;
        }
        String sql = "SELECT ss.id AS seatShowtimeId, "
                + "CASE WHEN ss.trangThai = 'LOCKED' AND ss.lockedUntil <= NOW() "
                + "THEN 'AVAILABLE' ELSE ss.trangThai END AS trangThai, "
                + "ss.lockedUntil, se.id AS seatId, se.seatRow, se.seatNumber, "
                + "st.id AS seatTypeId, st.loaiGhe, st.phuThu, "
                + "s.id AS showtimeId, s.thoiGianBatDau, s.thoiGianKetThuc, "
                + "s.giaVeTieuChuan, r.tenPhong, rt.price AS roomPhuThu, "
                + "f.tenDinhDang, m.id AS movieId, m.tenPhim "
                + "FROM tblSeatShowtime ss "
                + "JOIN tblSeat se ON se.id = ss.tblSeatid "
                + "JOIN tblSeatType st ON st.id = se.tblSeatTypeid "
                + "JOIN tblShowtime s ON s.id = ss.tblShowtimeid "
                + "JOIN tblRoom r ON r.id = s.tblRoomid "
                + "JOIN tblRoomType rt ON rt.id = r.tblRoomTypeid "
                + "JOIN tblMovieFormat mf ON mf.id = s.tblMovieFormatid "
                + "JOIN tblFormat f ON f.id = mf.tblFormatid "
                + "JOIN tblMovie m ON m.id = mf.tblMovieid "
                + "WHERE s.id = ? ORDER BY se.seatRow, se.seatNumber";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SeatShowtime5 seat = new SeatShowtime5();
                    seat.setId(rs.getInt("seatShowtimeId"));
                    seat.setTrangThai(rs.getString("trangThai"));
                    seat.setLockedUntil(rs.getTimestamp("lockedUntil"));
                    seat.setSeatId(rs.getInt("seatId"));
                    seat.setSeatRow(rs.getString("seatRow"));
                    seat.setSeatNumber(rs.getInt("seatNumber"));
                    seat.setLoaiGhe(rs.getString("loaiGhe"));
                    seat.setPhuThuGhe(rs.getBigDecimal("phuThu"));
                    seat.setGiaVeTieuChuan(rs.getBigDecimal("giaVeTieuChuan"));
                    seat.setPhuThuPhong(rs.getBigDecimal("roomPhuThu"));
                    seat.setTenDinhDang(rs.getString("tenDinhDang"));
                    seat.setShowtimeId(rs.getInt("showtimeId"));
                    seat.setThoiGianBatDau(rs.getTimestamp("thoiGianBatDau"));
                    seat.setThoiGianKetThuc(rs.getTimestamp("thoiGianKetThuc"));
                    seat.setTenPhong(rs.getString("tenPhong"));
                    seat.setMovieId(rs.getInt("movieId"));
                    seat.setTenPhim(rs.getString("tenPhim"));
                    if ("LOCKED".equalsIgnoreCase(seat.getTrangThai())
                            && seat.getLockedUntil() != null
                            && seat.getLockedUntil().getTime() <= System.currentTimeMillis()) {
                        seat.setTrangThai("AVAILABLE");
                    }
                    seats.add(seat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return seats;
    }

    /**
     * Khóa đồng thời các ghế còn trống trong 10 phút.
     * @param seatShowtimeIds danh sách khóa ghế theo suất
     * @return true nếu tất cả ghế được khóa, false nếu có ghế bị chiếm
     * // Ứng với bước 35-37 trong biểu đồ tuần tự UC-03.
     */
    public boolean khoaGheSuatChieu(List<Integer> seatShowtimeIds) {
        if (!ketNoiSanSang() || seatShowtimeIds == null || seatShowtimeIds.isEmpty()) {
            return false;
        }
        Set<Integer> ids = new LinkedHashSet<Integer>(seatShowtimeIds);
        if (ids.size() != seatShowtimeIds.size() || ids.contains(null)) {
            return false;
        }
        boolean owner = false;
        try {
            owner = con.getAutoCommit();
            if (owner) {
                con.setAutoCommit(false);
            }
            String select = "SELECT id,trangThai,lockedUntil FROM tblSeatShowtime "
                    + "WHERE id IN (" + placeholders(ids.size()) + ") FOR UPDATE";
            try (PreparedStatement ps = con.prepareStatement(select)) {
                ganDanhSachId(ps, 1, ids);
                try (ResultSet rs = ps.executeQuery()) {
                    int found = 0;
                    while (rs.next()) {
                        found++;
                        String state = rs.getString("trangThai");
                        java.sql.Timestamp lockedUntil = rs.getTimestamp("lockedUntil");
                        boolean expiredLock = "LOCKED".equalsIgnoreCase(state)
                                && lockedUntil != null
                                && lockedUntil.getTime() <= System.currentTimeMillis();
                        if (!"AVAILABLE".equalsIgnoreCase(state) && !expiredLock) {
                            if (owner) {
                                con.rollback();
                            }
                            return false;
                        }
                    }
                    if (found != ids.size()) {
                        if (owner) {
                            con.rollback();
                        }
                        return false;
                    }
                }
            }
            String deletePending = "DELETE FROM tblTicket WHERE tblInvoiceid IS NULL "
                    + "AND trangThai='PENDING_PAYMENT' AND tblSeatShowtimeid IN ("
                    + placeholders(ids.size()) + ")";
            try (PreparedStatement ps = con.prepareStatement(deletePending)) {
                ganDanhSachId(ps, 1, ids);
                ps.executeUpdate();
            }
            String update = "UPDATE tblSeatShowtime SET trangThai='LOCKED', "
                    + "lockedUntil=DATE_ADD(NOW(), INTERVAL ? MINUTE) WHERE id IN ("
                    + placeholders(ids.size()) + ") AND (trangThai='AVAILABLE' "
                    + "OR (trangThai='LOCKED' AND lockedUntil<=NOW()))";
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setInt(1, Constants.THOI_GIAN_KHOA_GHE_PHUT);
                ganDanhSachId(ps, 2, ids);
                if (ps.executeUpdate() != ids.size()) {
                    if (owner) {
                        con.rollback();
                    }
                    return false;
                }
            }
            if (owner) {
                con.commit();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (owner) {
                    con.rollback();
                }
            } catch (Exception rollbackError) {
                rollbackError.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (owner) {
                    con.setAutoCommit(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Chuyển tất cả ghế đã khóa hợp lệ sang SOLD.
     * @param ids khóa ghế cần bán
     * @return true nếu mọi ghế vẫn đang được giữ
     * // Ứng với bước 62-64 trong biểu đồ tuần tự UC-03.
     */
    public boolean capNhatGheSuatChieu(List<Integer> ids) {
        if (!ketNoiSanSang() || ids == null || ids.isEmpty()) {
            return false;
        }
        String sql = "UPDATE tblSeatShowtime SET trangThai='SOLD', lockedUntil=NULL "
                + "WHERE id IN (" + placeholders(ids.size()) + ") "
                + "AND trangThai='LOCKED' AND lockedUntil>NOW()";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ganDanhSachId(ps, 1, ids);
            return ps.executeUpdate() == ids.size();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
