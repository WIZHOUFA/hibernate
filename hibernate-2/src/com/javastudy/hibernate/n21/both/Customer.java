package com.javastudy.hibernate.n21.both;

import java.util.HashSet;
import java.util.Set;

public class Customer {
	private Integer customerID;
	private String customerName;
	//必须初始化否则会出现空指针异常
	//必须使用Set借口，因为获取后是hibernate的内置集合
	//org.hibernate.collection.internal.PersistentSet
	private Set<Order> orders=new HashSet<Order>();
	
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
	
	public Set<Order> getOrders() {
		return orders;
	}
	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}
	
	@Override
	public String toString() {
		return "Customer [customerID=" + customerID + ", customerName="
				+ customerName + "]";
	}
	
}
