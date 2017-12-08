package com.langram.utils.channels;

/**
 * This file is part of the project java.
 *
 * @author Guillaume
 * @version 1.0
 * @date 04/12/2017
 * @since 1.0
 */
public class Channel
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
