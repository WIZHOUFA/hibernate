package com.javastudy.hibernate.n21.both;

public class Order {
	private Integer orderID;
	private String orderName;
	
	private Customer customer;

	
	public Order() {
		super();
	}


	public Order(String orderName, Customer customer) {
		super();
		this.orderName = orderName;
		this.customer = customer;
	}


	public Integer getOrderID() {
		return orderID;
	}


	public void setOrderID(Integer orderID) {
		this.orderID = orderID;
	}


	public String getOrderName() {
		return orderName;
	}


	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}


	public Customer getCustomer() {
		return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
	}


	@Override
	public String toString() {
		return "Order [orderID=" + orderID + ", orderName=" + orderName
				+ ", customer=" + customer + "]";
	}
	
	
}
