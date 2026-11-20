package model;

import java.sql.Date;

public class User5 {
    private int id;
    private String username;
    private String hoTen;
    private String email;
    private String sdt;
    private Date dob;
    private String password;
    private String vaiTro;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
}
