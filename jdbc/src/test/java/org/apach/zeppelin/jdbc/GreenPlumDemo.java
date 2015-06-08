/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apach.zeppelin.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GreenPlumDemo {
	
	private Connection db = null;
	public static void main(String[] args) {
		GreenPlumDemo demo = new GreenPlumDemo();
		String url = "jdbc:postgresql://172.19.1.101:5432/test";
		String userName = "gpadmin";
		String passWord = "gpadmin";
		demo.initConnetion(url,userName,passWord);
		
		String sql = "select * from t1";
		demo.query(sql);
		
		
		String creatTable = "CREATE TABLE NewTable (\"id\" int4,\"hostname\" text,\"dfdevice\" text,\"dfspace\" int8)";
		demo.excuteUpdate(creatTable);
	}
	public void initConnetion(String url,String userName,String passWord){
		try {
			Class.forName("org.postgresql.Driver");
			db = DriverManager.getConnection(url, userName, passWord);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void query(String sql){
		try {
/*			PreparedStatement  st = db.prepareStatement(sql);
			//st.setInt(1,5);			
			ResultSet rs = st.executeQuery();*/	
			
			Statement st = db.createStatement();
			ResultSet rs =  st.executeQuery(sql);
			
			while(rs.next()){
				System.out.println("colum 1 = "+rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public int excuteUpdate(String sql){
		int result =0;
		try {
			Statement st = db.createStatement();
			result = st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
