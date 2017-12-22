package com.langram.utils.exchange.database;

import com.langram.utils.Settings;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This file is part of the project java.
 *
 * @author Guillaume
 * @version 1.0
 * @date 04/12/2017
 * @since 1.0
 */
public class DatabaseStore {
	private static final String JDBC_SQLITE = "jdbc:sqlite:";
	private static DatabaseStore databaseStore;
	private final String db_path;

	private DatabaseStore() {
		this.db_path = Settings.getInstance().getDatabasePath();
	}

	public static synchronized DatabaseStore getInstance() {
		if (databaseStore == null) {
			databaseStore = new DatabaseStore();
		}
		databaseStore.createSchema();
		return databaseStore;
	}

	private String getConnectionUrl() {
		return JDBC_SQLITE + this.db_path;
	}

	public Connection connect() {

		Connection conn = null;
		try {

			String url = this.getConnectionUrl();
			conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
			System.err.println(e.getMessage());
		}

		return conn;
	}

	private void createSchema() {
		// Create data directory
        (new File(Settings.getInstance().getDatabaseDirectory())).mkdirs();

        // SQL statement for creating a new table
		String sql = "CREATE TABLE IF NOT EXISTS channel (\n"
				+ "	id VARCHAR PRIMARY KEY,\n"
				+ "	channelName TEXT NOT NULL,\n"
				+ "	ipAddress VARCHAR(15),\n"
				+ " active INTEGER,\n"
				+ " isPrivate INTEGER\n"
				+ ");";

		String sql2 = "CREATE TABLE IF NOT EXISTS message (\n"
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT, \n"
				+ " messageType VARCHAR(30) NOT NULL, \n"
				+ " message_date DATETIME NOT NULL, \n"
				+ " senderName VARCHAR(30) NOT NULL, \n"
				+ " content TEXT,\n "
				+ " channelID String,\n"
                + " sent INTEGER\n"
				+ ");";

		try (Connection conn = this.connect()) {
			if (conn != null) {
				Statement stmt = conn.createStatement();
				stmt.execute(sql);
				stmt.execute(sql2);

				conn.close();
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}