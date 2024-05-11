package com.data;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class DataEngine
{
	static Map <String, Map <String ,Object>> KEY_VS_OFFSET;
static final String PARTITION="p";
	static final String OFFSET="o";
	static
	{
			KEY_VS_OFFSET = DataWorker.loadFromDisk();
	}

	public static void addData (String key , Map data)
	{
		DataBroker.pushtoKafka(key , data);
	}

	public static String getData (String key)
	{
		Map <String ,Object> offset = KEY_VS_OFFSET.get(key);
		if(offset == null)
		{
			return DataBroker.readFromKafka(null , null);
		}
		else
		{

			return DataBroker.readFromKafka((Integer) offset.get(PARTITION) ,  Long.valueOf(String.valueOf((Integer)offset.get(OFFSET))));
		}
	}
}
