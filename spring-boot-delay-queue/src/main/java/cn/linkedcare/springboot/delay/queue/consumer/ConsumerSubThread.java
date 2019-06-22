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
public class ConsumerSubThread implements Callable<HashSet<String>>{

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
	public HashSet<String> call() throws Exception {
		HashSet<String> strs = new HashSet<String>();
		
		try{
			Set<String> sets = this.redisTemplate.zrange(pre,0,10);
			long now = System.currentTimeMillis()/1000;
			for(String set:sets){
				DelayQueueRecordDto dto = JSON.parseObject(set,DelayQueueRecordDto.class);
				if(dto.getTimestamp()<=now){
					try{
						consumerMethodDto.getMethod().invoke(consumerMethodDto.getObject(),dto);
						
						strs.add(set);
					}catch(Exception e){
						e.printStackTrace();
						log.error("exception:",e);
						
						if(consumerMethodDto.isAutoCommit()){
							strs.add(set);	
						}
					}
				}
			}
			if(strs.size()>0){
				this.redisTemplate.zrem(pre,strs.toArray(new String[strs.size()]));
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
		return strs;
	}
	
}
