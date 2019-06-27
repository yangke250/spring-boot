package cn.linkedcare.springboot.delay.queue.dto;

import org.springframework.beans.BeanUtils;

import lombok.Data;

@Data
public class DelayQueueRecordDto {
	private String id;
	private String topic;
	private String body;
	private int partition;
	private long timestamp;
	
	
	
	@Override
	public boolean equals(Object obj) {
		String objUid = ((DelayQueueRecordDto)obj).getId();
        
		return (this.id.equals(objUid));
    }
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}
	
}
