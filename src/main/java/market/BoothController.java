package market;

import core.BotInstance;
import database.Accessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class BoothController extends Accessor {
	
	public final ProductController products;
	public final RequestController requests;
	
	public BoothController() throws SQLException {
		super("./data/booths.db");
		checkBoothTable();
		products = new ProductController(this);
		requests = new RequestController(this);
	}
	
	private void checkBoothTable() throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS Booth " +
						"(memberid INTEGER primary key, " +
						"name TEXT, " +
						"open TEXT)"
		);
		pstmt.execute();
		pstmt.close();
	}
	
	public String getName(long memberid) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(
					"SELECT name FROM Booth WHERE memberid = ?"
			);
			pstmt.setLong(1, memberid);
			return pstmt.executeQuery().getString("name");
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return null;
		}
	}
	
	public boolean isOpen(long memberid) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(
					"SELECT open FROM Booth WHERE memberid = ?"
			);
			pstmt.setLong(1, memberid);
			return Boolean.parseBoolean(pstmt.executeQuery().getString("open"));
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}
	
	public void changeName(long memberid, String name) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(
					"INSERT INTO Booth (memberid, name, open) " +
							"VALUES (?,?,'false')" +
							"ON CONFLICT (memberid)" +
							"DO UPDATE SET name = ?"
			);
			pstmt.setLong(1,memberid);
			pstmt.setString(2,name);
			pstmt.setString(3,name);
			pstmt.execute();
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	public void changeOpen(long memberid, boolean open) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(
					"INSERT INTO Booth (memberid, name, open) " +
							"VALUES (?,?,?)" +
							"ON CONFLICT (memberid)" +
							"DO UPDATE SET open = ?"
			);
			pstmt.setLong(1,memberid);
			pstmt.setString(2,String.valueOf(open));
			pstmt.setString(3,BotInstance.botInstance.jda.getUserById(memberid).getName() + "'s Booth");
			pstmt.setString(4,String.valueOf(open));
			pstmt.execute();
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	public long generateGenericId(String table, String column, int bitLength) {
		try {
			connect();
			PreparedStatement pstmt = conn.prepareStatement("SELECT " + column + " FROM " + table);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<Long> longs = new ArrayList<>();
			while(rs.next()) longs.add(rs.getLong(column));
			pstmt.close();
			Random random = new Random();
			bitLength = (int) Math.pow(2, 64-bitLength);
			long rand = random.nextLong() / bitLength;							//Just using ints because Discord sucks
			while(longs.contains(rand) || rand < 1) rand = random.nextInt();	//seriously, fuck Discord
			return rand;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return 0;
		}
	}
}
