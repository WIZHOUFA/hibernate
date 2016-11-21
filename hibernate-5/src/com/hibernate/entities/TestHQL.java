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
		//创建HQL对象，提供一个hql语句，可以包含占位符"？",或者":别名"
		String hql="from Employee e where e.salary> ? and e.email like ?"
				+ " order by e.salary";
		Query query=session.createQuery(hql);
		//绑定占位符,index从1开始
		query.setFloat(0, 60)
		     .setString(1, "%B%");
		//执行查询
		List<Employee> emps=query.list();
		System.out.println(emps.size());
	}
	@Test
	public void testHQL2(){
		//创建HQL对象，提供一个hql语句，可以包含占位符"？",或者":别名"
		String hql="from Employee e where e.salary> :sal and e.email like :email"
				+ " order by e.salary";
		Query query=session.createQuery(hql);
		//绑定占位符
		query.setFloat("sal", 60)
		     .setString("email", "%B%");
		//执行查询
		List<Employee> emps=query.list();
		System.out.println(emps.size());
	}
	//分页查询
	@Test 
	public void testPageHQL(){
		String hql="from Employee";
		Query query=session.createQuery(hql);
		
		int pageNo=3;
		int pageSize=5;
		//设置该页第一条记录所在的索引从0开始
		query.setFirstResult((pageNo-1)*pageSize);
		//设置该页的记录数
		query.setMaxResults(5);
		
		List<Employee> emps=query.list();
		
		for(Employee emp:emps){
			System.out.println(emp);
		}
	} 
	//命名查询
	@Test 
	public void testNamedHQL(){
		Query query=session.getNamedQuery("deptquery");
		
		Department dept=new Department();
		dept.setId(80);
		query.setEntity("dept",dept);
		
		List<Employee> emps=query.list();
		System.out.println(emps.size());
	}
	//投影查询
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
	//报表查询：使用group by,having等分组函数
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
	//HQL迫切左外连接:1.只发送一条SQL语句，返回的类型是所查表的对象。并且所查结果包含N的
	//哪一方的记录，在获取时不会发送sql语句
	//2.如何去重：两种方式：
	//a.hql改为select distinct d from Department d left join fetch d.emps;
	//b.LinkedHashSet包装生成的list,自动去重
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
	//HQL左外连接：1.返回的是Object[]类型，包含连接部门，员工表的所有内容
	//2.如何返回所查表类型：hql:select d From Department d left join d.emps;
	//但是，该类所对应的集合并没有被加载。在每次调用的时候发送sql语句，延时加载
	//3.去重。只能通过包装list的方法返回。
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
	//迫切内连接与内连接Inner join fetch 与inner join 相当于右连接。其形式与left连接一致
	
	
	//QBC检索
	//Criteria：标准，准则;Restriction: 限制，限定
	//criteria的add函数，传入Criterion类型值，可用过Restriction的静态方法获得。
	@Test
	public void testQBC(){
		//1.创建一个Criteria对象
		Criteria criteria =session.createCriteria(Employee.class);
		//2.添加查询条件，可以是eq,like...
		criteria.add(Restrictions.eq("name","Baer"));
		//3.执行查询
		Employee employee=(Employee) criteria.uniqueResult();
		System.out.println(employee);
	}
	//在查询条件中使用And连接符。conjunction：联合，连接
	//Conjunction类也属于Criterion类,可以传入add(Criterion criterion)函数中
	//在查询中使用Or连接符。disjunction:分离，析取，同样为criterion子类
	//可以做出复杂的sql连接
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
	//统计查询，projection：投射。
	@Test
	public void testGroupQBC(){
		Criteria criteria=session.createCriteria(Employee.class);
		criteria.setProjection(Projections.max("salary"));
		float max_sal=(Float) criteria.uniqueResult();
		System.out.println(max_sal);
	}
	//排序查询
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
	//分页查询
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
	
	//本地SQL查询
	@Test
	public void testLocalSQL(){
		String sql="insert into dept values (?,?)";
		Query query=session.createSQLQuery(sql);
		query.setInteger(0, 280);
		query.setString(1, "Atguigu");
		
		query.executeUpdate();
		
	}
	
	
	
	
	//二级缓存：sessionFactory中的缓存。使用缓存插件EHCACHE
	//1.导入jar包,cache.xml配置文件
	//2.在cfg.xml配置文件中，指定是否使用二级缓存，使用哪个缓存产品，对哪个类或者集合使用缓存
	//类级别缓存。
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
	//集合级别的缓存:其实只要缓存对应的类就好了
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
	//查询缓存与时间戳缓存,依赖于二级缓存
	@Test
	public void testQueryCache(){
		Employee emp=(Employee) session.get(Employee.class, 107);
		System.out.println(emp);
		
		emp.setEmail("LILI");
		
		Employee emp2=(Employee) session.get(Employee.class, 107);
		System.out.println(emp2);
		
	}
	
	//管理session,通过与本地现成绑定的方式来管理session，hibernate封装了此类方法
	
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
	
	//批量增删改,使用原生的JDBCAPI效率高
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
 