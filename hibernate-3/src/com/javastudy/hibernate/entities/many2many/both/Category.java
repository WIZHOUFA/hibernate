package com.javastudy.hibernate.entities.many2many.both;

import java.util.HashSet;
import java.util.Set;

public class Category {
	private Integer categoryId;
	private String name;
	
	private Set<Item> items=new HashSet<Item>();

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Item> getItems() {
		return items;
	}

	public void setItems(Set<Item> items) {
		this.items = items;
	}
	
	
}
