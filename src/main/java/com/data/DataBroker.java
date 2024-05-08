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
import java.util.Properties;
import java.util.Map;

class DataBroker
{
	private static final String TOPIC_NAME = "knodb";
	private static final String BOOTSTRAP_SERVERS = "localhost:9092";

	static void pushtoKafka (String key , Map data)
	{
		Properties props = new Properties();
		props.put("bootstrap.servers" , BOOTSTRAP_SERVERS);
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
						DataEngine.KEY_VS_OFFSET.put(key , new ArrayList <Integer>()
						{{
							add(metadata.partition());
							add((int) metadata.offset());
						}});
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

	static Map readFromKafka (Integer partition , Integer offset)
	{
		Properties props = new Properties();
		props.put("bootstrap.servers" , BOOTSTRAP_SERVERS);
		props.put("group.id" , "test-group"); // Consumer group ID
		props.put("enable.auto.commit" , "false");
		props.put("key.deserializer" , "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer" , "org.apache.kafka.common.serialization.StringDeserializer");

		try(KafkaConsumer <Object, Object> consumer = new KafkaConsumer <>(props))
		{
			TopicPartition topicPartition = new TopicPartition(TOPIC_NAME , partition);
			consumer.assign(Collections.singletonList(topicPartition));
			consumer.seek(topicPartition , offset);

			ConsumerRecords <Object, Object> records = consumer.poll(Duration.ofMillis(1000));
			Map firstValue = null;
			for(ConsumerRecord record : records)
			{
				firstValue = (Map) record.value();
				break;
			}
			return firstValue;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
