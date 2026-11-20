package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Showtime5;

public class ShowtimeDAO extends DAO {
    /**
     * Lấy các suất chưa chiếu của một phim.
     * @param movieId mã phim được chọn
     * @return danh sách suất chiếu
     * // Ứng với bước 9-12 trong biểu đồ tuần tự UC-03.
     */
    public ArrayList<Showtime5> layLichChieuTheoPhim(int movieId) {
        ArrayList<Showtime5> showtimes = new ArrayList<Showtime5>();
        if (!ketNoiSanSang()) {
            return showtimes;
        }
        String sql = "SELECT s.id, s.thoiGianBatDau, s.thoiGianKetThuc, s.giaVeTieuChuan, s.trangThai, "
                + "r.id AS roomId, r.tenPhong, r.sucChua, rt.price AS roomPhuThu, "
                + "f.tenDinhDang, mf.id AS movieFormatId, m.id AS movieId, m.tenPhim "
                + "FROM tblShowtime s "
                + "JOIN tblRoom r ON r.id = s.tblRoomid "
                + "JOIN tblRoomType rt ON rt.id = r.tblRoomTypeid "
                + "JOIN tblMovieFormat mf ON mf.id = s.tblMovieFormatid "
                + "JOIN tblMovie m ON m.id = mf.tblMovieid "
                + "JOIN tblFormat f ON f.id = mf.tblFormatid "
                + "WHERE m.id = ? AND s.trangThai = 'ACTIVE' AND s.thoiGianBatDau > NOW() "
                + "ORDER BY s.thoiGianBatDau";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Showtime5 showtime = new Showtime5();
                    showtime.setId(rs.getInt("id"));
                    showtime.setThoiGianBatDau(rs.getTimestamp("thoiGianBatDau"));
                    showtime.setThoiGianKetThuc(rs.getTimestamp("thoiGianKetThuc"));
                    showtime.setGiaVeTieuChuan(rs.getBigDecimal("giaVeTieuChuan"));
                    showtime.setTrangThai(rs.getString("trangThai"));
                    showtime.setRoomId(rs.getInt("roomId"));
                    showtime.setTenPhong(rs.getString("tenPhong"));
                    showtime.setTenDinhDang(rs.getString("tenDinhDang"));
                    showtime.setRoomPhuThu(rs.getBigDecimal("roomPhuThu"));
                    showtime.setMovieId(rs.getInt("movieId"));
                    showtime.setTenPhim(rs.getString("tenPhim"));
                    showtimes.add(showtime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return showtimes;
    }

    public Integer getMovieIdByShowtimeId(int showtimeId) {
        if (!ketNoiSanSang()) {
            return null;
        }
        String sql = "SELECT mf.tblMovieid FROM tblShowtime s "
                + "JOIN tblMovieFormat mf ON mf.id=s.tblMovieFormatid WHERE s.id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Integer.valueOf(rs.getInt(1)) : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
