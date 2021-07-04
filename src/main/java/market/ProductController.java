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
		checkForeignProductTable();
	}
	
	private void checkProductTable() throws SQLException {
		booths.connect();
		PreparedStatement pstmt = booths.conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS Product " +
						"(productid INTEGER primary key, " +
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
	private void checkForeignProductTable() throws SQLException {
		booths.connect();
		PreparedStatement pstmt = booths.conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS ForeignProduct " +
						"(productid INTEGER primary key, " +
						"itemid INTEGER, " +
						"ownerID INTEGER, " +
						"crowns INTEGER, " +
						"stars INTEGER, " +
						"stock INTEGER DEFAULT 0, " +
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
	public long registerForeignProduct(long itemid, long ownerid, int crowns, int stars, boolean autotrade, int productidbitlength) {
		if(!isAdded(itemid, ownerid))
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"INSERT INTO ForeignProduct (productid, itemid, ownerID, crowns, stars, auto) " +
							"VALUES (?,?,?,?,?,?)");
			long pid = generateProductId(productidbitlength);
			if(pid == 0) return 0;
			pstmt.setLong(1,pid);
			pstmt.setLong(2,itemid);
			pstmt.setLong(3,ownerid);
			pstmt.setInt(4,crowns);
			pstmt.setInt(5,stars);
			pstmt.setBoolean(6,autotrade);
			pstmt.execute();
			pstmt.close();
			return pid;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return 0;
		}
		else return 0;
	}
	
	public boolean dropProduct(long productid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"DELETE FROM Product WHERE productid = ?");
			pstmt.setLong(1, productid);
			boolean out = pstmt.executeUpdate() > 0;
			pstmt.close();
			if(!out) {
				pstmt = booths.conn.prepareStatement(
						"DELETE FROM ForeignProduct WHERE productid = ?");
				pstmt.setLong(1, productid);
				out = pstmt.executeUpdate() > 0;
				pstmt.close();
			}
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
			if(!out) {
				pstmt = booths.conn.prepareStatement(
						"UPDATE ForeignProduct SET crowns = ?, stars = ? WHERE productid = ?");
				pstmt.setInt(1, crowns);
				pstmt.setInt(2, stars);
				pstmt.setLong(3, productid);
				out = pstmt.executeUpdate() > 0;
				pstmt.close();
			}
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
			if(!out) {
				pstmt = booths.conn.prepareStatement(
						"UPDATE ForeignProduct SET auto = ? WHERE productid = ?");
				pstmt.setBoolean(1, bool);
				pstmt.setLong(2, productid);
				out = pstmt.executeUpdate() > 0;
				pstmt.close();
			}
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
			if(!out) {
				pstmt = booths.conn.prepareStatement(
						"UPDATE ForeignProduct SET open = ? WHERE productid = ?");
				pstmt.setBoolean(1, open);
				pstmt.setLong(2, productid);
				out = pstmt.executeUpdate() > 0;
				pstmt.close();
			}
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
			if(rs.isClosed()) {
				pstmt = booths.conn.prepareStatement(
						"SELECT open FROM ForeignProduct WHERE productid = ?");
				pstmt.setLong(1, productid);
				rs = pstmt.executeQuery();
				if(rs.isClosed()) return false;
			}
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
					"SELECT name FROM Product LEFT JOIN ForeignProduct ON Product.productid = ForeignProduct.itemid WHERE ForeignProduct.productid = ? OR Product.productid = ?");
			pstmt.setLong(1, productid);
			pstmt.setLong(2, productid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.isClosed()) return null;
			String out = rs.getString("name");
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return null;
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
	
	//FOREIGN STUFF
	public boolean isAdded(long productid, long ownerid) {
		try {
			booths.connect();
			PreparedStatement pstmt = booths.conn.prepareStatement(
					"SELECT * FROM Product INNER JOIN ForeignProduct ON Product.productid = ForeignProduct.itemid WHERE ForeignProduct.ownerID = ? AND (ForeignProduct.productid = ? OR Product.productid = ?)");
			pstmt.setLong(1, ownerid);
			pstmt.setLong(2, productid);
			pstmt.setLong(3, productid);
			ResultSet rs = pstmt.executeQuery();
			boolean out = !rs.isClosed();
			pstmt.close();
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return false;
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
			if(!out) {
				pstmt = booths.conn.prepareStatement(
						"UPDATE ForeignProduct SET stock = ? WHERE productid = ?");
				pstmt.setInt(1, stock);
				pstmt.setLong(2, productid);
				out = pstmt.executeUpdate() > 0;
				pstmt.close();
			}
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
			if(!out) {
				pstmt = booths.conn.prepareStatement(
						"UPDATE ForeignProduct SET stock = ? WHERE productid = ?");
				pstmt.setInt(1, stock - amount);
				pstmt.setLong(2, productid);
				out = pstmt.executeUpdate() > 0;
				pstmt.close();
			}
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
			if(rs.isClosed()) {
				pstmt.close();
				pstmt = booths.conn.prepareStatement(
						"SELECT stock FROM ForeignProduct WHERE productid = ?");
				pstmt.setLong(1, productid);
				rs = pstmt.executeQuery();
				if(rs.isClosed()) return -2;
			}
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
			boolean isForeign = false;
			if(rs.isClosed()) {
				pstmt = booths.conn.prepareStatement(
						"SELECT * FROM ForeignProduct WHERE productid = ?");
				pstmt.setLong(1, productid);
				rs = pstmt.executeQuery();
				if(rs.isClosed()) return null;
				isForeign = true;
			}
			ProductContainer out = new ProductContainer(
				rs.getLong("productid"),
				isForeign ? rs.getLong("itemid") : 0,
				rs.getLong("ownerid"),
				isForeign ? getName(rs.getLong("itemid")) : rs.getString("name"),
				rs.getInt("crowns"),
				rs.getInt("stars"),
				rs.getInt("stock"),
				rs.getBoolean("auto"),
				rs.getBoolean("open"),
				isForeign);
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
			while(rs.next()) {
				out.add(new ProductContainer(
						rs.getLong("productid"),
						rs.getLong("itemid"),
						rs.getLong("ownerid"),
						rs.getString("name"),
						rs.getInt("crowns"),
						rs.getInt("stars"),
						rs.getInt("stock"),
						rs.getBoolean("auto"),
						rs.getBoolean("open"),
						false
				));
			}
			pstmt.close();
			pstmt = booths.conn.prepareStatement(
					"SELECT * FROM ForeignProduct WHERE ownerID = ?");
			pstmt.setLong(1, ownerid);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				out.add(new ProductContainer(
						rs.getLong("productid"),
						rs.getLong("itemid"),
						rs.getLong("ownerid"),
						rs.getString("name"),
						rs.getInt("crowns"),
						rs.getInt("stars"),
						rs.getInt("stock"),
						rs.getBoolean("auto"),
						rs.getBoolean("open"),
						true
				));
			}
			return out;
		} catch(SQLException throwables) {
			throwables.printStackTrace();
			return out;
		}
	}
	
	public boolean isForeign(long itemid) {
	
	}
	
	public static class ProductContainer {
		
		public final long productid, itemid, ownerid;
		public final boolean open, auto, isForeign;
		public final int stock, stars, crowns;
		public final String name;
		
		ProductContainer(long productid, long itemid, long ownerid, String name, int crowns, int stars, int stock, boolean auto, boolean open, boolean isForeign) {
			this.productid = productid;
			this.itemid = itemid;
			this.ownerid = ownerid;
			this.name = name;
			this.crowns = crowns;
			this.stars = stars;
			this.stock = stock;
			this.auto = auto;
			this.open = open;
			this.isForeign = isForeign;
		}
		
	}
	
	//INTERNAL HELPER
	private long generateProductId(int productidbitlength) {
		return booths.generateGenericId("Product", "productid", productidbitlength);
	}
	private long generateForeignId(int productidbitlength) {
		return booths.generateGenericId("ForeignProduct", "productid", productidbitlength);
	}
	
}
