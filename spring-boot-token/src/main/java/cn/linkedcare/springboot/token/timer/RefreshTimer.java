package cn.linkedcare.springboot.token.timer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode.Mode;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.linkedcare.springboot.token.manage.ITokenManage;
import cn.linkedcare.springboot.token.zk.ZkLeaderShip;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order
public class RefreshTimer implements BeanPostProcessor, ApplicationListener<SpringApplicationEvent> {

	private static List<ITokenManage> list = new ArrayList<ITokenManage>();

	public static final String ZK_PATH = "/linkedcare/tokenTask/";

	@Value("${zookeeper.url}")
	private String zkUrl;
	
	@Override
	public void onApplicationEvent(SpringApplicationEvent event) {
		ZkLeaderShip zkLeaderShip = null;
		if (event instanceof ApplicationReadyEvent) {
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(10000, Integer.MAX_VALUE);
			CuratorFramework client = CuratorFrameworkFactory.newClient(zkUrl, retryPolicy);

			try {
				// 连接启动
				client.start();
				client.blockUntilConnected();
				log.info("token Task zk connect");

				zkLeaderShip = new ZkLeaderShip(list,client, ZK_PATH);
				zkLeaderShip.start();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		log.info("ZkRefreshTask end");
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ITokenManage) {
			log.info(bean.getClass().getName() + " init token....");

			list.add((ITokenManage) bean);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
