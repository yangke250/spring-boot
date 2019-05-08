package cn.linkedcare.springboot.sr2f.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


@Component
public class ServerClientMonitor implements BeanPostProcessor{

	private static List<IServerClient> serverList = new ArrayList<IServerClient>();

	public static List<IServerClient> getServerList() {
		return serverList;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof IServerClient){
			serverList.add((IServerClient)bean);
		}
		
		return bean;
	}
	
	
}
