package cn.linkedcare.springboot.delay.queue.producer;

import cn.linkedcare.springboot.delay.queue.dto.DelayQueueRecordDto;
import cn.linkedcare.springboot.delay.queue.enums.TimeUnit;

/**
 * 延时队列发送接口
 * @author wl
 *
 */
public interface IDelayQueueProducer {
	
	/**
	 * 发送延时消息
	 * @param topic
	 * @param body
	 * @param time
	 * @param timeUnit
	 */
	public DelayQueueRecordDto sendDelayMsg(String topic,String body,int time,TimeUnit timeUnit);
	
	
	/**
	 * 发送延时消息
	 * @param topic
	 * @param body
	 * @param time
	 * @param timeUnit
	 */
	public DelayQueueRecordDto sendDelayMsg(String topic,String body,int time,TimeUnit timeUnit,String key);
	
	/**
	 * 发送延时消息
	 * @param topic
	 * @param body
	 * @param time
	 * @param timeUnit
	 */
	public DelayQueueRecordDto sendDelayMsg(int partition,String topic,String body,int time,TimeUnit timeUnit);
	
	/**
	 * 删除延时发送的队列消息
	 * @param delayQueueRecordDto
	 * @return
	 */
	public boolean deleteDelayMsg(DelayQueueRecordDto delayQueueRecordDto);
	
	/**
	 * 删除延时发送的队列消息
	 * @param topic
	 * @param key
	 * @return
	 */
	public boolean deleteDelayMsg(String topic,String key);
}
