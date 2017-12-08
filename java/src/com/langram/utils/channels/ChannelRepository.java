package com.langram.utils.channels;

import com.langram.utils.exchange.database.DatabaseStore;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class ChannelRepository implements RepositoryInterface<Channel>
{
	private static DatabaseStore db = DatabaseStore.getInstance();

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

	public boolean channelExistsWIthIp(String ip) {
		String sql = "SELECT id FROM channel";

		try (Connection conn = db.connect();
			 Statement stmt  = conn.createStatement();
			 ResultSet rs    = stmt.executeQuery(sql)){

			if(rs)
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void switchActiveChannel(UUID activeChannelID)
	{

	}

	public void deleteChannel(UUID channel)
	{

	}




}
