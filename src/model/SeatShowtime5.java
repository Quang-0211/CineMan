package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class SeatShowtime5 {
    private int id;
    private String trangThai;
    private Timestamp lockedUntil;
    private int seatId;
    private String seatRow;
    private int seatNumber;
    private String loaiGhe;
    private BigDecimal phuThuGhe;
    private BigDecimal giaVeTieuChuan;
    private BigDecimal phuThuPhong;
    private String tenDinhDang;
    private int showtimeId;
    private Timestamp thoiGianBatDau;
    private Timestamp thoiGianKetThuc;
    private String tenPhong;
    private int movieId;
    private String tenPhim;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public Timestamp getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(Timestamp lockedUntil) { this.lockedUntil = lockedUntil; }
    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }
    public String getSeatRow() { return seatRow; }
    public void setSeatRow(String seatRow) { this.seatRow = seatRow; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public String getSeatLabel() { return seatRow + seatNumber; }
    public String getLoaiGhe() { return loaiGhe; }
    public void setLoaiGhe(String loaiGhe) { this.loaiGhe = loaiGhe; }
    public BigDecimal getPhuThuGhe() { return phuThuGhe; }
    public void setPhuThuGhe(BigDecimal phuThuGhe) { this.phuThuGhe = phuThuGhe; }
    public BigDecimal getGiaVeTieuChuan() { return giaVeTieuChuan; }
    public void setGiaVeTieuChuan(BigDecimal giaVeTieuChuan) { this.giaVeTieuChuan = giaVeTieuChuan; }
    public BigDecimal getPhuThuPhong() { return phuThuPhong; }
    public void setPhuThuPhong(BigDecimal phuThuPhong) { this.phuThuPhong = phuThuPhong; }
    public String getTenDinhDang() { return tenDinhDang; }
    public void setTenDinhDang(String tenDinhDang) { this.tenDinhDang = tenDinhDang; }
    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }
    public Timestamp getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(Timestamp thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }
    public Timestamp getThoiGianKetThuc() { return thoiGianKetThuc; }
    public void setThoiGianKetThuc(Timestamp thoiGianKetThuc) { this.thoiGianKetThuc = thoiGianKetThuc; }
    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    public String getTenPhim() { return tenPhim; }
    public void setTenPhim(String tenPhim) { this.tenPhim = tenPhim; }
}
