package com.jb.genemap.next.service.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取jdbc连接的公共类
 * @author yjl
 *
 */
public class JdbcDao
{
	public List<Map<String, String>> queryBySql(String sql){
		//从配置文件获取数据库连接信息
		ConfigUtil configUtil = new ConfigUtil();
		String	driverStr = configUtil.getValue("db.properties", "driverStr");
		String	url = configUtil.getValue("db.properties", "url");
		String	userName = configUtil.getValue("db.properties", "userName");
		String	passWord = configUtil.getValue("db.properties", "passWord");
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName(driverStr);
			conn = DriverManager.getConnection(url, userName, passWord);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			result = rs2ListMap(rs);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn!=null){
					conn.close();
				}
				if(stmt!=null){
					stmt.close();
				}
				if(rs!=null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public List<Map<String, String>> rs2ListMap(ResultSet rs)
	{
		try
		{
			List <Map<String,String>> list=new ArrayList<Map<String,String>>();						
			while (rs.next())
			{
				Map<String,String> map=new HashMap<String, String>();
				
				ResultSetMetaData rsmd=rs.getMetaData();
				int count=rsmd.getColumnCount();
				for (int i = 1; i <=count; i++)
				{
					String columnName=rsmd.getColumnName(i);				
					String columnValue=rs.getString(columnName);
				
					map.put(columnName, columnValue);
				}
				list.add(map);
			}
			return list;
		}
		catch (SQLException e)
		{
			System.out.println("rs2ListMap异常");
			e.printStackTrace();
		}
		return null;
	}
}
