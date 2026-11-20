package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Invoice5 {
    private int id;
    private BigDecimal tongTien = BigDecimal.ZERO;
    private String ppThanhToan;
    private BigDecimal tienGiamGia = BigDecimal.ZERO;
    private BigDecimal thuThucTe = BigDecimal.ZERO;
    private String trangThai;
    private String loaiHoanDon;
    private Integer voucherId;
    private int employeeId;
    private Integer customerId;
    private int memberPlanId;
    private List<Integer> seatShowtimeIds = new ArrayList<Integer>();
    private List<Ticket5> tickets = new ArrayList<Ticket5>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public String getPpThanhToan() { return ppThanhToan; }
    public void setPpThanhToan(String ppThanhToan) { this.ppThanhToan = ppThanhToan; }
    public BigDecimal getTienGiamGia() { return tienGiamGia; }
    public void setTienGiamGia(BigDecimal tienGiamGia) { this.tienGiamGia = tienGiamGia; }
    public BigDecimal getThuThucTe() { return thuThucTe; }
    public void setThuThucTe(BigDecimal thuThucTe) { this.thuThucTe = thuThucTe; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getLoaiHoanDon() { return loaiHoanDon; }
    public void setLoaiHoanDon(String loaiHoanDon) { this.loaiHoanDon = loaiHoanDon; }
    public Integer getVoucherId() { return voucherId; }
    public void setVoucherId(Integer voucherId) { this.voucherId = voucherId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public int getMemberPlanId() { return memberPlanId; }
    public void setMemberPlanId(int memberPlanId) { this.memberPlanId = memberPlanId; }
    public List<Integer> getSeatShowtimeIds() { return seatShowtimeIds; }
    public void setSeatShowtimeIds(List<Integer> seatShowtimeIds) { this.seatShowtimeIds = seatShowtimeIds; }
    public List<Ticket5> getTickets() { return tickets; }
    public void setTickets(List<Ticket5> tickets) { this.tickets = tickets; }
}
