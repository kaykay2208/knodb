package com.data;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map;

class DataBroker
{
	private static final String TOPIC_NAME = "knodb";
	private static final String ZOOKEEPER_CONNECT = "localhost:9092";


	static void pushtoKafka (String key , Map data)
	{
		Properties props = new Properties();
		props.put("bootstrap.servers" , ZOOKEEPER_CONNECT);
		props.put("key.serializer" , "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer" , "org.apache.kafka.common.serialization.StringSerializer");

		Producer <String, String> producer = new KafkaProducer <>(props);

		try
		{
			producer.send(new ProducerRecord <>(TOPIC_NAME , key , data.toString()) , new Callback()
			{
				@Override
				public void onCompletion (RecordMetadata metadata , Exception exception)
				{
					if(exception != null)
					{
						System.err.println("Error sending message to Kafka: " + exception.getMessage());
					}
					else
					{
						System.out.println("Message sent successfully to topic " + metadata.topic()
							+ " partition " + metadata.partition() + " offset " + metadata.offset());
						HashMap map = new HashMap<>();
						map.put(DataEngine.PARTITION,metadata.partition());

						map.put(DataEngine.OFFSET,metadata.offset());

						DataEngine.KEY_VS_OFFSET.put(key , map);
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			producer.flush();
			producer.close();
		}
	}

	static String readFromKafka (Integer partition , Long offset)
	{
		Properties props = new Properties();
		props.put("bootstrap.servers" , ZOOKEEPER_CONNECT);
		props.put("group.id" , "test-group"); // Consumer group ID
		props.put("enable.auto.commit" , "false");
		props.put("key.deserializer" , "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer" , "org.apache.kafka.common.serialization.StringDeserializer");

		try(KafkaConsumer <Object, Object> consumer = new KafkaConsumer <>(props))
		{
			TopicPartition topicPartition;
			if(partition==null||offset==null){
				 topicPartition = new TopicPartition(TOPIC_NAME , partition);
				consumer.assign(Collections.singletonList(topicPartition));
				consumer.seekToBeginning(Collections.singletonList(topicPartition));
				ConsumerRecords <Object, Object> records = consumer.poll(Duration.ofMillis(1000));
				Iterator itr =records.iterator();

			}
			else
			{
				topicPartition = new TopicPartition(TOPIC_NAME , partition);
				consumer.assign(Collections.singletonList(topicPartition));
				consumer.seek(topicPartition , offset);

				ConsumerRecords <Object, Object> records = consumer.poll(Duration.ofMillis(1000));
				System.out.println("record data" + records.toString());
				String firstValue = null;
				for(ConsumerRecord record : records)
				{
					firstValue = (String) record.value();
					System.out.println("record data" + record.toString());
					break;
				}
				return firstValue;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
