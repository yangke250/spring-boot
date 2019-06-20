package cn.linkedcare.springboot.delay.queue.consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
import cn.linkedcare.springboot.delay.queue.dto.ConsumerDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueRecordConsumer {

	public static final String PATH = "/easy-delay-queue/consumer";

	public static final String NODE_NAME = "consumer";
	
	public static final String CONSUMER_PATH = PATH+"/"+NODE_NAME;

	private Map<String,ConsumerDto> map;
	
	public QueueRecordConsumer(Map<String,ConsumerDto> map){
		this.map = map;
	}
	
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
		System.out.println(8/1);
		System.out.println(2%8);

	}

	/**
	 * 计算需要拉取的队列
	 * @param partition 总分片数
	 * @param total     总服务数
	 * @param position	
	 * @return
	 */
	private List<Integer> getCreateConsumerNum(int partition,int total,int position){
		
		List<Integer> list = new ArrayList<Integer>();
		
		int num = partition/total;
		
		if(num<=1){
			list.add(e)
		}else{
			
		}
				
				
		return list;
	}
	
	/**
	 * 创建消费者
	 * @param client
	 * @param str
	 * @throws Exception
	 */
	private synchronized void createConsumer(CuratorFramework client,String str) throws Exception{
		int partition = DelayQueueConfig.getPartition();
		
		List<String> consumerList = client.getChildren().forPath(PATH);
		
		Collections.sort(consumerList,new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				long num1 = Long.parseLong(o1.replace(NODE_NAME,""));
				long num2 = Long.parseLong(o2.replace(NODE_NAME,""));
				
				if(num1>num2){
					return 1;
				}else{
					return -1;
				}
			}
			
		});
		
		int i =0;
		for(String consumer:consumerList){
			if(consumer.equals(str)){
				break;
			}
			i++;
		}
		
		
	}

	/**
	 * 增加子节点变化的监控
	 */
	private String createConsumer(CuratorFramework client,PathChildrenCache cache,String topic) {
		
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
