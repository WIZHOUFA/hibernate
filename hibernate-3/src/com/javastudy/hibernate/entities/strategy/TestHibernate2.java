package com.javastudy.hibernate.entities.strategy;

import java.util.List;

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
	 * set 中的lazy,batch-size,fetch属性对一对多，多对多检索策略的影响。
	 * lazy属性：默认为true,并不建议设为false,设为false不会发生懒加载,
	 * 还有设为extra值，当设置为该属性时，增强懒加载
	 * batch-size属性：一次性初始化查询结果的个数(在HQL中使用的比較多)
	 * fetch属性：默认为select,还可设为subselect，决定如何初始化set
	 * 设为subselect,batch-size失效，lazy不会失效
	 * 设置为join，则查询是强迫左外连接，将与之关联的Order一起查出
	 * 但是HQL忽略fetch=join设置。
	 */
	@Test 
	public void testQuery(){
//		Customer customer=(Customer) session.get(Customer.class, 1);
//		System.out.println(customer.getCustomerName());
//		System.out.println(customer.getOrders().size());
        List<Customer> customers=session.createQuery("FROM Customer").list();	
        for(Customer customer:customers){
        	if(customer!=null)
      	       System.out.println(customer.getOrders().size());
        }
	}
	@Test 
	public void testQuery1(){
//		Customer customer=(Customer) session.get(Customer.class, 1);
//		System.out.println(customer.getCustomerName());
//		System.out.println(customer.getOrders().size());
        List<Customer> customers=session.createQuery("FROM Customer").list();	
        for(Customer customer:customers){
        	if(customer!=null)
      	       System.out.println(customer.getOrders().size());
        }
	}
	@Test
	public void teatQuery2(){
		Customer customer=(Customer) session.get(Customer.class, 1);
		System.out.println(customer.getOrders());
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
