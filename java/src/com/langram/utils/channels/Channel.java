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
	private String ipAdress;
	private String identifiant;

	public Channel(String n, String ip)
	{
		this.id = UUID.randomUUID();
		this.identifiant = n;
		this.ipAdress = ip;

		System.out.println("Nouveau Channel : ["
				+ this.id + "] "
				+ this.identifiant + " "
				+ this.ipAdress);
	}

	public String getIdentifiant() {
		return this.identifiant;
	}

	public String getIpAdress() {
		return this.ipAdress;
	}

	public UUID getId() {
		return this.id;
	}
}
