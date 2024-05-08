package com.data;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class DataEngine
{
	static Map<String , List<Integer>> KEY_VS_OFFSET;
	static {
		KEY_VS_OFFSET=new HashMap<>();
	}
	public static void addData (String key , Map data)
	{
		DataBroker.pushtoKafka(key,data);
	}

	public static Map getData (String key)
	{
		List<Integer> offset= KEY_VS_OFFSET.get(key);
		return DataBroker.readFromKafka(offset.get(0),offset.get(1));
	}
}
