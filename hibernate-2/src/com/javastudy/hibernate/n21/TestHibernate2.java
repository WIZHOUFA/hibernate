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
	 * �Ȳ���һ����һ�˻����3��Insert��䡣
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
	 * �Ȳ����������һ�ˣ�������ٵ�һ�����������Update
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
	 * ��ѯ���һ�ˣ������������
	 */
	@Test 
	public void testQuery(){
		Order order=(Order) session.get(Order.class, 1);
		System.out.println(order.getOrderName());
		//���ȡ��customerΪ�������
		System.out.println(order.getCustomer().getClass().getName());
		//��������ȡcustomerʱ�Żᷢ��sql�������ݿ��л�ȡcustomer����
		//���������м����session.close()��ᷢ���������쳣��LazyInitializationException����
		//session.close();
		System.out.println(order.getCustomer());
	}
	/**
	 * ɾ��һ����һ������ڶ��һ�����ݿ���ڶ�Ӧ�ļ�¼���������쳣��
	 * ���û�ж�Ӧ�򲻻�����쳣��
	 */
	@Test
	public void testDelete(){
		session.delete(session.get(Customer.class, 3));
	}
}
