package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Showtime5 {
    private int id;
    private Timestamp thoiGianBatDau;
    private Timestamp thoiGianKetThuc;
    private BigDecimal giaVeTieuChuan;
    private String trangThai;
    private int movieId;
    private String tenPhim;
    private int roomId;
    private String tenPhong;
    private String tenDinhDang;
    private BigDecimal roomPhuThu;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Timestamp getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(Timestamp thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }
    public Timestamp getThoiGianKetThuc() { return thoiGianKetThuc; }
    public void setThoiGianKetThuc(Timestamp thoiGianKetThuc) { this.thoiGianKetThuc = thoiGianKetThuc; }
    public BigDecimal getGiaVeTieuChuan() { return giaVeTieuChuan; }
    public void setGiaVeTieuChuan(BigDecimal giaVeTieuChuan) { this.giaVeTieuChuan = giaVeTieuChuan; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    public String getTenPhim() { return tenPhim; }
    public void setTenPhim(String tenPhim) { this.tenPhim = tenPhim; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }
    public String getTenDinhDang() { return tenDinhDang; }
    public void setTenDinhDang(String tenDinhDang) { this.tenDinhDang = tenDinhDang; }
    public BigDecimal getRoomPhuThu() { return roomPhuThu; }
    public void setRoomPhuThu(BigDecimal roomPhuThu) { this.roomPhuThu = roomPhuThu; }
}
