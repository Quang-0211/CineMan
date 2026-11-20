USE cineman;

INSERT INTO tblUser (id, username, hoTen, email, sdt, dob, password) VALUES
(2, 'nvbh', 'Nhân viên bán hàng', 'nvbh@cineman.local', '0900000002', '1990-01-01', SHA2('nvbh12345', 256)),
(3, 'thanhvienvang', 'Thành viên Vàng', 'gold@cineman.local', '0900000003', '1995-05-10', SHA2('khach12345', 256)),
(4, 'thanhviendiamond', 'Thành viên Kim Cương', 'diamond@cineman.local', '0900000004', '1992-08-20', SHA2('khach12345', 256));

INSERT INTO tblCustomer (tblUserid, ngayDangKi, trangThai) VALUES
(3, CURDATE(), 'ACTIVE'),
(4, CURDATE(), 'ACTIVE');

INSERT INTO tblEmployee (tblUserid, chucVu) VALUES (2, 'SaleStaff');

INSERT INTO tblMembershipPlan (id, tenGoi, moTa, dieuKien) VALUES
(1, 'Bạc', 'Hạng thành viên ban đầu - giá trị demo', '0 VND chi tiêu năm'),
(2, 'Vàng', 'Hạng thành viên - giá trị demo', '5,000,000 VND chi tiêu năm'),
(3, 'Kim Cương', 'Hạng thành viên - giá trị demo', '15,000,000 VND chi tiêu năm');

INSERT INTO tblMemberYearlySpending (year, tongChiTieu, tblCustomerid, tblMembershipPlanid) VALUES
(YEAR(CURDATE()), 7500000, 3, 2),
(YEAR(CURDATE()), 18000000, 4, 3);

INSERT INTO tblRoomType (id, name, description, price) VALUES
(1, 'Thường', 'Phòng chiếu tiêu chuẩn', 0),
(2, 'IMAX', 'Phòng chiếu IMAX; phụ thu demo', 20000);

INSERT INTO tblRoom (id, tenPhong, sucChua, tblRoomTypeid) VALUES
(1, 'Phòng 01', 24, 1),
(2, 'Phòng IMAX', 24, 2);

INSERT INTO tblSeatType (id, loaiGhe, phuThu, moTa) VALUES
(1, 'Thường', 0, 'Ghế thường'),
(2, 'VIP', 30000, 'Phụ thu ghế VIP demo'),
(3, 'Đôi', 50000, 'Phụ thu ghế đôi demo');

INSERT INTO tblSeat (seatRow, seatNumber, tblRoomid, tblSeatTypeid)
SELECT 
    seat_rows.rowName, 
    nums.seatNo, 
    rooms.roomId,
    CASE 
        WHEN seat_rows.rowName IN ('C', 'D') AND nums.seatNo <= 3 THEN 2
        WHEN seat_rows.rowName = 'E' AND nums.seatNo IN (1, 3) THEN 3
        ELSE 1
    END
FROM (
    SELECT 1 AS roomId UNION ALL SELECT 2
) rooms
CROSS JOIN (
    SELECT 'A' AS rowName UNION ALL SELECT 'B' UNION ALL SELECT 'C' UNION ALL SELECT 'D' UNION ALL SELECT 'E'
) seat_rows
CROSS JOIN (
    SELECT 1 AS seatNo UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
) nums;


INSERT INTO tblFormat (id, tenDinhDang, moTa) VALUES
(1, '2D', 'Định dạng 2D'),
(2, 'IMAX', 'Định dạng IMAX');

INSERT INTO tblCompatibility (tblRoomid, tblFormatid, isTuongThich) VALUES
(1, 1, TRUE),
(2, 1, TRUE),
(2, 2, TRUE);

INSERT INTO tblGenre (id, genreName, description) VALUES
(1, 'Khoa học viễn tưởng', 'Phim khoa học viễn tưởng'),
(2, 'Hành động', 'Phim hành động'),
(3, 'Hoạt hình', 'Phim hoạt hình');

INSERT INTO tblProducer (id, producerName, country, description) VALUES
(1, 'CineMan Studio A', 'Việt Nam', 'Nhà sản xuất mẫu A'),
(2, 'CineMan Studio B', 'Việt Nam', 'Nhà sản xuất mẫu B'),
(3, 'CineMan Studio C', 'Việt Nam', 'Nhà sản xuất mẫu C');

INSERT INTO tblMovie (id, tenPhim, thoiGianChieu, nhanTuoi, trangThai, ngayKhoiChieu, ngayKetThuc, moTa) VALUES
(1, 'Avatar 3', 165, 'T13', 'ACTIVE', DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Phim mẫu khoa học viễn tưởng.'),
(2, 'Hành Trình Cuối', 120, 'T16', 'ACTIVE', DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'Phim mẫu hành động.'),
(3, 'Thế Giới Kỳ Diệu', 95, 'P', 'ACTIVE', DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'Phim mẫu hoạt hình.');

INSERT INTO tblMovieGenre (tblMovieid, tblGenreid) VALUES
(1, 1), (2, 2), (3, 3);

INSERT INTO tblMovieProducer (tblMovieid, tblProducerid) VALUES
(1, 1), (2, 2), (3, 3);

INSERT INTO tblMovieFormat (id, tblMovieid, tblFormatid) VALUES
(1, 1, 1), (2, 1, 2), (3, 2, 1), (4, 2, 2), (5, 3, 1);

INSERT INTO tblShowtime
    (id, thoiGianBatDau, thoiGianKetThuc, giaVeTieuChuan, trangThai, tblRoomid, tblMovieFormatid)
VALUES
(1, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 2 DAY), '14:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 2 DAY), '16:45:00'), 90000, 'ACTIVE', 1, 1),
(2, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '19:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '21:00:00'), 100000, 'ACTIVE', 2, 4),
(3, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '16:45:00'), 90000, 'ACTIVE', 1, 1),
(4, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '21:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '23:45:00'), 120000, 'ACTIVE', 2, 2),
(5, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 2 DAY), '18:30:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 2 DAY), '20:30:00'), 100000, 'ACTIVE', 1, 3),
(6, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY), '16:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY), '17:35:00'), 80000, 'ACTIVE', 2, 5);

INSERT INTO tblSeatShowtime (tblSeatid, tblShowtimeid, trangThai, lockedUntil)
SELECT s.id, st.id,
       CASE WHEN st.id IN (1, 2) AND s.id % 7 = 0 THEN 'SOLD' ELSE 'AVAILABLE' END,
       NULL
FROM tblSeat s
CROSS JOIN tblShowtime st;

INSERT INTO tblVoucher
    (id, code, mucGiamGia, giamToiDa, ngayHetHan, giaToiThieu, loaiGiam, trangThai, ngayBatDau, soLuong, tblMembershipPlanid)
VALUES
(1, 'WELCOME10', '10', 50000, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 0, 'PERCENT', 'ACTIVE', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 100, 1),
(2, 'EXPIRED20', '20', 100000, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 0, 'PERCENT', 'ACTIVE', DATE_SUB(CURDATE(), INTERVAL 60 DAY), 0, 1);
