package com.hibernate.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCTools {
	public static Connection getConnection() throws IOException, ClassNotFoundException, SQLException{
		InputStream is=JDBCTools.class.getClassLoader().getResourceAsStream("jdbc.properties");
		Properties pro=new Properties();
		pro.load(is);
		Class.forName(pro.getProperty("driver"));
		return DriverManager.getConnection(pro.getProperty("url"), pro.getProperty("user"), pro.getProperty("password"));
	}
	public void relase(Connection conn,Statement stm,ResultSet rs){
		try{
		if(rs!=null){
			rs.close();
		}}catch(Exception e){e.printStackTrace();}
		try{
			if(stm!=null){
				stm.close();
			}}catch(Exception e){e.printStackTrace();}
		try{
			if(conn!=null){
				conn.close();
			}}catch(Exception e){e.printStackTrace();}
	}
}
