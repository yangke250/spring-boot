package cn.linkedcare.springboot.delay.queue.producer;

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
	public void sendDelayMsg(String topic,String body,int time,TimeUnit timeUnit);
	
	
	
	/**
	 * 发送延时消息
	 * @param topic
	 * @param body
	 * @param time
	 * @param timeUnit
	 */
	public void sendDelayMsg(int partition,String topic,String body,int time,TimeUnit timeUnit);
	
}
