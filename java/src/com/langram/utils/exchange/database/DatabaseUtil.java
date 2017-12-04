package com.langram.utils.exchange.database;

/**
 * This file is part of the project java.
 *
 * @author Guillaume
 * @version 1.0
 * @date 04/12/2017
 * @since 1.0
 */
public class DatabaseUtil
{
	private static DatabaseUtil databaseUtil;

	private DatabaseUtil()
	{

	}

	public static synchronized DatabaseUtil getInstance()
	{
		if(databaseUtil == null){
			databaseUtil = new DatabaseUtil();
		}
		return databaseUtil;
	}



}
