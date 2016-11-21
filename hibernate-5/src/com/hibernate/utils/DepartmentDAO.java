package com.hibernate.utils;

import org.hibernate.Session;

import com.hibernate.entities.Department;

public class DepartmentDAO {
	public void save(Department dept){
		Session session=HibernateUtils.getInstence().getSession();
		System.out.println(session.hashCode());
	}
}
