package cn.linkedcare.springboot.delay.queue.consumer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import cn.linkedcare.springboot.delay.queue.annotation.DelayQueueListener;
import cn.linkedcare.springboot.delay.queue.annotation.EnableDelayConsumer;
import cn.linkedcare.springboot.delay.queue.dto.ConsumerMethodDto;
import cn.linkedcare.springboot.delay.queue.dto.DelayQueueRecordDto;

/**
 * 消费者相关初始化
 * @author wl
 *
 */
@Component
public class InitConsumerListener implements BeanPostProcessor, ApplicationListener<ApplicationEvent> {

	private Map<String,ConsumerMethodDto> map = new ConcurrentHashMap<String,ConsumerMethodDto>();

	@Resource
	private Environment environment;
	
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof ApplicationReadyEvent){
			
			new QueueRecordConsumer(map).init();
		}
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		EnableDelayConsumer enable = bean.getClass().getAnnotation(EnableDelayConsumer.class);
		if (enable == null) {
			return bean;
		}

		Method[] methods = bean.getClass().getMethods();
		for (Method m : methods) {
			
			DelayQueueListener listener = m.getAnnotation(DelayQueueListener.class);

			if (listener != null) {
				Class<?>[] classzz = m.getParameterTypes();
				if(classzz.length!=1||classzz[0]!=DelayQueueRecordDto.class){
					throw new IllegalArgumentException("parameter length must 1 and type is DelayQueueRecordDto");
				}
				ConsumerMethodDto dto = new ConsumerMethodDto();
				dto.setMethod(m);
				dto.setObject(bean);
				
				String[] topics = listener.topic();
				//如果走环境变量
				if(listener.topicYml()){
					for(int i=0;i<topics.length;i++){
						topics[i]= environment.getProperty(topics[i]);
					}
				}
				
				dto.setTopics(topics);
				
				
				for(String topic:listener.topic()){
					map.put(topic,dto);
				}
			}
		}

		
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
