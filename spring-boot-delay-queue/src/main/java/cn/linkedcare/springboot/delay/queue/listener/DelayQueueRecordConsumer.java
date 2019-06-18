package cn.linkedcare.springboot.delay.queue.listener;

import java.util.concurrent.ConcurrentHashMap;

import cn.linkedcare.springboot.delay.queue.dto.ConsumerDto;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;



public class DelayQueueRecordConsumer {
	
	private Map<String,ConsumerDto> map = new ConcurrentHashMap<String,ConsumerDto>();
	
	public DelayQueueRecordConsumer(List<ConsumerDto> consumerList){
		initConsumerMap(consumerList);
		
		
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
