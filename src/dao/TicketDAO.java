package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import model.Ticket5;

public class TicketDAO extends DAO {
    /**
     * Lưu vé chờ thanh toán cho một ghế đã được giữ.
     * @param seatShowtimeId ghế của suất chiếu
     * @param price giá vé
     * @return vé mới hoặc null nếu ghi lỗi
     */
    public Ticket5 themVe(int seatShowtimeId, BigDecimal price) {
        if (!ketNoiSanSang()) {
            return null;
        }
        String sql = "INSERT INTO tblTicket(giaVe,trangThai,tblSeatShowtimeid,tblInvoiceid) "
                + "SELECT ?,'PENDING_PAYMENT',ss.id,NULL FROM tblSeatShowtime ss "
                + "WHERE ss.id=? AND ss.trangThai='LOCKED' AND ss.lockedUntil>NOW()";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBigDecimal(1, price);
            ps.setInt(2, seatShowtimeId);
            if (ps.executeUpdate() != 1) {
                return null;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    return null;
                }
                int id = keys.getInt(1);
                Ticket5 ticket = new Ticket5();
                ticket.setId(id);
                ticket.setMaVe("CM-" + id);
                ticket.setMaQR("CINEMAN:TICKET:" + id);
                ticket.setGiaVe(price);
                ticket.setTrangThai("PENDING_PAYMENT");
                ticket.setSeatShowtimeId(seatShowtimeId);
                return ticket;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deletePendingTickets(ArrayList<Integer> ticketIds) {
        if (!ketNoiSanSang() || ticketIds == null || ticketIds.isEmpty()) {
            return ticketIds != null && ticketIds.isEmpty();
        }
        String sql = "DELETE FROM tblTicket WHERE tblInvoiceid IS NULL "
                + "AND trangThai='PENDING_PAYMENT' AND id IN ("
                + placeholders(ticketIds.size()) + ")";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ganDanhSachId(ps, 1, ticketIds);
            return ps.executeUpdate() == ticketIds.size();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách vé thuộc hóa đơn để in lại.
     * @param invoiceId mã hóa đơn
     * @return danh sách vé
     * // Ứng với bước in vé sau khi thanh toán UC-03.
     */
    public ArrayList<Ticket5> getListTicket(int invoiceId) {
        ArrayList<Ticket5> tickets = new ArrayList<Ticket5>();
        if (!ketNoiSanSang()) {
            return tickets;
        }
        String sql = "SELECT t.id,t.giaVe,t.trangThai,t.tblSeatShowtimeid,"
                + "m.tenPhim,r.tenPhong,s.thoiGianBatDau,se.seatRow,se.seatNumber "
                + "FROM tblTicket t JOIN tblSeatShowtime ss ON ss.id=t.tblSeatShowtimeid "
                + "JOIN tblSeat se ON se.id=ss.tblSeatid "
                + "JOIN tblShowtime s ON s.id=ss.tblShowtimeid "
                + "JOIN tblRoom r ON r.id=s.tblRoomid "
                + "JOIN tblMovieFormat mf ON mf.id=s.tblMovieFormatid "
                + "JOIN tblMovie m ON m.id=mf.tblMovieid WHERE t.tblInvoiceid=? ORDER BY t.id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ticket5 ticket = new Ticket5();
                    int id = rs.getInt("id");
                    ticket.setId(id);
                    ticket.setMaVe("CM-" + id);
                    ticket.setMaQR("CINEMAN:TICKET:" + id);
                    ticket.setGiaVe(rs.getBigDecimal("giaVe"));
                    ticket.setTrangThai(rs.getString("trangThai"));
                    ticket.setSeatShowtimeId(rs.getInt("tblSeatShowtimeid"));
                    ticket.setTenPhim(rs.getString("tenPhim"));
                    ticket.setTenPhong(rs.getString("tenPhong"));
                    ticket.setThoiGianChieu(rs.getTimestamp("thoiGianBatDau").toString());
                    ticket.setGhe(rs.getString("seatRow") + rs.getInt("seatNumber"));
                    tickets.add(ticket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return tickets;
    }

    /**
     * Cập nhật trạng thái vé.
     * @param ticketId mã vé
     * @param status trạng thái vé
     * @param invoiceId hóa đơn cần gắn vào vé
     * @return true nếu cập nhật được đúng một vé
     * // Ứng với bước cập nhật vé sau thanh toán UC-03.
     */
    public boolean capNhatVe(int ticketId, String status, int invoiceId) {
        if (!ketNoiSanSang()) {
            return false;
        }
        String sql = "UPDATE tblTicket SET trangThai=?,tblInvoiceid=? "
                + "WHERE id=? AND tblInvoiceid IS NULL AND trangThai='PENDING_PAYMENT'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, invoiceId);
            ps.setInt(3, ticketId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
