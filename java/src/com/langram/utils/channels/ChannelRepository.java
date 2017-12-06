package com.langram.utils.channels;

import com.langram.utils.exchange.database.DatabaseStore;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChannelRepository implements RepositoryInterface<Channel>
{
	private static DatabaseStore db = DatabaseStore.getInstance();


	@Override
	public void store(Channel c) {

		String sql = "INSERT INTO channel(id,channelName,ipAddress) VALUES(?,?,?)";

		PreparedStatement pstmt = db.preparedStatement(sql);

		try {
			pstmt.setInt(1, 10);
			pstmt.setString(2, c.getChannelName());
			pstmt.setString(3, c.getIpAddress());
			pstmt.execute();
		} catch (SQLException e) {
			System.out.println("ICI");
			e.printStackTrace();
		}
	}

	@Override
	public Channel retrieve() {
		return null;
	}
}
