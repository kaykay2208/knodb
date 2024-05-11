package com.data;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataWorker
{
	private static String filename = "keyMap.json";

	public static void initializeWorker ()
	{
		sync();
	}

	private static final ScheduledExecutorService scheduler =
		Executors.newScheduledThreadPool(1);

	public static void sync ()
	{
		final Runnable beeper = new Runnable()
		{
			public void run ()
			{
				syncDatatoDisk();
			}
		};
		System.out.println("going to initiate sync thread");
		final ScheduledFuture <?> beeperHandle = scheduler.scheduleAtFixedRate(beeper , 10 , 10 , SECONDS);
		System.out.println("initiated sync thread");
	}

	private static void syncDatatoDisk ()
	{

		try
		{
			File file = new File(filename);
			System.out.println("going to fsync");
			if(DataEngine.KEY_VS_OFFSET.isEmpty())
			{
				System.out.println("Map is empty");
				if(file.exists())
				{
					ObjectMapper mapper = new ObjectMapper();
					Map <String, Map <String, Object>> userData = mapper.readValue(
						file , new TypeReference <Map <String, Map <String, Object>>>()
						{
						});
					DataEngine.KEY_VS_OFFSET = userData;
				}
				else
				{
					System.out.println("file not present");
				}
			}
			else
			{
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.writeValue(file , DataEngine.KEY_VS_OFFSET);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	static Map loadFromDisk ()
	{
		Map data = new HashMap <>();

		try
		{
			File file = new File(filename);
			if(file.exists())
			{
				System.out.println("file present");
				ObjectMapper mapper = new ObjectMapper();
				Map <String, Map <String, Object>> userData = mapper.readValue(
					file , new TypeReference <Map <String, Map <String, Object>>>()
					{
					});
				DataEngine.KEY_VS_OFFSET = userData;
			}
			else
			{
				System.out.println("file not present");
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return data;
	}
}
