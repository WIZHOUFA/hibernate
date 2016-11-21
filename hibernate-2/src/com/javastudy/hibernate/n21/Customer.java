package com.javastudy.hibernate.n21;

public class Customer {
	private Integer customerID;
	private String customerName;
	public Customer() {
		super();
	}
	public Customer(String customerName) {
		super();
		this.customerName = customerName;
	}
	public Integer getCustomerID() {
		return customerID;
	}
	public void setCustomerID(Integer customerID) {
		this.customerID = customerID;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	@Override
	public String toString() {
		return "Customer [customerID=" + customerID + ", customerName="
				+ customerName + "]";
	}
	
}
