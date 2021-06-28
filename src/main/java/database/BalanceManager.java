package database;

import core.Currency;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BalanceManager extends Accessor {
	
	public BalanceManager() {
		super("data/finance.db");
		try {
			checkMoneyTable();
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	private void checkMoneyTable() throws SQLException {
		conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS Money " +
						"(memberid INTEGER primary key, " +
						"balance1 INTEGER default 10, " +
						"balance2 INTEGER default 0)"
		).execute();
	}
	
	public int getBalance(long memberID, Currency currency) {
		try {
			connect();
			PreparedStatement pstmt = conn.prepareStatement(
					"SELECT * FROM Money WHERE memberid = ?");
			pstmt.setLong(1, memberID);
			if(pstmt.execute()) {
				ResultSet rs = pstmt.getResultSet();
				if(rs.isClosed()) return 0;
				else return rs.getInt("balance" + currency.id);
			} else System.out.println("Could not execute");
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
		return 0;
	}
	
	public void addBalance(long memberID, Currency currency, int amount) {
		setBalance(memberID, currency, getBalance(memberID, currency) + amount);
	}
	
	public boolean withdraw(long memberID, Currency currency, int amount, boolean forceWithdrawal) {
		try {
			connect();
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
		if(forceWithdrawal) {
			setBalance(memberID, currency, getBalance(memberID, currency) - amount);
			return true;
		} else {
			int balance = getBalance(memberID, currency);
			if(amount > balance) return false;
			setBalance(memberID, currency, getBalance(memberID, currency) - amount);
			return true;
		}
	}
	
	public void setBalance(long memberID, Currency currency, int newBalance) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(
					String.format("UPDATE Money SET balance%d = ? WHERE memberid = ?", currency.id));
			pstmt.setLong(1, newBalance);
			pstmt.setLong(2, memberID);
			if(pstmt.executeUpdate() == 0) {
				pstmt.close();
				if(insertMember(memberID)) setBalance(memberID, currency, newBalance);
			}
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	public boolean insertMember(long memberID) throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement(
				"INSERT INTO Money (memberid) VALUES (?)");
		pstmt.setLong(1, memberID);
		return pstmt.execute();
	}
	
}
//282551955975307264 (Meine ID)