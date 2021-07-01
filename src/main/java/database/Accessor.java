package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Accessor {
	
	
	private final String DATABASEPATH;
	public Connection conn;
	
	public Accessor(String path) {
		DATABASEPATH = "jdbc:sqlite:" + path;
		try {
			connect();
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	public void connect() throws SQLException {
		if(conn == null) {
			conn = DriverManager.getConnection(DATABASEPATH);
		}
	}
	
}
