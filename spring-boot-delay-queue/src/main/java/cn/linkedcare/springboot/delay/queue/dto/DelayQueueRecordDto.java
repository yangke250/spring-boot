package cn.linkedcare.springboot.delay.queue.dto;

import org.springframework.beans.BeanUtils;

import lombok.Data;

@Data
public class DelayQueueRecordDto {
	private String uid;
	private String topic;
	private String body;
	private int partition;
	private long timestamp;
	
	
	
	@Override
	public boolean equals(Object obj) {
		String objUid = ((DelayQueueRecordDto)obj).getUid();
        
		return (this.uid.equals(objUid));
    }
	
	@Override
	public int hashCode(){
		return uid.hashCode();
	}
	
}
