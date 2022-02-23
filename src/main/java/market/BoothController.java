package market;

import core.BotInstance;
import database.Accessor;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
	
	
	public List<BoothPrint> getAllBooths() {
		try {
			connect();
			PreparedStatement pstmt = conn.prepareStatement(
					"SELECT * FROM Booth"
			);
			return getAllBoothsByStatement(pstmt);
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return null;
		}
	}
	public List<BoothPrint> getAllBooths(boolean requiredOpenState) {
		try {
			connect();
			PreparedStatement pstmt = conn.prepareStatement(
					"SELECT * FROM Booth WHERE open = ?"
			);
			pstmt.setString(1, String.valueOf(requiredOpenState));
			return getAllBoothsByStatement(pstmt);
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return null;
		}
	}
	@Nullable
	private List<BoothPrint> getAllBoothsByStatement(PreparedStatement pstmt) throws SQLException {
		ResultSet rs = pstmt.executeQuery();
		List<BoothPrint> output = new ArrayList<>();
		while(rs.next()) {
			output.add(new BoothPrint(
					rs.getLong("memberid"),
					rs.getString("name"),
					rs.getBoolean("open")
			));
		}
//		return output.size() > 0 ? output : null;
		return output;
	}
	
	
	public String getName(long memberid) {
		try {
			connect();
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
			connect();
			PreparedStatement pstmt = conn.prepareStatement(
					"SELECT open FROM Booth WHERE memberid = ?"
			);
			pstmt.setLong(1, memberid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.isClosed()) {
				changeOpen(memberid, false);
				return false;
			}
			return Boolean.parseBoolean(pstmt.executeQuery().getString("open"));
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}
	
	public void changeName(long memberid, String name) {
		try {
			connect();
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
			connect();
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
			int rand = random.nextInt() / bitLength;									//Just using ints because Discord sucks
			while(longs.contains((long) rand) || rand < 1) rand = random.nextInt();		//seriously, fuck Discord
			return rand;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return 0;
		}
	}
	
	public static class BoothPrint {
		
		public final long memberid;
		public final String name;
		public final boolean openState;
		
		public BoothPrint(long memberid, String name, boolean openState) {
			this.memberid = memberid;
			this.name = name;
			this.openState = openState;
		}
		
	}
	
}
