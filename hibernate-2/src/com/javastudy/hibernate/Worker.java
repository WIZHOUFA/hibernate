package com.javastudy.hibernate;

public class Worker {
	private Integer id;
	private String workerName;
	private Pay pay;
	public Worker() {
		super();
	}
	public Worker(String workerName, Pay pay) {
		super();
		this.workerName = workerName;
		this.pay = pay;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getWorkerName() {
		return workerName;
	}
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	public Pay getPay() {
		return pay;
	}
	public void setPay(Pay pay) {
		this.pay = pay;
	}
	@Override
	public String toString() {
		return "Worker [id=" + id + ", workerName=" + workerName + "]";
	}
	
}
