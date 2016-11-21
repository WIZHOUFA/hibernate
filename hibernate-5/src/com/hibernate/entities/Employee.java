package com.hibernate.entities;

public class Employee {
	private Integer id;
	private String name;
	private float salary;
	private String email;
	
	private Department dept;
    
	
	
	public Employee() {
		super();
	}

	public Employee(Integer id, float salary, String email) {
		super();
		this.id = id;
		this.salary = salary;
		this.email = email;
	}
    
	public Employee(Integer id, float salary, String email, Department dept) {
		super();
		this.id = id;
		this.salary = salary;
		this.email = email;
		this.dept = dept;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getSalary() {
		return salary;
	}

	public void setSalary(float salary) {
		this.salary = salary;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Department getDept() {
		return dept;
	}

	public void setDept(Department dept) {
		this.dept = dept;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", salary=" + salary
				+ ", email=" + email + ", dept=" + dept + "]";
	}
	
	
}
