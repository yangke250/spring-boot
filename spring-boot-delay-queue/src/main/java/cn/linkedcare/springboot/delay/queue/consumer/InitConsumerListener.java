package cn.linkedcare.springboot.delay.queue.consumer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import cn.linkedcare.springboot.delay.queue.annotation.DelayQueueListener;
import cn.linkedcare.springboot.delay.queue.annotation.EnableDelayConsumer;
import cn.linkedcare.springboot.delay.queue.dto.ConsumerDto;
import cn.linkedcare.springboot.delay.queue.dto.DelayQueueRecordDto;

/**
 * 消费者相关初始化
 * @author wl
 *
 */
@Component
public class InitConsumerListener implements BeanPostProcessor, ApplicationListener<ApplicationEvent> {

	private List<ConsumerDto> consumerList = new CopyOnWriteArrayList<ConsumerDto>();

	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof ApplicationReadyEvent){
			new QueueRecordConsumer(consumerList);
		}
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
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
				ConsumerDto dto = new ConsumerDto();
				dto.setMethod(m);
				dto.setObject(bean);
				dto.setTopics(listener.topic());
				
				consumerList.add(dto);
			}
		}

		return bean;
	}

}
