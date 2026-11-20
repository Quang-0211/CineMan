package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Customer5 extends User5 {
    private Date ngayDangKi;
    private String trangThai;
    private String tenHang;
    private BigDecimal tongChiTieu = BigDecimal.ZERO;

    public Date getNgayDangKi() { return ngayDangKi; }
    public void setNgayDangKi(Date ngayDangKi) { this.ngayDangKi = ngayDangKi; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getTenHang() { return tenHang; }
    public void setTenHang(String tenHang) { this.tenHang = tenHang; }
    public BigDecimal getTongChiTieu() { return tongChiTieu; }
    public void setTongChiTieu(BigDecimal tongChiTieu) { this.tongChiTieu = tongChiTieu; }
}
