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
        String sql = "INSERT INTO channel(id, channelName, ipAddress, active) VALUES(?,?,?, ?)";
        try {
            Connection conn = db.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, c.getChannelName());
            pstmt.setString(3, c.getIpAddress());
            pstmt.setInt(4, 0);
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
            ResultSet rs = getResultSet("SELECT channelName, ipAddress, active FROM channel");
            while (rs.next()) {
                Channel channel = new Channel(rs.getString("channelName"), rs.getString("ipAddress"));
                channelsList.add(channel);
                if (rs.getInt("active") == 1) {
                    currentChannel = channel;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return channelsList;
    }

    private ResultSet getResultSet(String sql) throws SQLException {
        Connection conn = db.connect();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    public boolean isConnectedToChannel(String ipAddress) {
        try {
            ResultSet rs = getResultSet("SELECT id FROM channel WHERE ipAddress = '" + ipAddress + "'");
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
            ResultSet rs = getResultSet("SELECT ipAddress FROM channel WHERE channelName = '" + channelName + "'");
            if(rs.isBeforeFirst()) {
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
            stmt.execute("UPDATE channel SET active = 0 WHERE active = 1");
            stmt.close();
            PreparedStatement pstmt = db.connect().prepareStatement("UPDATE channel SET active = 1 WHERE channelName = ?");
            pstmt.setString(1, channelName);
            pstmt.execute();
            stmt.close();
            ResultSet rs = getResultSet("SELECT ipAddress FROM channel WHERE channelName = '" + channelName + "'");
            rs.next();
            this.currentChannel = new Channel(channelName, rs.getString("ipAddress"));
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteChannel(String channelName) {

    }


    public boolean isActiveChannel(String channelName) {
        return false;
    }

    public Channel getCurrentChannel() {
        if(this.currentChannel == null) {
            try {
                ResultSet rs = getResultSet("SELECT channelName, ipAddress FROM channel WHERE active = 1");
                if(rs.isBeforeFirst()) {
                    rs.next();
                    this.currentChannel = new Channel(rs.getString("channelName"), rs.getString("ipAddress"));
                    System.out.println("jkloreerl");
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this.currentChannel;
    }
}
