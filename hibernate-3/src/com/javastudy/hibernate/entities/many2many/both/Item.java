package com.javastudy.hibernate.entities.many2many.both;

import java.util.HashSet;
import java.util.Set;

public class Item {
	private Integer itemId;
	private String name;
	
	private Set<Category> categories=new HashSet<Category>();
	
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<Category> getCategories() {
		return categories;
	}
	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}
	
}
