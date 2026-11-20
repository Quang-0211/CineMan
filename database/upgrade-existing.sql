USE cineman;

-- Run once only against the existing database after checking the columns manually.
ALTER TABLE tblVoucher
    ADD COLUMN code VARCHAR(50) NULL AFTER id;
ALTER TABLE tblVoucher
    ADD UNIQUE KEY uq_tblVoucher_code (code);

ALTER TABLE tblUser
    ADD COLUMN hoTen VARCHAR(100) NULL AFTER username;

ALTER TABLE tblOtpVerification
    ADD COLUMN soLanSai INT NOT NULL DEFAULT 0 AFTER expired_at;

ALTER TABLE tblSeatShowtime
    ADD COLUMN lockedUntil DATETIME NULL AFTER trangThai,
    ADD KEY ix_tblSeatShowtime_status (tblShowtimeid, trangThai, lockedUntil);

ALTER TABLE tblMovie
    MODIFY COLUMN thoiGianChieu INT NULL COMMENT 'Thoi luong phim, don vi phut';

ALTER TABLE tblTicket
    MODIFY COLUMN tblInvoiceid INT NULL;

ALTER TABLE tblInvoice
    MODIFY COLUMN tblCustomerid INT NULL;

UPDATE tblInvoice i
JOIN tblUser u ON u.id = i.tblCustomerid
SET i.tblCustomerid = NULL
WHERE u.username = 'khachvanglai';

DELETE c FROM tblCustomer c
JOIN tblUser u ON u.id = c.tblUserid
WHERE u.username = 'khachvanglai'
  AND NOT EXISTS (SELECT 1 FROM tblOtpVerification o WHERE o.tblCustomerid = c.tblUserid)
  AND NOT EXISTS (SELECT 1 FROM tblMemberYearlySpending s WHERE s.tblCustomerid = c.tblUserid)
  AND NOT EXISTS (SELECT 1 FROM tblReview r WHERE r.tblCustomerid = c.tblUserid);

DELETE u FROM tblUser u
LEFT JOIN tblCustomer c ON c.tblUserid = u.id
LEFT JOIN tblEmployee e ON e.tblUserid = u.id
WHERE u.username = 'khachvanglai'
  AND c.tblUserid IS NULL
  AND e.tblUserid IS NULL;
