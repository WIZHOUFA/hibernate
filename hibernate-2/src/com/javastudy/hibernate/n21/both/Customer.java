package com.javastudy.hibernate.n21.both;

import java.util.HashSet;
import java.util.Set;

public class Customer {
	private Integer customerID;
	private String customerName;
	//�����ʼ���������ֿ�ָ���쳣
	//����ʹ��Set��ڣ���Ϊ��ȡ����hibernate�����ü���
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
