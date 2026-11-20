package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Movie5;

public class MovieDAO extends DAO {
    /**
     * Lấy danh sách phim đang chiếu cùng thể loại, định dạng và nhà sản xuất.
     * @return danh sách phim, danh sách rỗng nếu chưa có phim
     * // Ứng với bước 3-5 trong biểu đồ tuần tự UC-03.
     */
    public ArrayList<Movie5> layDanhSachPhimDangChieu() {
        return layDanhSachPhimDangChieu(null, null);
    }

    /**
     * Lọc danh sách phim đang chiếu theo tên và thể loại.
     * @param tuKhoa tên phim cần tìm, có thể null
     * @param genreId mã thể loại, có thể null
     * @return danh sách phim phù hợp
     * // Ứng với bước 3-5 trong biểu đồ tuần tự UC-03.
     */
    public ArrayList<Movie5> layDanhSachPhimDangChieu(String tuKhoa, Integer genreId) {
        ArrayList<Movie5> movies = new ArrayList<Movie5>();
        if (!ketNoiSanSang()) {
            return movies;
        }
        String sql = "SELECT m.id, m.tenPhim, m.thoiGianChieu, m.nhanTuoi, m.trangThai, "
                + "m.ngayKhoiChieu, m.ngayKetThuc, m.moTa, "
                + "GROUP_CONCAT(DISTINCT g.genreName ORDER BY g.genreName SEPARATOR ', ') AS theLoai, "
                + "GROUP_CONCAT(DISTINCT f.tenDinhDang ORDER BY f.tenDinhDang SEPARATOR ', ') AS dinhDang, "
                + "GROUP_CONCAT(DISTINCT p.producerName ORDER BY p.producerName SEPARATOR ', ') AS nhaSanXuat "
                + "FROM tblMovie m "
                + "LEFT JOIN tblMovieGenre mg ON mg.tblMovieid = m.id "
                + "LEFT JOIN tblGenre g ON g.id = mg.tblGenreid "
                + "LEFT JOIN tblMovieFormat mf ON mf.tblMovieid = m.id "
                + "LEFT JOIN tblFormat f ON f.id = mf.tblFormatid "
                + "LEFT JOIN tblMovieProducer mp ON mp.tblMovieid = m.id "
                + "LEFT JOIN tblProducer p ON p.id = mp.tblProducerid "
                + "WHERE m.trangThai = 'ACTIVE' "
                + "AND (m.ngayKhoiChieu IS NULL OR m.ngayKhoiChieu <= CURDATE()) "
                + "AND (m.ngayKetThuc IS NULL OR m.ngayKetThuc >= CURDATE()) "
                + "AND (? IS NULL OR ? = '' OR m.tenPhim LIKE CONCAT('%', ?, '%')) "
                + "AND (? IS NULL OR g.id = ?) "
                + "GROUP BY m.id ORDER BY m.tenPhim";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tuKhoa);
            ps.setString(2, tuKhoa);
            ps.setString(3, tuKhoa);
            if (genreId == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, genreId);
                ps.setInt(5, genreId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movie5 movie = new Movie5();
                    movie.setId(rs.getInt("id"));
                    movie.setTenPhim(rs.getString("tenPhim"));
                    movie.setThoiGianChieu(rs.getInt("thoiGianChieu"));
                    movie.setNhanTuoi(rs.getString("nhanTuoi"));
                    movie.setTrangThai(rs.getString("trangThai"));
                    movie.setNgayKhoiChieu(rs.getDate("ngayKhoiChieu"));
                    movie.setNgayKetThuc(rs.getDate("ngayKetThuc"));
                    movie.setMoTa(rs.getString("moTa"));
                    movie.setTheLoai(rs.getString("theLoai"));
                    movie.setDinhDang(rs.getString("dinhDang"));
                    movie.setNhaSanXuat(rs.getString("nhaSanXuat"));
                    movies.add(movie);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return movies;
    }
}
