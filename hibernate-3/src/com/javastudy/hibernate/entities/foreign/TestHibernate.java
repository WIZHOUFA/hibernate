package com.javastudy.hibernate.entities.foreign;


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
	 * 以不同的顺序插入仍然会出现update不一样的结果。
	 */
	@Test
	public void testSave() {
		Manager manager=new Manager();
		Department department=new Department();
		manager.setMgrName("BB");
		department.setDeptName("bb");
		department.setMgr(manager);
		manager.setDept(department);
		session.save(department);
		session.save(manager);
	}
	/**
	 * 必须设置one-to-one的属性property-ref值为mgr.否则会在get时sql出错
	 * 若果get的值为外键不在的表的类，则即时加载对应的department.若为外键不在的列，则延时加载。
	 * 在使用时初始化对象。
	 */
	@Test
    public void testGet(){
//		Manager manager=(Manager) session.get(Manager.class, 1);
//		System.out.println(manager.getMgrName());
		Department department=(Department) session.get(Department.class, 1);
		System.out.println(department.getDeptName());
		System.out.println(department.getMgr());
	}
}
