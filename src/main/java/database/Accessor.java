package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Accessor {
	
	
	private final String DATABASEPATH;
	protected Connection conn;
	
	protected Accessor(String path) {
		DATABASEPATH = path;
		try {
			connect();
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	protected void connect() throws SQLException {
		if(conn == null) {
			conn = DriverManager.getConnection(DATABASEPATH);
		}
	}
	
}
