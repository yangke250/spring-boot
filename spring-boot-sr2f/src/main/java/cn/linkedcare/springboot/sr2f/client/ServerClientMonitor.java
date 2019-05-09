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
public class ServerClientMonitor implements BeanPostProcessor,ApplicationListener<ApplicationEvent>{


	private static Map<String,List<IServerClient>> map = new HashMap<String,List<IServerClient>>();
	
	

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof IServerClient){
			IServerClient sc = (IServerClient)bean;
			List<IServerClient> scs = map.get(sc.path()); 
			if(scs==null){
				scs = new ArrayList<IServerClient>(); 
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
				List<IServerClient> servers = map.get(key);
				
				ZkServerClient zc = new ZkServerClient(servers);
			}
		}
	} 
	
	
}
