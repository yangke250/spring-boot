package cn.linkedcare.springboot.delay.queue.dto;

import java.util.concurrent.locks.Condition;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumerLeaderDto {
	private String topic;//主题
	private int partition;//分片
	private ConsumerMethodDto consumerMethodDto;
	private Condition condition;
}
