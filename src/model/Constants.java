package model;

import java.math.BigDecimal;

public final class Constants {
    public static final int THOI_GIAN_KHOA_GHE_PHUT = 10;
    public static final int THOI_GIAN_OTP_PHUT = 5;
    public static final int SO_LAN_NHAP_OTP_TOI_DA = 5;
    public static final int MAT_KHAU_TOI_THIEU = 8;
    public static final String HANG_BAN_DAU = "Bạc";
    public static final BigDecimal NGUONG_VANG = new BigDecimal("5000000");
    public static final BigDecimal NGUONG_KIM_CUONG = new BigDecimal("15000000");
    public static final BigDecimal PHU_THU_3D = new BigDecimal("15000");
    public static final BigDecimal PHU_THU_IMAX = new BigDecimal("30000");
    public static final BigDecimal PHU_THU_KHUNG_GIO = new BigDecimal("20000");

    private Constants() {
    }

    public static BigDecimal tyLeGiamTheoHang(String tenHang) {
        if ("Kim Cương".equalsIgnoreCase(tenHang)) {
            return new BigDecimal("0.10");
        }
        if ("Vàng".equalsIgnoreCase(tenHang)) {
            return new BigDecimal("0.05");
        }
        return BigDecimal.ZERO;
    }

    public static String xepHang(BigDecimal tongChiTieu) {
        if (tongChiTieu.compareTo(NGUONG_KIM_CUONG) >= 0) {
            return "Kim Cương";
        }
        if (tongChiTieu.compareTo(NGUONG_VANG) >= 0) {
            return "Vàng";
        }
        return "Bạc";
    }
}
