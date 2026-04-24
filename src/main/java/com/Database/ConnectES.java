package com.Database;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.client.transport.TransportClient;

import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ConnectES
{
	public TransportClient client;

	private ConnectES()
	{
		try	{
			client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		}

		catch (Exception e)	{
		}
	}

	private static ConnectES es;

	public static ConnectES getInstance()
	{
		if (es == null)
		{
			synchronized (ConnectES.class)
			{
				if (es==null)
					es = new ConnectES();
			}
		}
		return es;
	}
}
