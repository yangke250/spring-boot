package cn.linkedcare.springboot.delay.queue.listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;


import cn.linkedcare.springboot.delay.queue.annotation.DelayQueueListener;
import cn.linkedcare.springboot.delay.queue.annotation.EnableDelayConsumer;
import cn.linkedcare.springboot.delay.queue.dto.ConsumerDto;
import cn.linkedcare.springboot.delay.queue.dto.DelayQueueRecordDto;

/**
 * 监听相关的消费者
 * 
 * @author wl
 *
 */
public class InitConsumerListener implements BeanPostProcessor, ApplicationListener<ApplicationEvent> {

	private List<ConsumerDto> consumerList = new CopyOnWriteArrayList<ConsumerDto>();

	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof ApplicationPreparedEvent){
			new DelayQueueRecordConsumer(consumerList);
		}
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
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
