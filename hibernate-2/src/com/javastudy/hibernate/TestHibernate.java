package com.javastudy.hibernate;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
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
		//配置configuration，从*.cfg.xml文件中获取配置，也可以在config()函数中重置文件名
		//来获取配置文件的内容
		Configuration configuration=new Configuration().configure();
		//获取serviceRedistry对象，将配置注册到服务代理中
		ServiceRegistry serviceRegistry=new ServiceRegistryBuilder()
		                                .applySettings(configuration.getProperties())
		                                .buildServiceRegistry();
		//获取sessionFactory的值
		sessionFactory=configuration.buildSessionFactory(serviceRegistry);
		//获取Hibernate核心Session
		session=sessionFactory.openSession();
		//获取事务
		transaction=session.beginTransaction();
	}
	@After
	public void destory(){
		transaction.commit();
		session.close();
		sessionFactory.close();
	}
	/**
	 * Session 缓存测试
	 */
	@Test
	public void testSessionCahe() {
		News news=(News) session.get(News.class, 1);
		System.out.println(news);
		//当session从数据库中获取元素或会将获取到的值存入Session缓存，下次获取时在
		//没有对缓存清理的前提下会先访问Session缓存。
		News news1=(News) session.get(News.class, 1);
		System.out.println(news1);
	}
	/**
	 * flush()使数据库数据与session缓存数据一致。
	 */
    @Test
    public void testSessionFlush(){
    	News news=(News) session.get(News.class, 1);
    	news.setAuthor("Sun");
    	session.flush();
    	System.out.println("flush...");
    }
    /**
     * reflush()使session缓存与数据库数据记录一致（Mysql的事务隔离级别设为ReadCommited，
     * cfg.xml中在设置connection.isolation为2才能同步，因为Mysql默认repeatable read）
     */
    @Test
    public void testSessionReflush(){
    	News news=(News)session.get(News.class, 1);
    	System.out.println(news);
    	session.refresh(news);
    	System.out.println(news);
    }
    /**
     * 将session缓存中的数据清空。如果没有clear,再次获取相同ID的记录，直接从缓存中获取。
     * clear()以后则要再次从数据库获取
     */
    @Test 
    public void testSessionClear(){
    	News news1=(News) session.get(News.class, 1);
    	System.out.println(news1);
    	session.clear();
    	News news2=(News) session.get(News.class, 1);
    	System.out.println(news2);
    }
    @Test
    public void testSave(){
    	//News news=new News("XML","Sun",new Date(new java.util.Date().getTime()));
    	News news=new News();
    	news.setAuthor("BB");
    	news.setTitle("bb");
    	news.setDate(new Date(new java.util.Date().getTime()));
    	news.setId(10);
    	System.out.println(news);
    	session.save(news);
    	System.out.println(news);
    	
    }
    /**
     * persist()方法在执行前临时对象不能设置ID，否则会报异常。
     */
    @Test
    public void testPersist(){
    	News news=new News();
    	news.setAuthor("CC");
    	news.setTitle("cc");
    	news.setDate(new Date(new java.util.Date().getTime()));
    	//news.setId(10);
    	System.out.println(news);
    	session.persist(news);
    	System.out.println(news);
    }
    @Test
    public void testGet(){
    	News news=(News) session.get(News.class, 12);
        System.out.println(news);
    }
    /**
     * load()与get()方法比较：
     * 1.get()方法在调用时，就会立即发送sql语句加载对象，属于立即检索；load()方法只有在调用
     * 生成的持久化对象时才会发送，属于延时检索。
     * 2.load加载的对象属于代理类对象com.javastudy.hibernate.News_$$_javassist_0
     * 3.get()方法查询时若没有在数据库中获取到对象，则会返回null;load()方法若果没有在数据库中
     * 获取到对应元素则会在调用对象时抛出ObjectNotFoundException.
     * 4.load()方法会抛出LazyInitializationException异常（懒加载异常）：比如Session在调用
     * 被加载对象前关闭session.
     */
    @Test
    public void testLoad(){
    	News news=(News) session.load(News.class, 1);
    	System.out.println(news.getClass().getName());
    	session.close();
    	System.out.println(news);
    } 
    /**
     * 1.若更新一个持久化对象，不需要显示调用Update()方法，不可SetID()方法,因为
     * 在transaction.commit()前会调用session的flush()方法，自动发送sql语句；
     * 2.若更新一个游离对象，需要显示调用update(Object object)方法。
     * 3.若更新的持久化对象没有改变，则调用时不会发送UPDATE()语句；若更新游离对象则会
     * 发送SQL语句，可能会调用不必要的触发器。（若不调用sql语句则会在class
     * 中设置select-before-update为true.但会调用select语句）
     * 4.若Update()方法中的对象数据库中没有则会抛出一个异常。
     */
    @Test
    public void testUpdate(){
    	News news=(News) session.get(News.class, 1);
    	transaction.commit();
    	session.close();
    	session=sessionFactory.openSession();
    	transaction=session.beginTransaction();
    	//news.setAuthor("MOMO");
    	//News news=new News("DD","dd",new Date(new java.util.Date().getTime()));
    	session.update(news);
    }
    /**
     * 如果OID为NULL 则为save(),若不为NULL,则为update().
     */
    @Test
    public void testSaveOrUpdate(){
    }
    /**
     * 在Hibernate配置文件中，设置hibernate的hibernate.use_identifier_rollback为true
     * 则在删除后变量后立即置OID为空,避免操作失误。
     */
    @Test
    public void testDelete(){
    	News news=(News)session.get(News.class, 3);
    	System.out.println(news);
    	session.delete(news);
    	System.out.println(news);
    }
    @Test
    public void testEvcit(){
    	News news=(News)session.get(News.class, 4);
    	System.out.println(news);
    	News news1=(News)session.get(News.class, 4);
    	System.out.println(news1);
    	session.evict(news);
    	News news2=(News)session.get(News.class, 4);
    	System.out.println(news2);
    }
    /**
     * 调用转储过程.
     */
    @Test
    public void testDowork(){
    	session.doWork(new Work(){

			public void execute(Connection connection) throws SQLException {
				// TODO Auto-generated method stub
				System.out.println(connection);
			}
    		
    	});
    }
    /**
     * 置class属性的dynamic_update属性为true，在修改属性时，Update只修改对应的属性，置为false
     * 则全都修改。
     */
    @Test
    public void testDynamicUpdate(){
//    	News news=(News) session.get(News.class, 4);
//    	news.setAuthor("MMM");
    	News news1=new News("TT","tt",new Date(new java.util.Date().getTime()));
    	session.save(news1);
    }
    /**
     * 在property属性中置formula属性为（sql语句），其值为select出来的属性。
     */
    @Test
    public void testFormula(){
    	News news=(News) session.get(News.class, 4);
    	System.out.println(news.getDesc());
    }
    /**
     * SQL,Java,Hibernate中Date，Time,Timestamp的映射关系。
     * 在持久化类中一般置Date为java.util.Date,因为该类时java.sql.Date,Time,TimeStamp的父类
     */
    @Test
    public void testDate(){
    	News news1=new News("RR","rr",new java.util.Date());
    	session.save(news1);
    }
    /**
     *测试在数据库中加入Blob,Text等文件，二进制对象。
     * @throws Exception 
     */
    @Test
    public void testInsertBlob() throws Exception{
    	News news=new News();
    	news.setTitle("LL");
    	news.setAuthor("ll");
    	news.setDate(new java.util.Date());
    	InputStream stream=new FileInputStream("1.jpg");
    	Blob image=Hibernate.getLobCreator(session)
    			.createBlob(stream, stream.available());
    	news.setImage(image);
    	System.out.println(stream.available());
        session.save(news);
    }
    @Test
    public void testGetBlob() throws Exception{
    	News news=(News) session.get(News.class, 1);
    	Blob blob=news.getImage();
    	InputStream stream=blob.getBinaryStream();
    	System.out.println(stream.available());
    }
}
