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
		//����configuration����*.cfg.xml�ļ��л�ȡ���ã�Ҳ������config()�����������ļ���
		//����ȡ�����ļ�������
		Configuration configuration=new Configuration().configure();
		//��ȡserviceRedistry���󣬽�����ע�ᵽ���������
		ServiceRegistry serviceRegistry=new ServiceRegistryBuilder()
		                                .applySettings(configuration.getProperties())
		                                .buildServiceRegistry();
		//��ȡsessionFactory��ֵ
		sessionFactory=configuration.buildSessionFactory(serviceRegistry);
		//��ȡHibernate����Session
		session=sessionFactory.openSession();
		//��ȡ����
		transaction=session.beginTransaction();
	}
	@After
	public void destory(){
		transaction.commit();
		session.close();
		sessionFactory.close();
	}
	/**
	 * Session �������
	 */
	@Test
	public void testSessionCahe() {
		News news=(News) session.get(News.class, 1);
		System.out.println(news);
		//��session�����ݿ��л�ȡԪ�ػ�Ὣ��ȡ����ֵ����Session���棬�´λ�ȡʱ��
		//û�жԻ��������ǰ���»��ȷ���Session���档
		News news1=(News) session.get(News.class, 1);
		System.out.println(news1);
	}
	/**
	 * flush()ʹ���ݿ�������session��������һ�¡�
	 */
    @Test
    public void testSessionFlush(){
    	News news=(News) session.get(News.class, 1);
    	news.setAuthor("Sun");
    	session.flush();
    	System.out.println("flush...");
    }
    /**
     * reflush()ʹsession���������ݿ����ݼ�¼һ�£�Mysql��������뼶����ΪReadCommited��
     * cfg.xml��������connection.isolationΪ2����ͬ������ΪMysqlĬ��repeatable read��
     */
    @Test
    public void testSessionReflush(){
    	News news=(News)session.get(News.class, 1);
    	System.out.println(news);
    	session.refresh(news);
    	System.out.println(news);
    }
    /**
     * ��session�����е�������ա����û��clear,�ٴλ�ȡ��ͬID�ļ�¼��ֱ�Ӵӻ����л�ȡ��
     * clear()�Ժ���Ҫ�ٴδ����ݿ��ȡ
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
     * persist()������ִ��ǰ��ʱ����������ID������ᱨ�쳣��
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
     * load()��get()�����Ƚϣ�
     * 1.get()�����ڵ���ʱ���ͻ���������sql�����ض�����������������load()����ֻ���ڵ���
     * ���ɵĳ־û�����ʱ�Żᷢ�ͣ�������ʱ������
     * 2.load���صĶ������ڴ��������com.javastudy.hibernate.News_$$_javassist_0
     * 3.get()������ѯʱ��û�������ݿ��л�ȡ��������᷵��null;load()��������û�������ݿ���
     * ��ȡ����ӦԪ������ڵ��ö���ʱ�׳�ObjectNotFoundException.
     * 4.load()�������׳�LazyInitializationException�쳣���������쳣��������Session�ڵ���
     * �����ض���ǰ�ر�session.
     */
    @Test
    public void testLoad(){
    	News news=(News) session.load(News.class, 1);
    	System.out.println(news.getClass().getName());
    	session.close();
    	System.out.println(news);
    } 
    /**
     * 1.������һ���־û����󣬲���Ҫ��ʾ����Update()����������SetID()����,��Ϊ
     * ��transaction.commit()ǰ�����session��flush()�������Զ�����sql��䣻
     * 2.������һ�����������Ҫ��ʾ����update(Object object)������
     * 3.�����µĳ־û�����û�иı䣬�����ʱ���ᷢ��UPDATE()��䣻����������������
     * ����SQL��䣬���ܻ���ò���Ҫ�Ĵ�����������������sql��������class
     * ������select-before-updateΪtrue.�������select��䣩
     * 4.��Update()�����еĶ������ݿ���û������׳�һ���쳣��
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
     * ���OIDΪNULL ��Ϊsave(),����ΪNULL,��Ϊupdate().
     */
    @Test
    public void testSaveOrUpdate(){
    }
    /**
     * ��Hibernate�����ļ��У�����hibernate��hibernate.use_identifier_rollbackΪtrue
     * ����ɾ���������������OIDΪ��,�������ʧ��
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
     * ����ת������.
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
     * ��class���Ե�dynamic_update����Ϊtrue�����޸�����ʱ��Updateֻ�޸Ķ�Ӧ�����ԣ���Ϊfalse
     * ��ȫ���޸ġ�
     */
    @Test
    public void testDynamicUpdate(){
//    	News news=(News) session.get(News.class, 4);
//    	news.setAuthor("MMM");
    	News news1=new News("TT","tt",new Date(new java.util.Date().getTime()));
    	session.save(news1);
    }
    /**
     * ��property��������formula����Ϊ��sql��䣩����ֵΪselect���������ԡ�
     */
    @Test
    public void testFormula(){
    	News news=(News) session.get(News.class, 4);
    	System.out.println(news.getDesc());
    }
    /**
     * SQL,Java,Hibernate��Date��Time,Timestamp��ӳ���ϵ��
     * �ڳ־û�����һ����DateΪjava.util.Date,��Ϊ����ʱjava.sql.Date,Time,TimeStamp�ĸ���
     */
    @Test
    public void testDate(){
    	News news1=new News("RR","rr",new java.util.Date());
    	session.save(news1);
    }
    /**
     *���������ݿ��м���Blob,Text���ļ��������ƶ���
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
