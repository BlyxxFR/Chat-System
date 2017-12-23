package com.langram.utils.channels;

import com.langram.utils.exchange.database.DatabaseStore;
import com.langram.utils.messages.FileMessage;
import com.langram.utils.messages.Message;
import com.langram.utils.messages.TextMessage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class MessageRepository implements RepositoryInterface<Message>
{
	private static DatabaseStore db = DatabaseStore.getInstance();
	private static MessageRepository instance = new MessageRepository();

	public static MessageRepository getInstance() {
		return instance;
	}

	@Override
	public void store(Message message) {
		store(message, false);
	}

	public void store(Message message, boolean sent) {
		switch (message.getMessageType())
		{
			case TEXT_MESSAGE:
				this.storeTextMessage((TextMessage) message, sent);
				break;
			case FILE_MESSAGE:
				this.storeFileMessage((FileMessage) message, sent);
				break;
		}
	}

	private void storeTextMessage(TextMessage message, boolean sent)
	{
		String sql = "INSERT INTO message(messageType, message_date, senderName, content, channelID, sent) VALUES(?, ?, ?, ?, ?, ?)";
		try {
			Connection conn = db.connect();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, message.getMessageType().toString());
			pstmt.setString(2, message.getDate());
			pstmt.setString(3, message.getSenderName());
			pstmt.setString(4, message.getText());
			pstmt.setString(5, ChannelRepository.getInstance().getChannelUUID(message.getChannel().getChannelName()).toString());
			pstmt.setInt(6, (sent ? 1 : 0));
			pstmt.execute();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void storeFileMessage(FileMessage message, boolean sent)
	{
		throw new NotImplementedException();
	}

	@Override
	public ArrayList<Message> retrieveAll() {
		ArrayList<Message> messagesList = new ArrayList<>();
		try {
			ResultSet rs = getResultSet("SELECT id, messageType, message_date, senderName, content, channelID FROM message");
			messagesList = performRetrieve(rs);
			rs.close();
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return messagesList;
	}

	public ArrayList<Message> retrieveMessagesFromChannel(UUID idChannel) {
		ArrayList<Message> messagesList = new ArrayList<>();

		try {
			ResultSet rs = getResultSet("SELECT id, messageType, message_date, senderName, content, channelID, sent FROM message WHERE channelID = ?", new String[]{idChannel.toString()});
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					Message m = null;
					String channelID = rs.getString("channelID");
					Channel c = ChannelRepository.getInstance().getChannelWithUUID(channelID);
					if (Objects.equals(rs.getString("messageType"), Message.MessageType.TEXT_MESSAGE.toString())) {
						SimpleDateFormat parser = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.UK);
						try {
							java.util.Date date = parser.parse(rs.getString("message_date"));
							m = new TextMessage(
							        rs.getString("id"),
									rs.getString("senderName"),
									rs.getString("content"),
									date,
									c
							);
						} catch (ParseException e) {
							e.printStackTrace();
						}

					} else if (Objects.equals(rs.getString("messageType"), Message.MessageType.FILE_MESSAGE.toString())) {
						m = new FileMessage(null);
					}
					messagesList.add(m);
				}
			}
			rs.close();
		} catch(SQLException e){
			e.printStackTrace();
		}

		return messagesList;
	}

	private ResultSet getResultSet(String sql) throws SQLException {
		Connection conn = db.connect();
		Statement stmt = conn.createStatement();
		return stmt.executeQuery(sql);
	}

	private ResultSet getResultSet(String sql, String[] args) throws SQLException {
		PreparedStatement pstmt = db.connect().prepareStatement(sql);
		for (int i = 1; i <= args.length; i++) {
			pstmt.setString(i, args[i - 1]);
		}
		return pstmt.executeQuery();
	}

    public ArrayList<Message> retrieveNotSentMessages() {
		ArrayList<Message> messagesList = new ArrayList<>();
		try {
			ResultSet rs = getResultSet("SELECT id, messageType, message_date, senderName, content, channelID FROM message WHERE sent = 0");
			messagesList = performRetrieve(rs);
			rs.close();
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return messagesList;
    }

    private ArrayList<Message> performRetrieve(ResultSet rs) throws SQLException, ParseException {
		ArrayList<Message> messagesList = new ArrayList<>();
		while (rs.next()) {
			Message m = null;
			String channelID  = rs.getString("channelID");
			Channel c = ChannelRepository.getInstance().getChannelWithUUID(channelID);
			if(Objects.equals(rs.getString("messageType"), Message.MessageType.TEXT_MESSAGE.toString()))
			{
				SimpleDateFormat parser = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.UK);
				java.util.Date date = parser.parse(rs.getString("message_date"));
				m = new TextMessage(
				        rs.getString("id"),
						rs.getString("senderName"),
						rs.getString("content"),
						date,
						c
				);
			}else if(Objects.equals(rs.getString("messageType"), Message.MessageType.FILE_MESSAGE.toString())){
				m = new FileMessage(null);
			}

			messagesList.add(m);
		}
		return messagesList;
	}

    public void updateSentStatus(String UUID, boolean status) {
        try {
            PreparedStatement pstmt = db.connect().prepareStatement("UPDATE message SET sent = ? WHERE id = ?");
            pstmt.setInt(1, status ? 1 : 0);
            pstmt.setString(2, UUID);
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
