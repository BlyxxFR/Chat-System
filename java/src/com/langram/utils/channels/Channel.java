package com.langram.utils.channels;

import java.io.Serializable;

public class Channel implements Serializable
{
	private String ipAddress;
	private String channelName;

	public Channel(String n, String ip)
	{
		this.channelName = n;
		this.ipAddress = ip;
	}

	public String getChannelName() {
		return this.channelName;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

}
