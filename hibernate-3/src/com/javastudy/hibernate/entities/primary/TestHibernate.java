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
	 * �Ի�������ӳ���һ��һ����ʹ˳��ͬ��Ҳ�����ж����update��䣬
	 * �Ȳ�����һ�������ԡ�
	 * �����������������Ǹ���¼������ͬû������Ķ��ᱻ���롣
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
	 * get�������Ļ���������أ�
	 * getû�������Ļ����������ӡ�
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
