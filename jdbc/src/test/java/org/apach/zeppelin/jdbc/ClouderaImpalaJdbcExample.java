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

import org.junit.Test;

public class ClouderaImpalaJdbcExample {

	// here is an example query based on one of the Hue Beeswax sample tables
	private static final String SELECT_SQL_STATEMENT = "SELECT id,name,no FROM test limit 10";
	private static final String CREATE_SQL_STATEMENT = "create table test (id int,name string,no int)";
	private static final String INSERT_SQL_STATEMENT = "insert into test (id,name,no)values(1,'huangjian',1)";

	// set the impalad host
	private static final String IMPALAD_HOST = "192.168.11.153";

	// port 21050 is the default impalad JDBC port
	private static final String IMPALAD_JDBC_PORT = "21050";

	private static final String CONNECTION_URL = "jdbc:hive2://" + IMPALAD_HOST
			+ ':' + IMPALAD_JDBC_PORT + "/;auth=noSasl";

	private static final String JDBC_DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";

	@Test
	public void create() {

		System.out.println("\n=============================================");
		System.out.println("Cloudera Impala JDBC Example");
		System.out.println("Using Connection URL: " + CONNECTION_URL);
		System.out.println("Running SQL: " + CREATE_SQL_STATEMENT);

		Connection con = null;

		try {

			Class.forName(JDBC_DRIVER_NAME);

			con = DriverManager.getConnection(CONNECTION_URL);

			System.out.println("\n== Begin create start ================");
			Statement stmt = con.createStatement();
			stmt.execute(CREATE_SQL_STATEMENT);
			System.out.println("== End create end ================\n\n");

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e) {
				// swallow
			}
		}
	}

	@Test
	public void insert() {

		System.out.println("\n=============================================");
		System.out.println("Cloudera Impala JDBC Example");
		System.out.println("Using Connection URL: " + CONNECTION_URL);
		System.out.println("Running SQL: " + INSERT_SQL_STATEMENT);

		Connection con = null;

		try {

			Class.forName(JDBC_DRIVER_NAME);

			con = DriverManager.getConnection(CONNECTION_URL);

			System.out.println("\n== Begin Insert start ================");
			Statement stmt = con.createStatement();
			stmt.execute(INSERT_SQL_STATEMENT);
			System.out.println("== End Insert end ================\n\n");

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e) {
				// swallow
			}
		}
	}

	public static void main(String[] args) {

		System.out.println("\n=============================================");
		System.out.println("Cloudera Impala JDBC Example");
		System.out.println("Using Connection URL: " + CONNECTION_URL);
		System.out.println("Running SQL: " + SELECT_SQL_STATEMENT);

		Connection con = null;

		try {

			Class.forName(JDBC_DRIVER_NAME);

			con = DriverManager.getConnection(CONNECTION_URL);

			Statement stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(SELECT_SQL_STATEMENT);

			System.out
					.println("\n== Begin Query Results ======================");

			// print the results to the console
			while (rs.next()) {
				// the example query returns one String column
				System.out.println(rs.getString(1));
			}

			System.out
					.println("== End Query Results =======================\n\n");

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e) {
				// swallow
			}
		}
	}
}