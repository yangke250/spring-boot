package cn.linkedcare.springboot.cachecenter.refresh.task;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import cn.linkedcare.springboot.cachecenter.refresh.AbstractCacheRefresh;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CacheQueueHandler {
	private static final LinkedBlockingQueue<AbstractCacheRefresh> queue = new LinkedBlockingQueue<AbstractCacheRefresh>();
	
	
	public  static void add(AbstractCacheRefresh abstractCacheRefresh) {
		queue.add(abstractCacheRefresh);
	}
	
	@PostConstruct
	public void init() {
		new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						AbstractCacheRefresh cacheResh = queue.poll(60,TimeUnit.MINUTES);
						if(cacheResh!=null) {
							log.info("cacheResh:{}",cacheResh);
							cacheResh.abstractCache();
						}
					}catch(Exception e) {
						e.printStackTrace();
						log.error("exception:",e);
					}
				}
			}
		}.start();
	}

	
}
