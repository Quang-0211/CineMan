package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DAO {
    public static Connection con;

    public DAO() {
        ketNoi();
    }

    private static synchronized void ketNoi() {
        try {
            if (con != null && !con.isClosed()) {
                return;
            }
            String url = cauHinh("cineman.dbUrl", "CINEMAN_DB_URL",
                    "jdbc:mysql://localhost:3306/cineman?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8");
            String username = cauHinh("cineman.dbUser", "CINEMAN_DB_USER", "root");
            String password = cauHinh("cineman.dbPassword", "CINEMAN_DB_PASSWORD", "Quang@0211");
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            System.out.println("ket noi thanh cong");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ket noi that bai");
            con = null;
        }
    }

    private static String cauHinh(String property, String environment, String fallback) {
        String value = System.getProperty(property);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(environment);
        }
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    protected boolean ketNoiSanSang() {
        return con != null;
    }

    protected String placeholders(int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                result.append(',');
            }
            result.append('?');
        }
        return result.toString();
    }

    protected int ganDanhSachId(PreparedStatement statement, int viTriBatDau, Iterable<Integer> ids)
            throws SQLException {
        int index = viTriBatDau;
        for (Integer id : ids) {
            statement.setInt(index++, id);
        }
        return index;
    }
    public static void main(String[] args) {
        ketNoi();
    }
}
