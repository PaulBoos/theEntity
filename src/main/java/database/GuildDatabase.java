package database;

import java.sql.*;

public class GuildDatabase extends Accessor {
	
	protected GuildDatabase(long guildID) {
		super("data/guilds/" + guildID + ".db");
	}
	
	public synchronized long getMember(long user) throws SQLException {
		connect();
		PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM memberID WHERE user = ?");
		pstmt.setLong(1, user);
		pstmt.execute();
		ResultSet rs = pstmt.getResultSet();
		try {
			return rs.getLong("memberID");
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
		return 0L;
	}
	
	public synchronized long getGuild(long member) throws SQLException {
		connect();
		PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM memberID WHERE memberID = ?");
		pstmt.setLong(1, member);
		pstmt.execute();
		ResultSet rs = pstmt.getResultSet();
		try {
			return rs.getLong("guildID");
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
		return 0L;
	}
	
	public synchronized long getUser(long member) throws SQLException {
		connect();
		PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM memberID WHERE memberID = ?");
		pstmt.setLong(1, member);
		pstmt.execute();
		ResultSet rs = pstmt.getResultSet();
		try {
			return rs.getLong("userID");
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
		return 0L;
	}
	
}
