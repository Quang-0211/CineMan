package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Voucher5 {
    private int id;
    private String code;
    private String mucGiamGia;
    private BigDecimal giamToiDa;
    private Date ngayHetHan;
    private BigDecimal giaToiThieu;
    private String loaiGiam;
    private String trangThai;
    private Date ngayBatDau;
    private int soLuong;
    private int membershipPlanId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMucGiamGia() { return mucGiamGia; }
    public void setMucGiamGia(String mucGiamGia) { this.mucGiamGia = mucGiamGia; }
    public BigDecimal getGiamToiDa() { return giamToiDa; }
    public void setGiamToiDa(BigDecimal giamToiDa) { this.giamToiDa = giamToiDa; }
    public Date getNgayHetHan() { return ngayHetHan; }
    public void setNgayHetHan(Date ngayHetHan) { this.ngayHetHan = ngayHetHan; }
    public BigDecimal getGiaToiThieu() { return giaToiThieu; }
    public void setGiaToiThieu(BigDecimal giaToiThieu) { this.giaToiThieu = giaToiThieu; }
    public String getLoaiGiam() { return loaiGiam; }
    public void setLoaiGiam(String loaiGiam) { this.loaiGiam = loaiGiam; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public int getMembershipPlanId() { return membershipPlanId; }
    public void setMembershipPlanId(int membershipPlanId) { this.membershipPlanId = membershipPlanId; }
}
