package cn.linkedcare.springboot.delay.queue.consumer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.delay.queue.dto.ConsumerMethodDto;
import cn.linkedcare.springboot.delay.queue.dto.DelayQueueRecordDto;
import cn.linkedcare.springboot.redis.template.RedisTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerSubThread implements Callable<Integer>{

	private RedisTemplate redisTemplate;
	
	private CountDownLatch cdl;
	
	private String pre;
	
	private ConsumerMethodDto consumerMethodDto;
	
	private Condition condition; 
	
	private ReentrantLock lock;
	
	public ConsumerSubThread(ReentrantLock lock,Condition condition,RedisTemplate redisTemplate,CountDownLatch cdl,String pre,ConsumerMethodDto consumerMethodDto){
		this.lock = lock;
		this.redisTemplate = redisTemplate;
		this.cdl = cdl;
		this.pre = pre;
		this.consumerMethodDto=consumerMethodDto;
		this.condition = condition;
	}
	
	@Override
	public Integer call() throws Exception {
		int end = -1;
		
		try{
			Set<String> sets = this.redisTemplate.zrange(pre,0,50);
			long now = System.currentTimeMillis()/1000;
			
			for(String set:sets){
				DelayQueueRecordDto dto = JSON.parseObject(set,DelayQueueRecordDto.class);
				if(dto.getTimestamp()<=now){
					try{
						consumerMethodDto.getMethod().invoke(consumerMethodDto.getObject(),dto);
						
						end++;
					}catch(Exception e){
						e.printStackTrace();
						log.error("delay queue exception:",e);
						
						if(consumerMethodDto.isAutoCommit()){
							end++;	
						}
					}
				}
			}
			if(end>=0){
				log.info("delay queue zrem :{},{}",pre,end);
				this.redisTemplate.zremrangeByRank(pre,0,end);
			}
		}finally {
			cdl.countDown();
			try{
				lock.lock();
				condition.signalAll();
			}finally {
				lock.unlock();
			}
			
		}
		
		return end;
	}
	
}
