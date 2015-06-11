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

import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.jdbc.MysqlConnection;
import org.apache.zeppelin.jdbc.OracleConnection;
import org.junit.Test;

public class DbConnectionTest {

	@Test
	public void testMysql() {
		String port = "3306";
		String host = "172.18.1.46";
		String user = "root";
		String passwd = "qianfendian";
		MysqlConnection conn = new MysqlConnection(host, port, user, passwd);
		conn.open();
		String sql = "select name,age from test.T_TEST";
		InterpreterResult r = conn.executeSql(sql);
		System.out.println(r.message());
	}
	
	@Test
	public void testOracle() {
		String port = "1521:orcl";
		String host = "192.168.11.153";
		String user = "jupiter";
		String passwd = "jupiter";
		OracleConnection conn = new OracleConnection(host, port, user, passwd);
		conn.open();
		String sql = "select * from dual";
		InterpreterResult r = conn.executeSql(sql);
		System.out.println(r.message());
	}
	
	@Test
  public void testOracleZhx() {
    String port = "1521:orcl";
    String host = "10.6.109.61";
    String user = "jupiter";
    String passwd = "jupiter";
    OracleConnection conn = new OracleConnection(host, port, user, passwd);
    conn.open();
    String sql = "select * from bank";
    InterpreterResult r = conn.executeSql(sql);
    System.out.println(r.message());
  }

}
