package model;

import java.math.BigDecimal;

public class MemberYearlySpending5 {
    private int id;
    private int year;
    private BigDecimal tongChiTieu;
    private MembershipPlan5 membershipPlan;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public BigDecimal getTongChiTieu() { return tongChiTieu; }
    public void setTongChiTieu(BigDecimal tongChiTieu) { this.tongChiTieu = tongChiTieu; }
    public MembershipPlan5 getMembershipPlan() { return membershipPlan; }
    public void setMembershipPlan(MembershipPlan5 membershipPlan) { this.membershipPlan = membershipPlan; }
}
