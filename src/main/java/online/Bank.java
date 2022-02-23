package online;

import googledocs.Database;

import java.io.IOException;

public class Bank extends Database {
	
	private static final String SHEET = "ACCOUNTING";
	// COLUMNS:
	// ====================
	// 1 Account ID
	// 2 Account Type
	// 3 Bound Entity
	// 4 Balance
	// 5 Relations
	
	public double getBalance(int account) throws IOException {
		return Double.parseDouble(getField(SHEET, account, 4).toString().replace(',', '.'));
	}
	
	public boolean updateBalance(int account, double newBalance) {
		return false;
	}
	
}
