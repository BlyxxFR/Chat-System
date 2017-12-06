package com.langram.utils.channels;

import java.util.UUID;

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
	private UUID id;
	private String ipAddress;
	private String channelName;

	public Channel(String n, String ip)
	{
		this.id = UUID.randomUUID();
		this.channelName = n;
		this.ipAddress = ip;

		System.out.println("Nouveau Channel : ["
				+ this.id + "] "
				+ this.channelName + " "
				+ this.ipAddress);
	}

	public String getChannelName() {
		return this.channelName;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public UUID getId() {
		return this.id;
	}
}
