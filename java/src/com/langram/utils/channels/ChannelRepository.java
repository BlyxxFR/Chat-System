package com.langram.utils.channels;

import com.langram.utils.exchange.database.DatabaseStore;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class ChannelRepository implements RepositoryInterface<Channel> {
    private static DatabaseStore db = DatabaseStore.getInstance();
    private static ChannelRepository instance = new ChannelRepository();
    private Channel currentChannel = null;

    public static ChannelRepository getInstance() {
        return instance;
    }

    @Override
    public void store(Channel c) {
        this.store(c, false);
    }

    public void store(Channel c, boolean isPrivate) {
        String sql = "INSERT INTO channel(id, channelName, ipAddress, active, isPrivate) VALUES(?,?,?,?,?)";
        try {
            Connection conn = db.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, c.getChannelName());
            pstmt.setString(3, c.getIpAddress());
            pstmt.setInt(4, 0);
            pstmt.setInt(5, (isPrivate ? 1 : 0));
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Channel> retrieveAll() {
        ArrayList<Channel> channelsList = new ArrayList<>();
        try {
            ResultSet rs = getResultSet("SELECT channelName, ipAddress, active, isPrivate FROM channel");
            while (rs.next()) {
                if (rs.getInt("isPrivate") == 0) {
                    Channel channel = new Channel(rs.getString("channelName"), rs.getString("ipAddress"));
                    channelsList.add(channel);
                    if (rs.getInt("active") == 1) {
                        currentChannel = channel;
                    }
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return channelsList;
    }

    public Channel getChannelWithUUID(String id) {
        Channel channel = null;
        try {
            ResultSet rs = getResultSet("SELECT channelName, ipAddress, active FROM channel WHERE id = ?", new String[]{id});

            if (rs.next()) {
                channel = new Channel(rs.getString("channelName"), rs.getString("ipAddress"));
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public Channel getChannelWithIP(String ip) {
        Channel channel = null;
        try {
            ResultSet rs = getResultSet("SELECT channelName, ipAddress, active FROM channel WHERE ipAddress = ?", new String[]{ip});
            if (rs.next()) {
                channel = new Channel(rs.getString("channelName"), rs.getString("ipAddress"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public Channel getChannelWithName(String name) {
        Channel channel = null;
        try {
            ResultSet rs = getResultSet("SELECT channelName, ipAddress, active FROM channel WHERE channelName = ?", new String[]{name});
            if (rs.next()) {
                channel = new Channel(rs.getString("channelName"), rs.getString("ipAddress"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return channel;
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

    public boolean isConnectedToChannel(String ipAddress) {
        try {
            ResultSet rs = getResultSet("SELECT id FROM channel WHERE ipAddress = ?", new String[]{ipAddress});
            boolean result = rs.isBeforeFirst();
            rs.close();
            return result;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public String getChannelIP(String channelName) {
        try {
            ResultSet rs = getResultSet("SELECT ipAddress FROM channel WHERE channelName = ?", new String[]{channelName});
            if (rs.isBeforeFirst()) {
                rs.next();
                String ipAddress = rs.getString("ipAddress");
                rs.close();
                return ipAddress;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "none";
    }

    public void switchToChannel(String channelName) {
        try {
            Statement stmt = db.connect().createStatement();
            stmt.execute("UPDATE channel SET active = 0 WHERE active = 1 AND isPrivate = 0");
            stmt.close();
            PreparedStatement pstmt = db.connect().prepareStatement("UPDATE channel SET active = 1 WHERE channelName = ? AND isPrivate = 0");
            pstmt.setString(1, channelName);
            pstmt.execute();
            pstmt.close();
            ResultSet rs = getResultSet("SELECT ipAddress FROM channel WHERE channelName = ?", new String[]{channelName});
            rs.next();
            this.currentChannel = new Channel(channelName, rs.getString("ipAddress"));
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteChannel(String channelName) {

    }


    public boolean isActiveChannel(String ipAddress) {
        return currentChannel != null && this.currentChannel.getIpAddress().equals(ipAddress);
    }

    public Channel getCurrentChannel() {
        if (this.currentChannel == null) {
            try {
                ResultSet rs = getResultSet("SELECT channelName, ipAddress FROM channel WHERE active = 1");
                if (rs.isBeforeFirst()) {
                    rs.next();
                    this.currentChannel = new Channel(rs.getString("channelName"), rs.getString("ipAddress"));
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this.currentChannel;
    }

    public UUID getChannelUUID(String channelName) {
        UUID id = null;
        try {
            ResultSet rs = getResultSet("SELECT id FROM channel WHERE channelName = ?", new String[]{channelName});
            if (rs.isBeforeFirst()) {
                rs.next();
                id = UUID.fromString(rs.getString("id"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void updateChannelIP(String channelName, String senderAddress) {
        try {
            PreparedStatement pstmt = db.connect().prepareStatement("UPDATE channel SET ipAddress = ? WHERE channelName = ?");
            pstmt.setString(1, senderAddress);
            pstmt.setString(2, channelName);
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
