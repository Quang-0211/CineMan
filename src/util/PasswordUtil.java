package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class PasswordUtil {
    private PasswordUtil() {
    }

    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                result.append(String.format("%02x", b & 0xff));
            }
            return result.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Không thể băm mật khẩu", e);
        }
    }
}
