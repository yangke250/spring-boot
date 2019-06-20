package cn.linkedcare.springboot.delay.queue.dto;

import lombok.Data;

@Data
public class ConsumerLeaderDto {
	private String topic;//主题
	private int partition;//分片
}
