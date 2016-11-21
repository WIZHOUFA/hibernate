package com.javastudy.hibernate.n21.both;

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
	 * 双向一对多的插入.会出现3条Insert,2条Update。
	 * 可以通过设置set中的reverse=true属性来指定主控方。一般将主控方放在多的那一方（理解）。
	 */
	@Test
	public void testInsert1(){
        Customer customer=new Customer("AA");
        Order order=new Order();
        order.setOrderName("aa");
        order.setCustomer(customer);
        Order order2=new Order("bb",customer);
        customer.getOrders().add(order);
        customer.getOrders().add(order2);
        session.save(customer);
        session.save(order);
        session.save(order2);
	}
	/**
	 * 先插入两条多的一端，后插入少的一端则会仍然会多出两条Update。因此推荐在插入时先插入一的一端
	 * 在插入n的一端以提高效率。
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
	 * 查询一的一端，会产生集合的懒加载
	 * 查询n的一端，会产生对一的那一端的懒加载
	 */
	@Test 
	public void testQuery(){
		Customer customer=(Customer) session.get(Customer.class, 1);
		System.out.println(customer.getCustomerName());
		System.out.println(customer.getOrders().getClass());
	}
	/**
	 * 删除一的那一端如果在多的一端数据库存在对应的记录，则会出现异常。
	 * 如果没有对应则不会出现异常。
	 */
	@Test
	public void testDelete(){
		session.delete(session.get(Customer.class, 3));
	}
	/**
	 * 可以通过一得一端来修改n的那一端的属性。
	 */
	@Test
	public void testUpdate(){
		Customer customer=(Customer) session.get(Customer.class, 1);
		customer.getOrders().iterator().next().setOrderName("KK");
	}
}
