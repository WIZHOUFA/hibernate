package com.javastudy.hibernate.entities.foreign;

public class Department {
	private Integer deptId;
	private String deptName;
	private Manager mgr;
	public Integer getDetpId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public Manager getMgr() {
		return mgr;
	}
	public void setMgr(Manager mgr) {
		this.mgr = mgr;
	}
	
}
