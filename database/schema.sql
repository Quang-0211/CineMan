CREATE DATABASE IF NOT EXISTS cineman CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cineman;

CREATE TABLE IF NOT EXISTS tblUser (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    hoTen VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    sdt VARCHAR(15) NOT NULL,
    dob DATE NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblUser_username (username),
    UNIQUE KEY uq_tblUser_email (email),
    UNIQUE KEY uq_tblUser_sdt (sdt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblCustomer (
    tblUserid INT NOT NULL,
    ngayDangKi DATE NULL,
    trangThai VARCHAR(30) NOT NULL,
    PRIMARY KEY (tblUserid),
    CONSTRAINT fk_tblCustomer_tblUserid FOREIGN KEY (tblUserid) REFERENCES tblUser(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblEmployee (
    tblUserid INT NOT NULL,
    chucVu VARCHAR(50) NOT NULL,
    PRIMARY KEY (tblUserid),
    CONSTRAINT fk_tblEmployee_tblUserid FOREIGN KEY (tblUserid) REFERENCES tblUser(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblOtpVerification (
    id INT NOT NULL AUTO_INCREMENT,
    code VARCHAR(10) NOT NULL,
    expired_at DATETIME NOT NULL,
    soLanSai INT NOT NULL DEFAULT 0,
    tblCustomerid INT NOT NULL,
    PRIMARY KEY (id),
    KEY ix_tblOtpVerification_customer (tblCustomerid),
    CONSTRAINT fk_tblOtpVerification_tblCustomerid
        FOREIGN KEY (tblCustomerid) REFERENCES tblCustomer(tblUserid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblMembershipPlan (
    id INT NOT NULL AUTO_INCREMENT,
    tenGoi VARCHAR(50) NOT NULL,
    moTa TEXT NULL,
    dieuKien VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblMembershipPlan_tenGoi (tenGoi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblMemberYearlySpending (
    id INT NOT NULL AUTO_INCREMENT,
    year INT NOT NULL,
    tongChiTieu DECIMAL(15,2) NOT NULL DEFAULT 0,
    tblCustomerid INT NOT NULL,
    tblMembershipPlanid INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblMemberYearlySpending (tblCustomerid, year),
    CONSTRAINT fk_tblMemberYearlySpending_tblCustomerid
        FOREIGN KEY (tblCustomerid) REFERENCES tblCustomer(tblUserid),
    CONSTRAINT fk_tblMemberYearlySpending_tblMembershipPlanid
        FOREIGN KEY (tblMembershipPlanid) REFERENCES tblMembershipPlan(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblVoucher (
    id INT NOT NULL AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL,
    mucGiamGia VARCHAR(50) NOT NULL,
    giamToiDa DECIMAL(12,2) NULL,
    ngayHetHan DATE NOT NULL,
    giaToiThieu DECIMAL(12,2) NOT NULL DEFAULT 0,
    loaiGiam VARCHAR(30) NOT NULL,
    trangThai VARCHAR(30) NOT NULL,
    ngayBatDau DATE NOT NULL,
    soLuong INT NOT NULL,
    tblMembershipPlanid INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblVoucher_code (code),
    CONSTRAINT fk_tblVoucher_tblMembershipPlanid
        FOREIGN KEY (tblMembershipPlanid) REFERENCES tblMembershipPlan(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblRoomType (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    price DECIMAL(12,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblRoom (
    id INT NOT NULL AUTO_INCREMENT,
    tenPhong VARCHAR(50) NOT NULL,
    sucChua INT NOT NULL,
    tblRoomTypeid INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_tblRoom_tblRoomTypeid FOREIGN KEY (tblRoomTypeid) REFERENCES tblRoomType(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblSeatType (
    id INT NOT NULL AUTO_INCREMENT,
    loaiGhe VARCHAR(50) NOT NULL,
    phuThu DECIMAL(12,2) NOT NULL DEFAULT 0,
    moTa VARCHAR(255) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblSeat (
    id INT NOT NULL AUTO_INCREMENT,
    seatRow VARCHAR(5) NOT NULL,
    seatNumber INT NOT NULL,
    tblRoomid INT NOT NULL,
    tblSeatTypeid INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblSeat (tblRoomid, seatRow, seatNumber),
    CONSTRAINT fk_tblSeat_tblRoomid FOREIGN KEY (tblRoomid) REFERENCES tblRoom(id),
    CONSTRAINT fk_tblSeat_tblSeatTypeid FOREIGN KEY (tblSeatTypeid) REFERENCES tblSeatType(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblFormat (
    id INT NOT NULL AUTO_INCREMENT,
    tenDinhDang VARCHAR(50) NOT NULL,
    moTa VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblFormat_tenDinhDang (tenDinhDang)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblCompatibility (
    id INT NOT NULL AUTO_INCREMENT,
    isTuongThich BOOLEAN NOT NULL DEFAULT TRUE,
    tblRoomid INT NOT NULL,
    tblFormatid INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblCompatibility (tblRoomid, tblFormatid),
    CONSTRAINT fk_tblCompatibility_tblRoomid FOREIGN KEY (tblRoomid) REFERENCES tblRoom(id),
    CONSTRAINT fk_tblCompatibility_tblFormatid FOREIGN KEY (tblFormatid) REFERENCES tblFormat(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblMovie (
    id INT NOT NULL AUTO_INCREMENT,
    tenPhim VARCHAR(255) NOT NULL,
    thoiGianChieu INT NULL COMMENT 'Thoi luong phim, don vi phut',
    nhanTuoi VARCHAR(10) NULL,
    trangThai VARCHAR(30) NOT NULL,
    ngayKhoiChieu DATE NULL,
    ngayKetThuc DATE NULL,
    moTa TEXT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblGenre (
    id INT NOT NULL AUTO_INCREMENT,
    genreName VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblGenre_genreName (genreName)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblMovieGenre (
    id INT NOT NULL AUTO_INCREMENT,
    tblMovieid INT NOT NULL,
    tblGenreid INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblMovieGenre (tblMovieid, tblGenreid),
    CONSTRAINT fk_tblMovieGenre_tblMovieid FOREIGN KEY (tblMovieid) REFERENCES tblMovie(id),
    CONSTRAINT fk_tblMovieGenre_tblGenreid FOREIGN KEY (tblGenreid) REFERENCES tblGenre(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblProducer (
    id INT NOT NULL AUTO_INCREMENT,
    producerName VARCHAR(100) NOT NULL,
    country VARCHAR(50) NULL,
    description TEXT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblMovieProducer (
    id INT NOT NULL AUTO_INCREMENT,
    tblMovieid INT NOT NULL,
    tblProducerid INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblMovieProducer (tblMovieid, tblProducerid),
    CONSTRAINT fk_tblMovieProducer_tblMovieid FOREIGN KEY (tblMovieid) REFERENCES tblMovie(id),
    CONSTRAINT fk_tblMovieProducer_tblProducerid FOREIGN KEY (tblProducerid) REFERENCES tblProducer(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblMovieFormat (
    id INT NOT NULL AUTO_INCREMENT,
    tblMovieid INT NOT NULL,
    tblFormatid INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblMovieFormat (tblMovieid, tblFormatid),
    CONSTRAINT fk_tblMovieFormat_tblMovieid FOREIGN KEY (tblMovieid) REFERENCES tblMovie(id),
    CONSTRAINT fk_tblMovieFormat_tblFormatid FOREIGN KEY (tblFormatid) REFERENCES tblFormat(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblShowtime (
    id INT NOT NULL AUTO_INCREMENT,
    thoiGianBatDau DATETIME NOT NULL,
    thoiGianKetThuc DATETIME NOT NULL,
    giaVeTieuChuan DECIMAL(12,2) NOT NULL,
    trangThai VARCHAR(30) NOT NULL,
    tblRoomid INT NOT NULL,
    tblMovieFormatid INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_tblShowtime_tblRoomid FOREIGN KEY (tblRoomid) REFERENCES tblRoom(id),
    CONSTRAINT fk_tblShowtime_tblMovieFormatid FOREIGN KEY (tblMovieFormatid) REFERENCES tblMovieFormat(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblSeatShowtime (
    id INT NOT NULL AUTO_INCREMENT,
    trangThai VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    lockedUntil DATETIME NULL,
    tblSeatid INT NOT NULL,
    tblShowtimeid INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblSeatShowtime (tblSeatid, tblShowtimeid),
    KEY ix_tblSeatShowtime_status (tblShowtimeid, trangThai, lockedUntil),
    CONSTRAINT fk_tblSeatShowtime_tblSeatid FOREIGN KEY (tblSeatid) REFERENCES tblSeat(id),
    CONSTRAINT fk_tblSeatShowtime_tblShowtimeid FOREIGN KEY (tblShowtimeid) REFERENCES tblShowtime(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblInvoice (
    id INT NOT NULL AUTO_INCREMENT,
    tongTien DECIMAL(12,2) NOT NULL,
    ppThanhToan VARCHAR(50) NOT NULL,
    tienGiamGia DECIMAL(12,2) NOT NULL DEFAULT 0,
    thuThucTe DECIMAL(12,2) NOT NULL,
    trangThai VARCHAR(30) NOT NULL,
    loaiHoanDon VARCHAR(50) NULL,
    tblVoucherid INT NULL,
    tblEmployeeid INT NOT NULL,
    tblCustomerid INT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_tblInvoice_tblVoucherid FOREIGN KEY (tblVoucherid) REFERENCES tblVoucher(id),
    CONSTRAINT fk_tblInvoice_tblEmployeeid FOREIGN KEY (tblEmployeeid) REFERENCES tblEmployee(tblUserid),
    CONSTRAINT fk_tblInvoice_tblCustomerid FOREIGN KEY (tblCustomerid) REFERENCES tblCustomer(tblUserid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblTicket (
    id INT NOT NULL AUTO_INCREMENT,
    giaVe DECIMAL(12,2) NOT NULL,
    trangThai VARCHAR(30) NOT NULL,
    tblSeatShowtimeid INT NOT NULL,
    tblInvoiceid INT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblTicket_tblSeatShowtimeid (tblSeatShowtimeid),
    CONSTRAINT fk_tblTicket_tblSeatShowtimeid FOREIGN KEY (tblSeatShowtimeid) REFERENCES tblSeatShowtime(id),
    CONSTRAINT fk_tblTicket_tblInvoiceid FOREIGN KEY (tblInvoiceid) REFERENCES tblInvoice(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblReview (
    id INT NOT NULL AUTO_INCREMENT,
    diemSo DECIMAL(3,1) NULL,
    danhGia TEXT NULL,
    thoiGianTao DATETIME NULL,
    tblCustomerid INT NOT NULL,
    tblMovieid INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblReview (tblCustomerid, tblMovieid),
    CONSTRAINT fk_tblReview_tblCustomerid FOREIGN KEY (tblCustomerid) REFERENCES tblCustomer(tblUserid),
    CONSTRAINT fk_tblReview_tblMovieid FOREIGN KEY (tblMovieid) REFERENCES tblMovie(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblPerson (
    id INT NOT NULL AUTO_INCREMENT,
    fullname VARCHAR(100) NULL,
    dateOfBirth DATE NULL,
    nationality VARCHAR(50) NULL,
    biography TEXT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblMoviePerson (
    id INT NOT NULL AUTO_INCREMENT,
    role VARCHAR(50) NULL,
    characterName VARCHAR(100) NULL,
    tblMovieid INT NOT NULL,
    tblPersonid INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tblMoviePerson (tblMovieid, tblPersonid, role),
    CONSTRAINT fk_tblMoviePerson_tblMovieid FOREIGN KEY (tblMovieid) REFERENCES tblMovie(id),
    CONSTRAINT fk_tblMoviePerson_tblPersonid FOREIGN KEY (tblPersonid) REFERENCES tblPerson(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
