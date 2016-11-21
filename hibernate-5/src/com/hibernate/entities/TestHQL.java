package com.hibernate.entities;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hibernate.utils.DepartmentDAO;
import com.hibernate.utils.HibernateUtils;

public class TestHQL {
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
	@Test
	public void testHQL(){
		//����HQL�����ṩһ��hql��䣬���԰���ռλ��"��",����":����"
		String hql="from Employee e where e.salary> ? and e.email like ?"
				+ " order by e.salary";
		Query query=session.createQuery(hql);
		//��ռλ��,index��1��ʼ
		query.setFloat(0, 60)
		     .setString(1, "%B%");
		//ִ�в�ѯ
		List<Employee> emps=query.list();
		System.out.println(emps.size());
	}
	@Test
	public void testHQL2(){
		//����HQL�����ṩһ��hql��䣬���԰���ռλ��"��",����":����"
		String hql="from Employee e where e.salary> :sal and e.email like :email"
				+ " order by e.salary";
		Query query=session.createQuery(hql);
		//��ռλ��
		query.setFloat("sal", 60)
		     .setString("email", "%B%");
		//ִ�в�ѯ
		List<Employee> emps=query.list();
		System.out.println(emps.size());
	}
	//��ҳ��ѯ
	@Test 
	public void testPageHQL(){
		String hql="from Employee";
		Query query=session.createQuery(hql);
		
		int pageNo=3;
		int pageSize=5;
		//���ø�ҳ��һ����¼���ڵ�������0��ʼ
		query.setFirstResult((pageNo-1)*pageSize);
		//���ø�ҳ�ļ�¼��
		query.setMaxResults(5);
		
		List<Employee> emps=query.list();
		
		for(Employee emp:emps){
			System.out.println(emp);
		}
	} 
	//������ѯ
	@Test 
	public void testNamedHQL(){
		Query query=session.getNamedQuery("deptquery");
		
		Department dept=new Department();
		dept.setId(80);
		query.setEntity("dept",dept);
		
		List<Employee> emps=query.list();
		System.out.println(emps.size());
	}
	//ͶӰ��ѯ
	@Test
	public void testTouyinHQL1(){
		String hql="select id,email,salary from Employee where dept_id=:dept";
		Query query=session.createQuery(hql);
		
		query.setInteger("dept", 80);
		
		List<Object[]> emps=query.list();
		
		for(Object[] emp:emps){
			System.out.println(Arrays.asList(emp));
		}
	}
	@Test
	public void testTouyinHQL2(){
		String hql="select new Employee(id,salary,email,dept) from Employee where dept_id=:dept";
		Query query=session.createQuery(hql);
		
		Department dept=new Department();
        dept.setId(80);		
		List<Employee> emps=query.setEntity("dept", dept).list();
		
		for(Employee emp:emps){
			System.out.println(Arrays.asList(emp));
		}
	}
	//�����ѯ��ʹ��group by,having�ȷ��麯��
	@Test
	public void testGroupHQL(){
		String hql="select Max(salary),Min(salary) "
				+ " from Employee "
				+ " group by dept_id"
				+ " having avg(salary)> :sal";
		Query query=session.createQuery(hql);
		
		query.setFloat("sal", 10000);
		
		List<Object[]> info=query.list();
		Iterator<Object[]> iterator=info.iterator();
		while(iterator.hasNext()){
			System.out.println(Arrays.asList(iterator.next()));
		}
	}
	//HQL������������:1.ֻ����һ��SQL��䣬���ص������������Ķ��󡣲�������������N��
	//��һ���ļ�¼���ڻ�ȡʱ���ᷢ��sql���
	//2.���ȥ�أ����ַ�ʽ��
	//a.hql��Ϊselect distinct d from Department d left join fetch d.emps;
	//b.LinkedHashSet��װ���ɵ�list,�Զ�ȥ��
	@Test
	public void testHQLJoin1(){
		String hql="From Department d left join fetch d.emps";
		Query query=session.createQuery(hql);
		
		List<Department> depts=query.list();
		depts=new ArrayList<Department>(new LinkedHashSet<Department>(depts));
		System.out.println(depts.size());
		for(Department dept:depts){
		     System.out.println(dept.getName()+":"+dept.getEmps().size());
		}
	}
	//HQL�������ӣ�1.���ص���Object[]���ͣ��������Ӳ��ţ�Ա�������������
	//2.��η�����������ͣ�hql:select d From Department d left join d.emps;
	//���ǣ���������Ӧ�ļ��ϲ�û�б����ء���ÿ�ε��õ�ʱ����sql��䣬��ʱ����
	//3.ȥ�ء�ֻ��ͨ����װlist�ķ������ء�
	@Test
	public void testHQLJoin2(){
		String hql="select d From Department d left join d.emps";
		Query query=session.createQuery(hql);
		
		List<Department> depts=new ArrayList<Department>(new LinkedHashSet<Department>(query.list()));

		System.out.println(depts.size());
		for(Department dept:depts){
			System.out.println(dept.getName()+":"+dept.getEmps().size());
		}
	}
	//������������������Inner join fetch ��inner join �൱�������ӡ�����ʽ��left����һ��
	
	
	//QBC����
	//Criteria����׼��׼��;Restriction: ���ƣ��޶�
	//criteria��add����������Criterion����ֵ�����ù�Restriction�ľ�̬������á�
	@Test
	public void testQBC(){
		//1.����һ��Criteria����
		Criteria criteria =session.createCriteria(Employee.class);
		//2.��Ӳ�ѯ������������eq,like...
		criteria.add(Restrictions.eq("name","Baer"));
		//3.ִ�в�ѯ
		Employee employee=(Employee) criteria.uniqueResult();
		System.out.println(employee);
	}
	//�ڲ�ѯ������ʹ��And���ӷ���conjunction�����ϣ�����
	//Conjunction��Ҳ����Criterion��,���Դ���add(Criterion criterion)������
	//�ڲ�ѯ��ʹ��Or���ӷ���disjunction:���룬��ȡ��ͬ��Ϊcriterion����
	//�����������ӵ�sql����
	@Test
	public void testAndQBC(){
		Criteria criteria=session.createCriteria(Employee.class);
		Conjunction conjunction=Restrictions.conjunction();
		conjunction.add(Restrictions.like("name", "a", MatchMode.ANYWHERE));
		Department dept=new Department();
		dept.setId(80);
		conjunction.add(Restrictions.eq("dept", dept));
		System.out.println(conjunction);
	    Disjunction disjunction=Restrictions.disjunction();
	    disjunction.add(Restrictions.isNotNull("email"));
	    disjunction.add(Restrictions.ge("salary", 6000F));
	    System.out.println(disjunction);
	    conjunction.add(disjunction);
	    System.out.println(conjunction);
		//criteria.add(conjunction);
		//criteria
	    criteria.add(disjunction);
	    List<Employee> emps=(List<Employee>) criteria.list();
	    System.out.println(emps.size());
	    
	}
	//ͳ�Ʋ�ѯ��projection��Ͷ�䡣
	@Test
	public void testGroupQBC(){
		Criteria criteria=session.createCriteria(Employee.class);
		criteria.setProjection(Projections.max("salary"));
		float max_sal=(Float) criteria.uniqueResult();
		System.out.println(max_sal);
	}
	//�����ѯ
	@Test
	public void testOrderQBC(){
		Criteria criteria=session.createCriteria(Employee.class);
		criteria.addOrder(Order.asc("salary"))
		        .addOrder(Order.desc("name"));
		List<Employee> emps=criteria.list();
		for(Employee emp:emps){
			System.out.println(emp.getName()+":"+emp.getSalary());
		}
	}
	//��ҳ��ѯ
	@Test
	public void testPageQBC(){
		Criteria criteria=session.createCriteria(Employee.class);
		int pageNo=2;
		int pageSize=5;
		criteria.setFirstResult((pageNo-1)*pageSize)
		        .setMaxResults(pageSize);
		List<Employee> emps=criteria.list();
		for(Employee emp:emps){
			System.out.println(emp.getId()+":"+emp.getName());
		}
	}
	
	//����SQL��ѯ
	@Test
	public void testLocalSQL(){
		String sql="insert into dept values (?,?)";
		Query query=session.createSQLQuery(sql);
		query.setInteger(0, 280);
		query.setString(1, "Atguigu");
		
		query.executeUpdate();
		
	}
	
	
	
	
	//�������棺sessionFactory�еĻ��档ʹ�û�����EHCACHE
	//1.����jar��,cache.xml�����ļ�
	//2.��cfg.xml�����ļ��У�ָ���Ƿ�ʹ�ö������棬ʹ���ĸ������Ʒ�����ĸ�����߼���ʹ�û���
	//�༶�𻺴档
	@Test
	public void testClassCache(){
		Employee emp=(Employee) session.get(Employee.class, 107);
		System.out.println(emp);
		transaction.commit();
		session.close();
		
		session=sessionFactory.openSession();
		transaction=session.beginTransaction();
		
		Employee emp1=(Employee) session.get(Employee.class, 107);
		System.out.println(emp1);
	}
	//���ϼ���Ļ���:��ʵֻҪ�����Ӧ����ͺ���
	@Test
	public void testCollectionCache(){
		Department dept=(Department) session.get(Department.class, 20);
		List<Employee> emps=new ArrayList(dept.getEmps());
		for(Employee emp:emps){
			System.out.println(emp.getName()+":"+emp.getSalary());
		}
		
		transaction.commit();
		session.close();
		
		session=sessionFactory.openSession();
		transaction=session.beginTransaction();
		
		Department dept2=(Department) session.get(Department.class, 20);
		List<Employee> emps2=new ArrayList(dept.getEmps());
		for(Employee emp:emps2){
			System.out.println(emp.getName()+":"+emp.getSalary());
		}
	}
	//��ѯ������ʱ�������,�����ڶ�������
	@Test
	public void testQueryCache(){
		Employee emp=(Employee) session.get(Employee.class, 107);
		System.out.println(emp);
		
		emp.setEmail("LILI");
		
		Employee emp2=(Employee) session.get(Employee.class, 107);
		System.out.println(emp2);
		
	}
	
	//����session,ͨ���뱾���ֳɰ󶨵ķ�ʽ������session��hibernate��װ�˴��෽��
	
	@Test
	public void testManageSession(){
		Session session=HibernateUtils.getInstence().getSession();
		System.out.println(session.hashCode());
		Department dept=new Department();
		dept.setId(290);
		
		DepartmentDAO dao=new DepartmentDAO();
		dao.save(dept);
		dao.save(dept);
		dao.save(dept);
		dao.save(dept);
	}
	
	//������ɾ��,ʹ��ԭ����JDBCAPIЧ�ʸ�
	@Test
	public void testBatch(){
		Work work=new Work(){

			public void execute(Connection conn) throws SQLException {
				// TODO Auto-generated method stub
				String sql="insert into ss values (?,?,?)";
				PreparedStatement ps=conn.prepareStatement(sql);
				long start=System.currentTimeMillis();
				Date date=new Date(start);
				for(int i=0;i<10000;i++){
					ps.setInt(1, i+1);
					ps.setString(2, "a"+i);
					ps.setDate(3,date);
					ps.execute();
				}
				long end=System.currentTimeMillis();
				System.out.println(end-start);//1435
			}
			};
	    session.doWork(work);
	}
	@Test
	public void testBatch2(){
		Work work=new Work(){

			public void execute(Connection conn) throws SQLException {
				// TODO Auto-generated method stub
				String sql="insert into ss values (?,?,?)";
				PreparedStatement ps=conn.prepareStatement(sql);
				long start=System.currentTimeMillis();
				Date date=new Date(start);
				for(int i=0;i<10000;i++){
					ps.setInt(1, i+1);
					ps.setString(2, "a"+i);
					ps.setDate(3,date);
					ps.addBatch();
					if((i+1)%300==0){
						ps.executeBatch();
						ps.clearBatch();
					}
				}
				if(10000%300!=0){
					ps.executeBatch();
					ps.clearBatch();
				}
				long end=System.currentTimeMillis();
				System.out.println(end-start);//55
			}
			};
	    session.doWork(work);
	}
}
 