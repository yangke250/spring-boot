package cn.linkedcare.springboot.delay.queue.consumer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import cn.linkedcare.springboot.delay.queue.config.DelayQueueConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueRecordConsumerPuller {

	public static final String PATH = "/easy-delay-queue/consumer";

	public static final String CONSUMER_PATH = PATH+"/consumer";

	public void init() {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(DelayQueueConfig.getZkUrl())
				.sessionTimeoutMs(60000).retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 10000)).build();

		PathChildrenCache cache = new PathChildrenCache(client, PATH, false);
		
		
		
		client.getConnectionStateListenable().addListener(new ConnectionStateListener() {

			@Override
			public void stateChanged(CuratorFramework client, ConnectionState newState) {
				switch (newState) {
					case CONNECTED: {
						String num = createConsumer(client,cache);
						
						break;
					}
					case RECONNECTED: {
						String num = createConsumer(client,cache);
						
						break;
					}
					case LOST: {
						cache.getListenable().clear();
						break;
					}
				}

			}
		});
		// 客户端注册监听，进行连接配置
		client.start();
	}

	public static void main(String[] args){
		
	}
	
	private synchronized void createConsumer(CuratorFramework client,String str) throws Exception{
		int p = DelayQueueConfig.getPartition();
		
		List<String> consumerList = client.getChildren().forPath(PATH);
		
		Collections.sort(s, c);
		
		
	}

	/**
	 * 增加子节点变化的监控
	 */
	private String createConsumer(CuratorFramework client,PathChildrenCache cache) {
		
		try {
			cache.start();
		} catch (Exception e) {
			log.error("exception:{}",e);
			throw new RuntimeException(e);
		}
		// 注册监听
		cache.getListenable().addListener(new PathChildrenCacheListener() {

			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				switch (event.getType()) {
				case CHILD_ADDED: {
					break;
				}
				case CHILD_REMOVED: {
					break;
				}
				}
			}
		});
		
		try {
			String result = client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(CONSUMER_PATH);
	
			return result;
		} catch (Exception e) {
			log.error("exception:{}",e);
			throw new RuntimeException(e);
		}
	}
}
