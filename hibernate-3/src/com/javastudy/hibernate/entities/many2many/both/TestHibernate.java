package com.javastudy.hibernate.entities.many2many.both;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestHibernate {
	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;
	
	@Before
	public void init(){
		Configuration configuration=new Configuration().configure();
		ServiceRegistry serviceRegistry=new ServiceRegistryBuilder()
		                                .applySettings(configuration.getProperties())
		                                .buildServiceRegistry();
		sessionFactory=configuration.buildSessionFactory(serviceRegistry);
		session=sessionFactory.openSession();
		transaction=session.beginTransaction();
	}
	@After
	public void destory(){
		transaction.commit();
		session.close();
		sessionFactory.close();
	}
	/**
	 * 不能直插入一个如果存在绑定的话。
	 */
	@Test
	public void testSave(){
		Category category1=new Category();
		category1.setName("CATG_A");
		Category category2=new Category();
		category2.setName("CATG_B");
		
		Item item1=new Item();
		item1.setName("Item_A");
		Item item2=new Item();
		item2.setName("Item_B");
		
		//添加绑定
		
		category1.getItems().add(item1);
		category1.getItems().add(item2);
		category2.getItems().add(item1);
		category2.getItems().add(item2);
		item1.getCategories().add(category1);
		item1.getCategories().add(category2);
		item2.getCategories().add(category1);
		item2.getCategories().add(category2);
		//session
		
		session.save(category1);
		session.save(category2);
		session.save(item1);
		session.save(item2);
		
	}
	/**
	 * 存在延时加载,在获取Set时会调用中间表
	 */
	@Test
	public void testGet(){
		Category category=(Category) session.get(Category.class,1);
		System.out.println(category.getName());
		Item item=(Item) session.get(Item.class, 1);
		System.out.println(item.getName());
		System.out.println(category.getItems().size());
	}
}
