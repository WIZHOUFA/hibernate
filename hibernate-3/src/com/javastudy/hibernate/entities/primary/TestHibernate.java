package com.javastudy.hibernate.entities.primary;


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
	 * 以基于主键映射的一对一，即使顺序不同，也不会有多余的update语句，
	 * 先插入哪一个都可以。
	 * 如果仅插入有外键的那个记录，则连同没有外键的都会被插入。
	 */
	@Test
	public void testSave() {
		Manager manager=new Manager();
		Department department=new Department();
		
		manager.setMgrName("DD");
		department.setDeptName("dd");
		
		department.setMgr(manager);
		manager.setDept(department);
		
		session.save(manager);
		session.save(department);
	}
	/**
	 * get有主键的会存在懒加载，
	 * get没有主键的会有左外连接。
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
