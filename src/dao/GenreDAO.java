package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Genre5;

public class GenreDAO extends DAO {
    /**
     * Lấy danh sách thể loại để lọc phim đang chiếu.
     * @return danh sách thể loại
     * // Ứng với bước 3-5 trong biểu đồ tuần tự UC-03.
     */
    public ArrayList<Genre5> getAll() {
        ArrayList<Genre5> genres = new ArrayList<Genre5>();
        if (!ketNoiSanSang()) {
            return genres;
        }
        String sql = "SELECT id, genreName, description FROM tblGenre ORDER BY genreName";
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Genre5 genre = new Genre5();
                genre.setId(rs.getInt("id"));
                genre.setGenreName(rs.getString("genreName"));
                genre.setDescription(rs.getString("description"));
                genres.add(genre);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return genres;
    }
}
