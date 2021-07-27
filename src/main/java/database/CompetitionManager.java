package database;

import java.sql.SQLException;

public class CompetitionManager extends Accessor {
	
	public CompetitionManager() {
		super("data/competitions.db");
		try {
			checkTables();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void checkTables() throws SQLException {
		conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS competition " +
						"(competitionid INTEGER primary key autoincrement, " +
						"title TEXT not null, " +
						"state INTEGER not null)"
		).execute();
		conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS competitor " +
						"(competitorid INTEGER primary key autoincrement, " +
						"competitionid INTEGER not null, " +
						"userid INTEGER not null," +
						"FOREIGN KEY(competitionid) REFERENCES competition(competitionid))"
		).execute();
		conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS match " +
						"(matchid INTEGER primary key autoincrement, " +
						"successor INTEGER, " +
						"competitionid INTEGER not null, " +
						"competitors TEXT not null, " +
						"FOREIGN KEY(competitionid) REFERENCES competition(competitionid))"
		).execute();
	}
	
	public boolean addCompetitor(int competitionid) {
		return false;
	}
	
	public static class CompetitionContainer {
	
	}
	
}
