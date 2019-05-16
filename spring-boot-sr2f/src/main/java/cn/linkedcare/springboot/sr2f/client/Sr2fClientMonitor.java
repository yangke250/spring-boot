package cn.linkedcare.springboot.sr2f.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
public class Sr2fClientMonitor implements BeanPostProcessor,ApplicationListener<ApplicationEvent>{


	private static Map<String,List<ISr2fClient>> map = new HashMap<String,List<ISr2fClient>>();
	
	

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof ISr2fClient){
			ISr2fClient sc = (ISr2fClient)bean;
			List<ISr2fClient> scs = map.get(sc.path()); 
			if(scs==null){
				scs = new ArrayList<ISr2fClient>(); 
				map.put(sc.path(),scs);
			}
			
			scs.add(sc);
		}
		
		return bean;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof ApplicationStartedEvent){
			for(String key:map.keySet()){
				List<ISr2fClient> servers = map.get(key);
				
				ZkClient zc = new ZkClient(key,servers);
			}
		}
	} 
	
	
}
