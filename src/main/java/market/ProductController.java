package market;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductController {
	
	BoothController booths;
	
	ProductController(BoothController boothController) throws SQLException {
		this.booths = boothController;
		checkProductTable();
	}
	
	private void checkProductTable() throws SQLException {
		booths.connect();
		PreparedStatement pstmt = booths.conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS Product " +
						"(productid INTEGER primary key AUTOINCREMENT, " +
						"ownerID INTEGER, " +
						"name TEXT, " +
						"crowns INTEGER, " +
						"stars INTEGER, " +
						"stock INTEGER, " +
						"auto INTEGER, " +
						"open INTEGER DEFAULT 0)");
		pstmt.execute();
		pstmt.close();
	}
	
	public long registerProduct(long ownerid, String productName, int crowns, int stars, int stock, boolean autotrade, int productidbitlength) {
		try {
			if(stock < -1) return 0;
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"INSERT INTO Product (productid, ownerID, name, crowns, stars, stock, auto) " +
							"VALUES (?,?,?,?,?,?,?)");
			long pid = generateProductId(productidbitlength);
			if(pid == 0) return 0;
			pstmt.setLong(1,pid);
			pstmt.setLong(2,ownerid);
			pstmt.setString(3,productName);
			pstmt.setInt(4,crowns);
			pstmt.setInt(5,stars);
			pstmt.setInt(6,stock);
			pstmt.setBoolean(7,autotrade);
			pstmt.execute();
			pstmt.close();
			return pid;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return 0;
		}
	}
	
	public boolean dropProduct(long productid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"DELETE FROM Product WHERE productid = ?");
			pstmt.setLong(1, productid);
			boolean out = pstmt.executeUpdate() > 0;
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}
	
	public boolean rename(long productid, String name) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"UPDATE Product SET name = ? WHERE productid = ?");
			pstmt.setString(1, name);
			pstmt.setLong(2, productid);
			boolean out = pstmt.executeUpdate() > 0;
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}
	
	public boolean reprice(long productid, int crowns, int stars) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"UPDATE Product SET crowns = ?, stars = ? WHERE productid = ?");
			pstmt.setInt(1, crowns);
			pstmt.setInt(2, stars);
			pstmt.setLong(3, productid);
			boolean out = pstmt.executeUpdate() > 0;
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}
	
	public boolean changeAutotrade(long productid, boolean bool) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"UPDATE Product SET auto = ? WHERE productid = ?");
			pstmt.setBoolean(1, bool);
			pstmt.setLong(2, productid);
			boolean out = pstmt.executeUpdate() > 0;
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}
	
	public boolean open(long productid, boolean open) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"UPDATE Product SET open = ? WHERE productid = ?");
			pstmt.setBoolean(1, open);
			pstmt.setLong(2, productid);
			boolean out = pstmt.executeUpdate() > 0;
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}
	
	public boolean isOpen(long productid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT open FROM Product WHERE productid = ?");
			pstmt.setLong(1, productid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.isClosed()) return false;
			boolean out = rs.getBoolean("open");
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}
	
	public String getName(long productid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT name FROM Product WHERE productid = ?");
			pstmt.setLong(1, productid);
			String out = pstmt.executeQuery().getString("name");
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			if(throwables.getMessage().equals("ResultSet closed")) return null;
			throwables.printStackTrace();
			return null;
		}
	}
	
	public boolean checkOwnership(long userid, long productid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT ownerID FROM Product WHERE productid = ?");
			pstmt.setLong(1, productid);
			boolean out = pstmt.executeQuery().getLong("ownerid") == userid;
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			if(throwables.getMessage().equals("ResultSet closed")) return false;
			throwables.printStackTrace();
			return false;
		}
	}
	
	public long getOwner(long productid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT ownerID FROM Product WHERE productid = ?");
			pstmt.setLong(1, productid);
			long out = pstmt.executeQuery().getLong("ownerID");
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			if(throwables.getMessage().equals("ResultSet closed")) return 0;
			throwables.printStackTrace();
			return 0;
		}
	}
	
	
	//STOCK MANIPULATION
	public boolean restock(long productid, int stock) {
		try {
			if(stock < -1) return false;
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"UPDATE Product SET stock = ? WHERE productid = ?");
			pstmt.setInt(1, stock);
			pstmt.setLong(2, productid);
			boolean out = pstmt.executeUpdate() > 0;
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}
	public boolean removeStock(long productid, int amount) {
		try {
			if(amount < 1) return false;
			int stock = getStock(productid);
			if(stock < -1) return false;
			if(stock == -1) return true;
			if(stock < amount) return false;
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"UPDATE Product SET stock = ? WHERE productid = ?");
			pstmt.setInt(1, stock - amount);
			pstmt.setLong(2, productid);
			boolean out = pstmt.executeUpdate() > 0;
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
	}
	public boolean hasStock(long productid, int amount) {
		int stock = getStock(productid);
		return stock != -2 && stock != 0 && (stock == -1 || stock >= amount);
	}
	public int getStock(long productid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT stock FROM Product WHERE productid = ?");
			pstmt.setLong(1, productid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.isClosed()) return -2;
			int out = rs.getInt("stock");
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return 0;
		}
	}
	
	//CONTAINER
	@Nullable
	public ProductContainer getProduct(long productid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT * FROM Product WHERE productid = ?");
			pstmt.setLong(1, productid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.isClosed()) return null;
			ProductContainer out = new ProductContainer(
						rs.getLong("productid"),
						rs.getLong("ownerid"),
						rs.getString("name"),
						rs.getInt("crowns"),
						rs.getInt("stars"),
						rs.getInt("stock"),
						rs.getBoolean("auto"),
						rs.getBoolean("open"));
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return null;
		}
	}
	
	public List<ProductContainer> getProducts(long ownerid) {
		ArrayList<ProductContainer> out = new ArrayList<>();
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT * FROM Product WHERE ownerID = ?");
			pstmt.setLong(1, ownerid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.isClosed()) return out;
			while(rs.next()) {
				out.add(new ProductContainer(
						rs.getLong("productid"),
						rs.getLong("ownerid"),
						rs.getString("name"),
						rs.getInt("crowns"),
						rs.getInt("stars"),
						rs.getInt("stock"),
						rs.getBoolean("auto"),
						rs.getBoolean("open")
				));
			}
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return out;
		}
	}
	
	public static class ProductContainer {
		
		public long productid, ownerid;
		public boolean open, auto;
		public int stock, stars, crowns;
		public String name;
		
		ProductContainer(long productid, long ownerid, String name, int crowns, int stars, int stock, boolean auto, boolean open) {
			this.productid = productid;
			this.ownerid = ownerid;
			this.name = name;
			this.crowns = crowns;
			this.stars = stars;
			this.stock = stock;
			this.auto = auto;
			this.open = open;
		}
		
	}
	
	//INTERNAL HELPER
	private long generateProductId(int productidbitlength) {
		return booths.generateGenericId("Product", "productid", productidbitlength);
	}
	
}
