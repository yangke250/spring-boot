package cn.linkedcare.springboot.delay.queue.consumer;

import java.io.IOException;
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
import cn.linkedcare.springboot.delay.queue.dto.ConsumerMethodDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueRecordConsumer {

	public static final String PATH = "/easy-delay-queue/consumer";

	private Map<String, ConsumerMethodDto> map;

	public QueueRecordConsumer(Map<String, ConsumerMethodDto> map) {
		this.map = map;
	}

	public void init() {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(DelayQueueConfig.getZkUrl())
				.sessionTimeoutMs(60000).retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 10000)).build();

		
		client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
			private List<ConsumerLeaderShip> list;
			
			@Override
			public void stateChanged(CuratorFramework client, ConnectionState newState) {
				synchronized (client) {
					switch (newState) {
						case CONNECTED: {
							list = createConsumerList(client);
							break;
						}
						case RECONNECTED: {
							list = createConsumerList(client);
							break;
						}
						case LOST: {
							for(ConsumerLeaderShip l:list){
								l.close();
							}
							list.clear();
							break;
						}
						default:
							break;
					}
				}
			}
		});
		// 客户端注册监听，进行连接配置
		client.start();
	}

	/**
	 * 生成消费者列表
	 * 
	 * @param client
	 * @param topic
	 * @return
	 */
	public List<ConsumerLeaderShip> createConsumerList(CuratorFramework client) {
		List<ConsumerLeaderShip> list = new ArrayList<ConsumerLeaderShip>();

		for (String topic : map.keySet()) {
			ConsumerMethodDto consumerMethodDto = map.get(topic);

			ConsumerLeaderShip leader = createConsumer(client, topic, consumerMethodDto, PATH + "/" + topic);
			list.add(leader);
		}

		return list;
	}

	/**
	 * 创建领导者
	 * 
	 * @param client
	 * @param topic
	 * @param consumerMethodDto
	 * @param path
	 * @return
	 */
	private ConsumerLeaderShip createConsumer(CuratorFramework client, String topic,
			ConsumerMethodDto consumerMethodDto, String path) {
		ConsumerLeaderShip consumerLeaderShip = new ConsumerLeaderShip(topic, consumerMethodDto, client, path);
		consumerLeaderShip.start();

		return consumerLeaderShip;
	}

	/**
	 * 计算需要拉取的队列
	 * 
	 * @param partition
	 *            总分片数
	 * @param total
	 *            总服务数
	 * @param position
	 * @return
	 */
	private List<Integer> getCreateConsumerNum(int partition, int total, int position) {

		List<Integer> list = new ArrayList<Integer>();

		int num = partition / total;

		if (num <= 1) {
		} else {

		}

		return list;
	}

	/**
	 * 创建消费者
	 * 
	 * @param client
	 * @param str
	 * @throws Exception
	 */
	private synchronized void createConsumer(CuratorFramework client, String str) throws Exception {
		int partition = DelayQueueConfig.getPartition();

		List<String> consumerList = client.getChildren().forPath(PATH);

//		Collections.sort(consumerList, new Comparator<String>() {
//			@Override
//			public int compare(String o1, String o2) {
//				long num1 = Long.parseLong(o1.replace(NODE_NAME, ""));
//				long num2 = Long.parseLong(o2.replace(NODE_NAME, ""));
//
//				if (num1 > num2) {
//					return 1;
//				} else {
//					return -1;
//				}
//			}
//
//		});

		int i = 0;
		for (String consumer : consumerList) {
			if (consumer.equals(str)) {
				break;
			}
			i++;
		}

	}

}
