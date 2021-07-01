package market;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class RequestController {
	
	BoothController booths;
	
	RequestController(BoothController boothController) throws SQLException {
		this.booths = boothController;
		checkRequestTable();
	}
	
	private void checkRequestTable() throws SQLException {
		booths.connect();
		PreparedStatement pstmt = booths.conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS Request " +
						"(requestid INTEGER, " +
						"productid INTEGER, " +
						"ownerid INTEGER, " +
						"buyerid INTEGER, " +
						"crowns INTEGER, " +
						"stars INTEGER, " +
						"amount INTEGER)"
		);
		pstmt.execute();
		pstmt.close();
	}
	
	public long createRequest(long productid, long customerid, long ownerid, int crowns, int stars, int amount, int requestidbitlength) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"INSERT INTO Request (requestid, productid, ownerid, buyerid, crowns, stars, amount) " +
							"VALUES (?,?,?,?,?,?,?)");
			long rid = generateRequestId(requestidbitlength);
			if(rid == 0) return 0;
			pstmt.setLong(1,rid);
			pstmt.setLong(2,productid);
			pstmt.setLong(3,ownerid);
			pstmt.setLong(4,customerid);
			pstmt.setInt(5,crowns);
			pstmt.setInt(6,stars);
			pstmt.setInt(7,amount);
			pstmt.execute();
			pstmt.close();
			return rid;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return 0;
		}
	}
	
	public boolean dropRequest(long requestid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"DELETE FROM Request WHERE requestid = ?");
			pstmt.setLong(1, requestid);
			boolean out = pstmt.executeUpdate() > 0;
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}
	public long getTrader(long requestid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT ownerID FROM Request WHERE requestid = ?");
			pstmt.setLong(1, requestid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.isClosed()) return 0;
			long out = rs.getLong("ownerID");
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return 0;
		}
	}

//	PROBABLY NOT NEEDED ANYMORE
//
//	public RequestContainer getRequest(long requestid) {
//		try {
//			booths.connect();
//			PreparedStatement pstmt = booths.conn.prepareStatement(
//					"SELECT * FROM Request WHERE requestid = ?");
//			pstmt.setLong(1, requestid);
//			ResultSet rs = pstmt.executeQuery();
//			if(rs.isClosed()) return null;
//			RequestContainer out = new RequestContainer(
//					rs.getLong("requestid"),
//					rs.getLong("productid"),
//					rs.getLong("buyerid"),
//					rs.getInt("crowns"),
//					rs.getInt("stars"),
//					rs.getInt("amount")
//			);
//			pstmt.close();
//			return out;
//		} catch(SQLException throwables) {
//			throwables.printStackTrace();
//			return null;
//		}
//	}
	
	public RequestContainer getRequest(long requestid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT * FROM Request WHERE requestid = ?");
			pstmt.setLong(1, requestid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.isClosed()) return null;
			RequestContainer out = new RequestContainer(
					rs.getLong("requestid"),
					rs.getLong("productid"),
					rs.getLong("buyerid"),
					rs.getInt("crowns"),
					rs.getInt("stars"),
					rs.getInt("amount")
			);
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return null;
		}
	}
	public HashMap<Long, RequestContainer> getRequestsByCustomer(long buyerid) {
		HashMap<Long, RequestContainer> out = new HashMap<>();
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT * FROM Request WHERE buyerid = ?");
			pstmt.setLong(1, buyerid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.isClosed()) return null;
			while(rs.next()) {
				out.put(rs.getLong("requestid"),
						new RequestContainer(
								rs.getLong("requestid"),
								rs.getLong("productid"),
								rs.getLong("buyerid"),
								rs.getInt("crowns"),
								rs.getInt("stars"),
								rs.getInt("amount")
						));
			}
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return null;
		}
	}
	public HashMap<Long, RequestContainer> getRequestsByTrader(long ownerid) {
		HashMap<Long, RequestContainer> out = new HashMap<>();
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT * FROM Request WHERE ownerID = ?");
			pstmt.setLong(1, ownerid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.isClosed()) return out;
			while(rs.next()) {
				out.put(rs.getLong("requestid"),
						new RequestContainer(
							rs.getLong("requestid"),
							rs.getLong("productid"),
							rs.getLong("buyerid"),
							rs.getInt("crowns"),
							rs.getInt("stars"),
							rs.getInt("amount")
				));
			}
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return out;
		}
	}
	
	private long generateRequestId(int requestidbitlength) {
		return booths.generateGenericId("Request", "requestid", requestidbitlength);
	}
	
	public static class RequestContainer {
		
		public final long requestid, productid, customerid;
		public final int crowns, stars, amount;
		
		public RequestContainer(long requestid, long productid, long customerid, int crowns, int stars, int amount) {
			this.requestid = requestid;
			this.productid = productid;
			this.customerid = customerid;
			this.crowns = crowns;
			this.stars = stars;
			this.amount = amount;
		}
		
	}
	
}
