package cn.linkedcare.springboot.delay.queue.consumer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.linkedcare.springboot.delay.queue.dto.ConsumerLeaderDto;
import cn.linkedcare.springboot.delay.queue.dto.ConsumerMethodDto;
import cn.linkedcare.springboot.delay.queue.dto.DelayQueueRecordDto;
import cn.linkedcare.springboot.delay.queue.producer.DelayQueueProducer;
import cn.linkedcare.springboot.redis.template.RedisTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order
public class ConsumerMainThread {

	private final ExecutorService server =  Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	@Resource
	private RedisTemplate redisTemplate;
	
	@PostConstruct
	public void init(){
			new Thread(()->{
				while(true){
					try{
						CopyOnWriteArraySet<ConsumerLeaderDto> sets = ConsumerLeaderShip.getTopicset();
						
						CountDownLatch cdl = new CountDownLatch(sets.size());
						
						for(ConsumerLeaderDto c:sets){
							String pre = DelayQueueProducer.getDelayQueuePre(c.getTopic(),c.getPartition());
							
							ConsumerSubThread cst = 
									new ConsumerSubThread(c.getCondition(),redisTemplate,cdl,pre,c.getConsumerMethodDto());
							
							server.submit(cst);
						}
						cdl.await();
						
						Thread.sleep(100l);
					}catch(Exception e){
						e.fillInStackTrace();
						log.error("exception:{}",e);
					}
					
				}
			}).start();
	}
	


	
}
