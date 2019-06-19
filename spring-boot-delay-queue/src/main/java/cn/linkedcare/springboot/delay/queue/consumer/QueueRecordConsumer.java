package cn.linkedcare.springboot.delay.queue.consumer;

import java.util.concurrent.ConcurrentHashMap;

import cn.linkedcare.springboot.delay.queue.dto.ConsumerDto;

import java.util.Map;
import java.util.List;



public class QueueRecordConsumer {
	
	private Map<String,ConsumerDto> map = new ConcurrentHashMap<String,ConsumerDto>();
	
	public QueueRecordConsumer(List<ConsumerDto> consumerList){
		initConsumerMap(consumerList);
		
		new QueueRecordConsumerPuller().init();
		
	}
	
	/**
	 * 
	 * @param consumerList
	 */
	private void initConsumerMap(List<ConsumerDto> consumerList){
		for(ConsumerDto dto:consumerList){
			String[] topics = dto.getTopics();
			for(String topic:topics){
				map.put(topic, dto);
			}
		}
	}
	
	
}
