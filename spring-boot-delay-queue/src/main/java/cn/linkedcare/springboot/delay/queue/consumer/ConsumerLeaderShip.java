package cn.linkedcare.springboot.delay.queue.consumer;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerLeaderShip extends LeaderSelectorListenerAdapter implements Closeable {
    //选主类
	private final LeaderSelector leaderSelector;
	private final static CopyOnWriteArraySet<String> topicSet = new CopyOnWriteArraySet<String>();
	private String topic;
	
    public ConsumerLeaderShip(String topic,CuratorFramework client, String path) {
        this.topic = topic;
    	
    	this.leaderSelector = new LeaderSelector(client, path, this);
        this.leaderSelector.autoRequeue();
    }
    
    
    public static CopyOnWriteArraySet<String> getTopicset() {
		return topicSet;
	}


	public void start() throws IOException {
        leaderSelector.start();
    }

    @Override
    public void close() throws IOException {
        leaderSelector.close();
        topicSet.remove(topic);
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
    	topicSet.add(topic);
    }

	
}
