package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Invoice5;
import model.SeatShowtime5;
import model.Ticket5;
import model.Voucher5;
import util.PriceCalculator;

public class InvoiceDAO extends DAO {
    private String lastError;

    /**
     * Tạo hóa đơn và chuẩn bị danh sách vé cho bên gọi lưu.
     * @param invoice dữ liệu thanh toán và danh sách ghế đã khóa
     * @return true nếu hóa đơn được tạo thành công
     */
    public boolean themHoaDon(Invoice5 invoice) {
        lastError = null;
        if (!ketNoiSanSang() || invoice == null || invoice.getSeatShowtimeIds() == null
                || invoice.getSeatShowtimeIds().isEmpty()) {
            lastError = "Không có ghế được chọn.";
            return false;
        }
        Set<Integer> ids = new LinkedHashSet<Integer>(invoice.getSeatShowtimeIds());
        if (ids.contains(null) || ids.size() != invoice.getSeatShowtimeIds().size()) {
            lastError = "Danh sách ghế không hợp lệ.";
            return false;
        }
        try {
            List<SeatShowtime5> seats = docGheDaChon(ids);
            if (seats == null) {
                return false;
            }
            BigDecimal subtotal = tinhTongGia(seats);
            String memberTier = layHangThanhVien(invoice.getCustomerId());
            BigDecimal memberDiscount = PriceCalculator.tinhGiamTheoHang(subtotal, memberTier);
            BigDecimal afterMember = subtotal.subtract(memberDiscount);

            Voucher5 voucher = invoice.getVoucherId() == null ? null : docVoucher(invoice.getVoucherId());
            if (invoice.getVoucherId() != null && voucher == null) {
                lastError = "Voucher không hợp lệ, hết hạn hoặc đã hết lượt.";
                return false;
            }
            BigDecimal voucherDiscount = voucher == null
                    ? BigDecimal.ZERO : PriceCalculator.tinhGiamVoucher(voucher, afterMember);
            if (voucher != null && voucherDiscount.signum() == 0
                    && afterMember.compareTo(voucher.getGiaToiThieu()) < 0) {
                lastError = "Hóa đơn chưa đạt giá trị tối thiểu của voucher.";
                return false;
            }
            BigDecimal paid = afterMember.subtract(voucherDiscount).max(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);

            invoice.setTongTien(subtotal);
            invoice.setTienGiamGia(memberDiscount.add(voucherDiscount));
            invoice.setThuThucTe(paid);
            invoice.setTrangThai("PAID");
            if (!insertHoaDon(invoice)) {
                lastError = "Không lưu được hóa đơn.";
                return false;
            }

            List<Ticket5> tickets = taoDanhSachVe(seats, paid, subtotal);
            if (tickets == null) {
                lastError = "Không tạo được danh sách vé.";
                return false;
            }
            invoice.setTickets(tickets);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            lastError = "Lỗi khi lưu hóa đơn.";
            return false;
        }
    }

    /**
     * Cập nhật lượt voucher và chi tiêu thành viên sau khi vé và ghế đã được cập nhật.
     * @param invoice hóa đơn đã tạo
     * @return true nếu các cập nhật thành công
     */
    public boolean completeInvoice(Invoice5 invoice) {
        lastError = null;
        try {
            if (invoice == null) {
                lastError = "Không có hóa đơn để hoàn tất.";
                return false;
            }
            if (invoice.getVoucherId() != null && !truVoucher(invoice.getVoucherId())) {
                lastError = "Voucher vừa hết lượt sử dụng, vui lòng chọn lại.";
                return false;
            }
            if (invoice.getCustomerId() != null
                    && !new CustomerDAO().congChiTieuNam(invoice.getCustomerId(), invoice.getThuThucTe())) {
                lastError = "Không cập nhật được chi tiêu thành viên.";
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            lastError = "Lỗi khi cập nhật voucher hoặc chi tiêu thành viên.";
            return false;
        }
    }

    public String getLastError() {
        return lastError;
    }

    private List<SeatShowtime5> docGheDaChon(Set<Integer> ids) throws Exception {
        String sql = "SELECT ss.id,ss.trangThai,ss.lockedUntil,ss.tblShowtimeid,"
                + "s.giaVeTieuChuan,s.thoiGianBatDau,st.phuThu AS phuThuGhe,st.loaiGhe,"
                + "rt.price AS phuThuPhong,f.tenDinhDang,m.tenPhim,r.tenPhong,"
                + "se.seatRow,se.seatNumber "
                + "FROM tblSeatShowtime ss JOIN tblSeat se ON se.id=ss.tblSeatid "
                + "JOIN tblSeatType st ON st.id=se.tblSeatTypeid "
                + "JOIN tblShowtime s ON s.id=ss.tblShowtimeid "
                + "JOIN tblRoom r ON r.id=s.tblRoomid "
                + "JOIN tblRoomType rt ON rt.id=r.tblRoomTypeid "
                + "JOIN tblMovieFormat mf ON mf.id=s.tblMovieFormatid "
                + "JOIN tblFormat f ON f.id=mf.tblFormatid "
                + "JOIN tblMovie m ON m.id=mf.tblMovieid "
                + "WHERE ss.id IN (" + placeholders(ids.size()) + ") ORDER BY ss.id";
        Map<Integer, SeatShowtime5> found = new LinkedHashMap<Integer, SeatShowtime5>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ganDanhSachId(ps, 1, ids);
            try (ResultSet rs = ps.executeQuery()) {
                Integer showtimeId = null;
                while (rs.next()) {
                    Timestamp lockedUntil = rs.getTimestamp("lockedUntil");
                    if (!"LOCKED".equalsIgnoreCase(rs.getString("trangThai"))
                            || lockedUntil == null || lockedUntil.getTime() <= System.currentTimeMillis()) {
                        lastError = "Ghế đã được bán, vui lòng chọn lại.";
                        return null;
                    }
                    int currentShowtime = rs.getInt("tblShowtimeid");
                    if (showtimeId != null && showtimeId.intValue() != currentShowtime) {
                        lastError = "Các ghế phải thuộc cùng một suất chiếu.";
                        return null;
                    }
                    showtimeId = currentShowtime;
                    SeatShowtime5 seat = new SeatShowtime5();
                    seat.setId(rs.getInt("id"));
                    seat.setShowtimeId(currentShowtime);
                    seat.setGiaVeTieuChuan(rs.getBigDecimal("giaVeTieuChuan"));
                    seat.setThoiGianBatDau(rs.getTimestamp("thoiGianBatDau"));
                    seat.setPhuThuGhe(rs.getBigDecimal("phuThuGhe"));
                    seat.setPhuThuPhong(rs.getBigDecimal("phuThuPhong"));
                    seat.setTenDinhDang(rs.getString("tenDinhDang"));
                    seat.setLoaiGhe(rs.getString("loaiGhe"));
                    seat.setTenPhim(rs.getString("tenPhim"));
                    seat.setTenPhong(rs.getString("tenPhong"));
                    seat.setSeatRow(rs.getString("seatRow"));
                    seat.setSeatNumber(rs.getInt("seatNumber"));
                    found.put(seat.getId(), seat);
                }
            }
        }
        if (found.size() != ids.size()) {
            lastError = "Ghế đã được bán, vui lòng chọn lại.";
            return null;
        }
        return new ArrayList<SeatShowtime5>(found.values());
    }

    private BigDecimal tinhTongGia(List<SeatShowtime5> seats) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (SeatShowtime5 seat : seats) {
            subtotal = subtotal.add(PriceCalculator.tinhGiaCoBan(seat));
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    private String layHangThanhVien(Integer customerId) throws Exception {
        if (customerId == null) {
            return null;
        }
        String sql = "SELECT p.tenGoi FROM tblMemberYearlySpending mys "
                + "JOIN tblMembershipPlan p ON p.id=mys.tblMembershipPlanid "
                + "WHERE mys.tblCustomerid=? AND mys.year=YEAR(CURDATE())";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("tenGoi") : null;
            }
        }
    }

    private Voucher5 docVoucher(int voucherId) throws Exception {
        String sql = "SELECT id,code,mucGiamGia,giamToiDa,ngayHetHan,giaToiThieu,"
                + "loaiGiam,trangThai,ngayBatDau,soLuong,tblMembershipPlanid "
                + "FROM tblVoucher WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
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
                LocalDate now = LocalDate.now();
                if (!"ACTIVE".equalsIgnoreCase(voucher.getTrangThai()) || voucher.getSoLuong() <= 0
                        || voucher.getNgayBatDau() == null || voucher.getNgayHetHan() == null
                        || now.isBefore(voucher.getNgayBatDau().toLocalDate())
                        || now.isAfter(voucher.getNgayHetHan().toLocalDate())) {
                    return null;
                }
                return voucher;
            }
        }
    }

    private boolean insertHoaDon(Invoice5 invoice) throws Exception {
        String sql = "INSERT INTO tblInvoice(tongTien,ppThanhToan,tienGiamGia,thuThucTe,"
                + "trangThai,loaiHoanDon,tblVoucherid,tblEmployeeid,tblCustomerid) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBigDecimal(1, invoice.getTongTien());
            ps.setString(2, invoice.getPpThanhToan());
            ps.setBigDecimal(3, invoice.getTienGiamGia());
            ps.setBigDecimal(4, invoice.getThuThucTe());
            ps.setString(5, invoice.getTrangThai());
            ps.setString(6, invoice.getLoaiHoanDon());
            if (invoice.getVoucherId() == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, invoice.getVoucherId());
            }
            ps.setInt(8, invoice.getEmployeeId());
            if (invoice.getCustomerId() == null) {
                ps.setNull(9, java.sql.Types.INTEGER);
            } else {
                ps.setInt(9, invoice.getCustomerId());
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    return false;
                }
                invoice.setId(keys.getInt(1));
                return true;
            }
        }
    }

    private List<Ticket5> taoDanhSachVe(List<SeatShowtime5> seats,
            BigDecimal paid, BigDecimal subtotal) {
        List<Ticket5> tickets = new ArrayList<Ticket5>();
        BigDecimal allocated = BigDecimal.ZERO;
        for (int i = 0; i < seats.size(); i++) {
            SeatShowtime5 seat = seats.get(i);
            BigDecimal ticketPrice;
            if (i == seats.size() - 1) {
                ticketPrice = paid.subtract(allocated);
            } else if (subtotal.signum() == 0) {
                ticketPrice = BigDecimal.ZERO;
            } else {
                ticketPrice = PriceCalculator.tinhGiaCoBan(seat).multiply(paid)
                        .divide(subtotal, 2, RoundingMode.HALF_UP);
                allocated = allocated.add(ticketPrice);
            }
            Ticket5 ticket = new Ticket5();
            ticket.setGiaVe(ticketPrice);
            ticket.setSeatShowtimeId(seat.getId());
            ticket.setTrangThai("PENDING_PAYMENT");
            ticket.setTenPhim(seat.getTenPhim());
            ticket.setTenPhong(seat.getTenPhong());
            ticket.setThoiGianChieu(seat.getThoiGianBatDau().toString());
            ticket.setGhe(seat.getSeatRow() + seat.getSeatNumber());
            tickets.add(ticket);
        }
        return tickets;
    }

    private boolean truVoucher(int voucherId) throws Exception {
        String sql = "UPDATE tblVoucher SET soLuong=soLuong-1 "
                + "WHERE id=? AND trangThai='ACTIVE' AND soLuong>0 "
                + "AND ngayBatDau<=CURDATE() AND ngayHetHan>=CURDATE()";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            return ps.executeUpdate() == 1;
        }
    }

}
