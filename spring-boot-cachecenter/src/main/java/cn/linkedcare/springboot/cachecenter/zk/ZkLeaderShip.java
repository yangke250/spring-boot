package cn.linkedcare.springboot.cachecenter.zk;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import cn.linkedcare.springboot.cachecenter.refresh.AbstractCacheRefresh;
import cn.linkedcare.springboot.cachecenter.refresh.task.CacheTimeWheel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZkLeaderShip extends LeaderSelectorListenerAdapter implements Closeable {
    //选主类
	private final LeaderSelector leaderSelector;
    private List<AbstractCacheRefresh> cacheRefreshs;
    
    private CacheTimeWheel cacheTimeWheel = new CacheTimeWheel();
    
    public ZkLeaderShip(CuratorFramework client, String path,List<AbstractCacheRefresh> list) {
        this.leaderSelector = new LeaderSelector(client, path, this);
        this.leaderSelector.autoRequeue();
        
        this.cacheRefreshs = list;
    }

    
    public void start() throws IOException {
        leaderSelector.start();
    }

    @Override
    public void close() throws IOException {
        leaderSelector.close();
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
    	for(AbstractCacheRefresh<?> cacheRefresh:cacheRefreshs) {
    		try {
    			//添加待刷新的任务
        		cacheTimeWheel.add(cacheRefresh,cacheRefresh.refreshTime());
        	}catch(Exception e) {
        		e.printStackTrace();
        		
        		log.error("exception:",e);
        	}
    	}
    	
    			cacheTimeWheel.start();
    }

	
}
