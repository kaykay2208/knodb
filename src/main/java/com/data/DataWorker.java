package com.data;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class DataWorker
{
	private static String filename = "keyMap";

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
		final ScheduledFuture <?> beeperHandle =
			scheduler.scheduleAtFixedRate(beeper , 10 , 10 , SECONDS);
		scheduler.schedule(new Runnable()
		{
			public void run ()
			{
				beeperHandle.cancel(true);
			}
		} , 60 * 60 , SECONDS);
		System.out.println("initiated sync thread");
	}

	private static void syncDatatoDisk ()
	{
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename)))
		{
			oos.writeObject(DataEngine.KEY_VS_OFFSET);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	static Map loadFromDisk ()
	{
		Map data = new HashMap <>();
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename)))
		{
			data = (Map) ois.readObject();

		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return data;
	}
}
