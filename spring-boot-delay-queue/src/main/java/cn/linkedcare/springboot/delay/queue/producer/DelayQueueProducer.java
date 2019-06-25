package cn.linkedcare.springboot.delay.queue.producer;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import cn.linkedcare.springboot.delay.queue.config.DelayQueueConfig;
import cn.linkedcare.springboot.delay.queue.dto.DelayQueueRecordDto;
import cn.linkedcare.springboot.delay.queue.enums.TimeUnit;
import cn.linkedcare.springboot.redis.template.RedisTemplate;

public class DelayQueueProducer implements IDelayQueueProducer{
	
	private RedisTemplate redisTemplate;
	
	private volatile AtomicLong increment = new AtomicLong(1);
	
	public static final String PRE="easy_delay_queue_";
	
	public DelayQueueProducer(RedisTemplate redisTemplate){
		this.redisTemplate = redisTemplate;
	}
	
	/**
	 * 得到相关redis前缀
	 * @param topic
	 * @param partition
	 * @return
	 */
	public static String getDelayQueuePre(String topic,int partition){
		return PRE+topic+partition;
	}

	/**
	 * 得到额外缓存的key
	 * @param topic
	 * @param key
	 * @return
	 */
	private static String getStoreKey(String topic,String key){
		return topic+"_"+key;
	}
	
	private void checkParams(int partition,String topic, String body, int time){
		if(partition<0
				||partition>DelayQueueConfig.getPartition()
				||StringUtils.isEmpty(topic)
				||StringUtils.isEmpty(body)
				||time<=0){
			throw new IllegalArgumentException(partition+":"+topic+":"+body+":"+time);
		}
	}
	
	
	@Override
	public DelayQueueRecordDto sendDelayMsg(String topic, String body, int time, TimeUnit timeUnit) {
		int partition= (int) (increment.getAndIncrement()/DelayQueueConfig.getPartition());
		
		DelayQueueRecordDto dto = sendDelayMsg(partition,topic,body,time,timeUnit);
		return dto;
	}
	
	@Override
	public DelayQueueRecordDto sendDelayMsg(String topic, String body, int time, TimeUnit timeUnit,String deleteKey) {
		DelayQueueRecordDto dto = sendDelayMsg(topic,body,time,timeUnit);
		
		this.redisTemplate.zadd(getStoreKey(topic,deleteKey),dto.getTimestamp(),JSON.toJSONString(dto));
		
		this.redisTemplate.expire(getStoreKey(topic,deleteKey),changeSeconds(time,timeUnit));
		
		return dto;
	}
	
	/**
	 * 创建延时队列dto
	 * @param partition
	 * @param topic
	 * @param body
	 * @param time
	 * @param timeUnit
	 * @return
	 */
	private DelayQueueRecordDto createDelayQueueRecordDto(int partition, String topic, String body,int time, TimeUnit timeUnit){
		DelayQueueRecordDto delayQueueRecordDto = new DelayQueueRecordDto();
		delayQueueRecordDto.setPartition(partition);
		
		long timestamp = System.currentTimeMillis()/1000+changeSeconds(time,timeUnit);
		delayQueueRecordDto.setTimestamp(timestamp);
		delayQueueRecordDto.setTopic(topic);
		delayQueueRecordDto.setUid(UUID.randomUUID().toString());
		delayQueueRecordDto.setBody(body);
		
		return delayQueueRecordDto;
	}
	
	/**
	 * 把延时时间转换成秒
	 * @param time
	 * @param timeUnit
	 * @return
	 */
	private  int changeSeconds(int time, TimeUnit timeUnit){
		switch (timeUnit) {
		case min:
			return time*60;
		case hour:
			return time*60*60;
		case day:
			return time*60*60*24;
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public DelayQueueRecordDto sendDelayMsg(int partition, String topic, String body,int time, TimeUnit timeUnit) {
		checkParams(partition,topic,body,time);
		
		DelayQueueRecordDto dto = createDelayQueueRecordDto(partition,topic,body,time,timeUnit);
		
		double score = Double.valueOf(String.valueOf(dto.getTimestamp()));
		
		this.redisTemplate.zadd(getDelayQueuePre(topic,dto.getPartition()),score,JSON.toJSONString(dto));
		
		return dto;
	}

	@Override
	public boolean deleteDelayMsg(DelayQueueRecordDto dto) {

		long  result = this.redisTemplate.zrem(getDelayQueuePre(dto.getTopic(),dto.getPartition()),JSON.toJSONString(dto));
		return result>0?true:false;
	}


	@Override
	public boolean deleteDelayMsg(String topic, String key){
		//超过500不做处理
		Set<String> strs = this.redisTemplate.zrange(getStoreKey(topic,key),0,500);
		//只有1个数据分片
	    this.redisTemplate.zrem(getDelayQueuePre(topic,1), strs.toArray(new String[strs.size()]));
	    
	    this.redisTemplate.del(getStoreKey(topic,key));
	    
	    return true;
	}

	

}
