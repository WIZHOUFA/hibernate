package com.javastudy.hibernate;

public class Pay {
	private Integer monthlyPay;
	private Integer yearlyPay;
	private Integer vocationPay;
	public Pay() {
		super();
	}
	public Pay(Integer monthlyPay, Integer yearlyPay, Integer vocationPay) {
		super();
		this.monthlyPay = monthlyPay;
		this.yearlyPay = yearlyPay;
		this.vocationPay = vocationPay;
	}
	public Integer getMonthlyPay() {
		return monthlyPay;
	}
	public void setMonthlyPay(Integer monthlyPay) {
		this.monthlyPay = monthlyPay;
	}
	public Integer getYearlyPay() {
		return yearlyPay;
	}
	public void setYearlyPay(Integer yearlyPay) {
		this.yearlyPay = yearlyPay;
	}
	public Integer getVocationPay() {
		return vocationPay;
	}
	public void setVocationPay(Integer vocationPay) {
		this.vocationPay = vocationPay;
	}
	
}
