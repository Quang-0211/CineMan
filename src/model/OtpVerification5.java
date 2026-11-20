package model;

import java.sql.Timestamp;

public class OtpVerification5 {
    private int id;
    private int customerId;
    private String code;
    private Timestamp expiredAt;
    private int soLanSai;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Timestamp getExpiredAt() { return expiredAt; }
    public void setExpiredAt(Timestamp expiredAt) { this.expiredAt = expiredAt; }
    public int getSoLanSai() { return soLanSai; }
    public void setSoLanSai(int soLanSai) { this.soLanSai = soLanSai; }
}
