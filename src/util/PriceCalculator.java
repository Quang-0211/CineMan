package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Calendar;

import model.Constants;
import model.SeatShowtime5;
import model.Voucher5;

public final class PriceCalculator {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private PriceCalculator() {
    }

    public static BigDecimal tinhGiaCoBan(SeatShowtime5 seat) {
        BigDecimal total = safe(seat.getGiaVeTieuChuan())
                .add(safe(seat.getPhuThuGhe()))
                .add(safe(seat.getPhuThuPhong()))
                .add(phuThuDinhDang(seat.getTenDinhDang()))
                .add(phuThuKhungGio(seat.getThoiGianBatDau()));
        return total.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal tinhGiamTheoHang(BigDecimal subtotal, String tenHang) {
        BigDecimal discount = subtotal.multiply(Constants.tyLeGiamTheoHang(tenHang));
        return gioiHan(discount, subtotal);
    }

    public static BigDecimal tinhGiamVoucher(Voucher5 voucher, BigDecimal subtotal) {
        if (voucher == null || subtotal.compareTo(safe(voucher.getGiaToiThieu())) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal amount;
        if ("PERCENT".equalsIgnoreCase(voucher.getLoaiGiam())
                || "PHANTRAM".equalsIgnoreCase(voucher.getLoaiGiam())) {
            BigDecimal percent = new BigDecimal(voucher.getMucGiamGia().trim());
            amount = subtotal.multiply(percent).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
        } else {
            amount = new BigDecimal(voucher.getMucGiamGia().trim());
        }
        if (voucher.getGiamToiDa() != null) {
            amount = amount.min(voucher.getGiamToiDa());
        }
        return gioiHan(amount, subtotal);
    }

    private static BigDecimal phuThuDinhDang(String format) {
        if (format == null) {
            return BigDecimal.ZERO;
        }
        if ("IMAX".equalsIgnoreCase(format)) {
            return Constants.PHU_THU_IMAX;
        }
        if ("3D".equalsIgnoreCase(format)) {
            return Constants.PHU_THU_3D;
        }
        return BigDecimal.ZERO;
    }

    private static BigDecimal phuThuKhungGio(Timestamp start) {
        if (start == null) {
            return BigDecimal.ZERO;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(start.getTime());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour >= 18 && hour < 23 ? Constants.PHU_THU_KHUNG_GIO : BigDecimal.ZERO;
    }

    private static BigDecimal gioiHan(BigDecimal discount, BigDecimal subtotal) {
        return safe(discount).max(BigDecimal.ZERO).min(safe(subtotal))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
