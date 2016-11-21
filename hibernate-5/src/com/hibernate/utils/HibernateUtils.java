package com.hibernate.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtils {
	private static HibernateUtils hibernateUtils=new HibernateUtils();
	private HibernateUtils(){
		
	}
	public static HibernateUtils getInstence(){
		return hibernateUtils;
	}
	
	private SessionFactory sessionFactory;
	public SessionFactory getSessionFactory(){
		if(sessionFactory==null){
		Configuration configuration=new Configuration().configure();
		ServiceRegistry serviceRegistry=new ServiceRegistryBuilder()
		                                    .applySettings(configuration.getProperties())
		                                    .buildServiceRegistry();
		sessionFactory=configuration.buildSessionFactory(serviceRegistry);
		}
		return sessionFactory;
	}
	
	public Session getSession(){
		return getSessionFactory().getCurrentSession();
	}
}
