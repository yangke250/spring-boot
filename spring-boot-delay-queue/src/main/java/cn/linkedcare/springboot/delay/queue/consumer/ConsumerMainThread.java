package cn.linkedcare.springboot.delay.queue.consumer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.springframework.stereotype.Component;

import cn.linkedcare.springboot.delay.queue.dto.ConsumerMethodDto;
import cn.linkedcare.springboot.delay.queue.dto.DelayQueueRecordDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConsumerMainThread implements Callable<HashSet<DelayQueueRecordDto>> {

	
	public ConsumerMainThread(Map<String,ConsumerMethodDto> map,List<DelayQueueRecordDto> records,CountDownLatch cdl){
		this.map = map;
		this.records = records;
		this.cdl = cdl;
	}
	


	public HashSet<DelayQueueRecordDto> call() throws Exception {
		HashSet<DelayQueueRecordDto> set = new HashSet<DelayQueueRecordDto>();
		
		try{
			for(DelayQueueRecordDto record:records){
				String topic = record.getTopic();
				
				ConsumerMethodDto consumer = map.get(topic);
				if(consumer==null){
					continue;
				}
				
				Object object = consumer.getObject();
				try{
					consumer.getMethod().invoke(object,record);
					
					set.add(record);
				}catch(Exception e){
					e.printStackTrace();
					log.error("exception:{}",e);
					
					if(consumer.isAutoCommit()){
						set.add(record);
					}
				}
				
				
			}
		}finally {
			cdl.countDown();
		}
		
		
		return set;
	}

}
