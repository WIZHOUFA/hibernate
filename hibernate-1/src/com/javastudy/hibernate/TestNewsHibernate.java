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
		//1)�½�һ��SessionFactory����
		SessionFactory sessionFactory=null;
		//���½�һ��Configuration����,��ȡhibernate�����ļ������ӳ���ϵ�ļ�
		Configuration configuration=new Configuration().configure();
		//��hibernate4.0֮ǰ��sessionFactory=configuration.buildSessionFactory();
		//��hibernate4.x����Ҫ���²��裺
		//���½�һ��ServiceRegistry����,��������Ϣע�ᵽserviceRegistry��
		ServiceRegistry serviceRegistry=(ServiceRegistry) new ServiceRegistryBuilder()
		                                .applySettings(configuration.getProperties())
		                                .buildServiceRegistry();
		//��ͨ��serviceRegistryΪsessionFactoryʵ����
		sessionFactory=configuration.buildSessionFactory(serviceRegistry);
		//2)�½�һ��session
		Session session=sessionFactory.openSession();
		//3)��������
		Transaction transaction=session.beginTransaction();
		//4)�½�POJO����
		News news=new News("Java","Atguigu",new Date(new java.util.Date().getTime()));
		session.save(news);
		//5)�ύ����
		transaction.commit();
		//6)�ر�session
		session.close();
		//7)�ر�SessionFactory
		sessionFactory.close();
	}

}
