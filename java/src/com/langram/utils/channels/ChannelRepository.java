package com.langram.utils.channels;

import com.langram.utils.exchange.database.DatabaseStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
	public Channel retrieve() {
		return null;
	}


}
