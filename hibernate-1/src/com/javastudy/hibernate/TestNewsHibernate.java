package com.javastudy.hibernate;

import java.sql.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Test;


public class TestNewsHibernate {

	@Test
	public void test() {
		//1)新建一个SessionFactory对象
		SessionFactory sessionFactory=null;
		//①新建一个Configuration对象,获取hibernate配置文件与对象映射关系文件
		Configuration configuration=new Configuration().configure();
		//在hibernate4.0之前：sessionFactory=configuration.buildSessionFactory();
		//在hibernate4.x后需要如下步骤：
		//②新建一个ServiceRegistry形象,将配置信息注册到serviceRegistry中
		ServiceRegistry serviceRegistry=(ServiceRegistry) new ServiceRegistryBuilder()
		                                .applySettings(configuration.getProperties())
		                                .buildServiceRegistry();
		//③通过serviceRegistry为sessionFactory实例化
		sessionFactory=configuration.buildSessionFactory(serviceRegistry);
		//2)新建一个session
		Session session=sessionFactory.openSession();
		//3)开启事务
		Transaction transaction=session.beginTransaction();
		//4)新建POJO对象
		News news=new News("Java","Atguigu",new Date(new java.util.Date().getTime()));
		session.save(news);
		//5)提交事务
		transaction.commit();
		//6)关闭session
		session.close();
		//7)关闭SessionFactory
		sessionFactory.close();
	}

}
