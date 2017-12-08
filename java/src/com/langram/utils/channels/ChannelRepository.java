package com.langram.utils.channels;

import com.langram.utils.exchange.database.DatabaseStore;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class ChannelRepository implements RepositoryInterface<Channel>
{
	private static DatabaseStore db = DatabaseStore.getInstance();
	private static ChannelRepository instance = new ChannelRepository();

	public static ChannelRepository getInstance() { return instance; }

	@Override
	public void store(Channel c) {

		String sql = "INSERT INTO channel(id, channelName, ipAddress, active) VALUES(?,?,?, ?)";

		try (Connection conn = db.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, c.getId().toString());
			pstmt.setString(2, c.getChannelName());
			pstmt.setString(3, c.getIpAddress());
			pstmt.setInt(4, c.isActive() ? 1 : 0);
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<Channel> retrieveAll() {
		return null;
	}

	private ResultSet getResultSet(String sql) throws SQLException {
        Connection conn = db.connect();
        Statement stmt  = conn.createStatement();
        return stmt.executeQuery(sql);
    }

	public boolean channelExists(String ipAddress) {
        try {
            return getResultSet("SELECT id FROM channel WHERE ipAddress = '" + ipAddress + "'").isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
	}


	public String getChannelIP(String channelName) {
        try {
            return getResultSet("SELECT ipAddress FROM channel WHERE channelName = '" + channelName + "'").getString(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return "none";
        }
    }

	public void switchToChannel(String channelName)
	{

	}

	public void deleteChannel(UUID channel)
	{

	}




}
