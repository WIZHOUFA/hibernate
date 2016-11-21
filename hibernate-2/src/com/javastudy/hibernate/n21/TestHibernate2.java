package com.javastudy.hibernate.n21;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestHibernate2 {
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
	 * 先插入一的那一端会插入3条Insert语句。
	 */
	@Test
	public void testInsert1(){
        Customer customer=new Customer("AA");
        Order order=new Order();
        order.setOrderName("aa");
        order.setCustomer(customer);
        Order order2=new Order("bb",customer);
        session.save(customer);
        session.save(order);
        session.save(order2);
	}
	/**
	 * 先插入两条多的一端，后插入少的一端则会多出两条Update
	 */
	@Test
	public void testInsert2(){
		Customer customer=new Customer("CC");
        Order order=new Order();
        order.setOrderName("ee");
        order.setCustomer(customer);
        Order order2=new Order("ff",customer);
        session.save(order);
        session.save(order2);
        session.save(customer);
	}
	/**
	 * 查询多的一端，会产生懒加载
	 */
	@Test 
	public void testQuery(){
		Order order=(Order) session.get(Order.class, 1);
		System.out.println(order.getOrderName());
		//其获取的customer为代理对象。
		System.out.println(order.getCustomer().getClass().getName());
		//当真正获取customer时才会发送sql语句从数据库中获取customer对象。
		//若果在其中间加入session.close()则会发生懒加载异常（LazyInitializationException）。
		//session.close();
		System.out.println(order.getCustomer());
	}
	/**
	 * 删除一的那一端如果在多的一端数据库存在对应的记录，则会出现异常。
	 * 如果没有对应则不会出现异常。
	 */
	@Test
	public void testDelete(){
		session.delete(session.get(Customer.class, 3));
	}
}
