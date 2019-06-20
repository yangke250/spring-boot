package cn.linkedcare.springboot.delay.queue;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import cn.linkedcare.springboot.delay.queue.producer.DelayQueueProducer;
import cn.linkedcare.springboot.redis.config.RedisConfig;
import cn.linkedcare.springboot.redis.template.RedisTemplate;


@Configurable
@ComponentScan
//@SpringBootApplication
public class Launch {
	
	public static void main(String[] args) throws InterruptedException{
		
		SpringApplication.run(Launch.class, args);
		System.out.println("launch...");
		Thread.sleep(500000l);
	}
	
	@Bean
	public DelayQueueProducer getDelayQueueProducer(@Qualifier("redisTemplate") RedisTemplate redisTemplate){
		return new DelayQueueProducer(redisTemplate);
	}
}
