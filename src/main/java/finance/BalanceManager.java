package finance;

import database.Accessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BalanceManager extends Accessor {
	
	public BalanceManager() throws SQLException {
		super("data/finance.db");
		checkMoneyTable();
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
	
	public void eraseStars() {
		try {
			PreparedStatement pstmt = conn.prepareStatement("UPDATE Money SET balance2 = 0");
			pstmt.executeUpdate();
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	public void credit(long memberID, Currency currency, int amount) {
		if(amount < 0) {
			System.out.println("addBalance() with amount < 0, aborting");
			return;
		}
		setBalance(memberID, currency, getBalance(memberID, currency) + amount);
	}
	
	public void credit(long memberID, int crowns, int stars) {
		if(crowns < 0 || stars < 0) {
			System.out.println("addBalance() with crowns/stars < 0, aborting");
			return;
		}
		setBalance(memberID, getBalance(memberID, Currency.CROWNS) + crowns, getBalance(memberID, Currency.STARS) + stars);
	}
	
	public boolean withdraw(long memberID, Currency currency, int amount, boolean forceWithdrawal) {
		if(amount < 0) {
			System.out.println("withdraw() with amount < 0, aborting");
			return false;
		}
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
	
	public boolean withdraw(long memberID, int crowns, int stars, boolean forceWithdrawal) {
		if(crowns < 0 || stars < 0) {
			System.out.println("withdraw() with crowns/stars < 0, aborting");
			return false;
		}
		try {
			connect();
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
		if(forceWithdrawal) {
			setBalance(memberID, Currency.CROWNS, getBalance(memberID, Currency.CROWNS) - crowns);
			setBalance(memberID, Currency.STARS, getBalance(memberID, Currency.STARS) - stars);
			return true;
		} else {
			int crownbalance = getBalance(memberID, Currency.CROWNS);
			int starsbalance = getBalance(memberID, Currency.STARS);
			if(crowns > crownbalance) return false;
			if(stars > starsbalance) return false;
			setBalance(memberID, Currency.CROWNS, getBalance(memberID, Currency.CROWNS) - crowns);
			setBalance(memberID, Currency.STARS, getBalance(memberID, Currency.STARS) - stars);
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
				if(insertMember(memberID)) credit(memberID, currency, newBalance);
			}
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	public void setBalance(long memberID, int newCrowns, int newStars) {
		try {
			PreparedStatement pstmt = conn.prepareStatement("UPDATE Money SET balance1 = ?, balance2 = ? WHERE memberid = ?");
			pstmt.setInt(1, newCrowns);
			pstmt.setInt(2, newStars);
			pstmt.setLong(3, memberID);
			if(pstmt.executeUpdate() == 0) {
				pstmt.close();
				if(insertMember(memberID)) setBalance(memberID, newCrowns, newStars);
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
	
	public BankAccount getAccount(long buyerid) {
		try {
			connect();
			PreparedStatement pstmt = conn.prepareStatement(
					"SELECT * FROM Money WHERE memberid = ?");
			pstmt.setLong(1, buyerid);
			if(pstmt.execute()) {
				ResultSet rs = pstmt.getResultSet();
				if(rs.isClosed()) return null;
				else return new BankAccount(rs.getInt("balance1"), rs.getInt("balance2"));
			} else {
				System.out.println("Could not execute");
				return null;
			}
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return null;
		}
	}
	
	public static class BankAccount {
		
		public final int crowns, stars;
		
		BankAccount(int crowns, int stars) {
			this.crowns = crowns;
			this.stars = stars;
		}
		
	}
	
}