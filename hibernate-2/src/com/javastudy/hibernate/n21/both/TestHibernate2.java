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
	 * ˫��һ�Զ�Ĳ���.�����3��Insert,2��Update��
	 * ����ͨ������set�е�reverse=true������ָ�����ط���һ�㽫���ط����ڶ����һ�������⣩��
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
	 * �Ȳ����������һ�ˣ�������ٵ�һ�������Ȼ��������Update������Ƽ��ڲ���ʱ�Ȳ���һ��һ��
	 * �ڲ���n��һ�������Ч�ʡ�
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
	 * ��ѯһ��һ�ˣ���������ϵ�������
	 * ��ѯn��һ�ˣ��������һ����һ�˵�������
	 */
	@Test 
	public void testQuery(){
		Customer customer=(Customer) session.get(Customer.class, 1);
		System.out.println(customer.getCustomerName());
		System.out.println(customer.getOrders().getClass());
	}
	/**
	 * ɾ��һ����һ������ڶ��һ�����ݿ���ڶ�Ӧ�ļ�¼���������쳣��
	 * ���û�ж�Ӧ�򲻻�����쳣��
	 */
	@Test
	public void testDelete(){
		session.delete(session.get(Customer.class, 3));
	}
	/**
	 * ����ͨ��һ��һ�����޸�n����һ�˵����ԡ�
	 */
	@Test
	public void testUpdate(){
		Customer customer=(Customer) session.get(Customer.class, 1);
		customer.getOrders().iterator().next().setOrderName("KK");
	}
}