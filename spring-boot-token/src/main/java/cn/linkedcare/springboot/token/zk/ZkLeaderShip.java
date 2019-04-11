package cn.linkedcare.springboot.token.zk;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import cn.linkedcare.springboot.token.manage.ITokenManage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZkLeaderShip extends LeaderSelectorListenerAdapter implements Closeable {
    //选主类
	private final LeaderSelector leaderSelector;
	private List<ITokenManage> list;
	
    public ZkLeaderShip(List<ITokenManage> list,CuratorFramework client, String path) {
        this.list = list;
    	
    	this.leaderSelector = new LeaderSelector(client, path, this);
        this.leaderSelector.autoRequeue();
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
    	log.info("takeLeadership refreshTime:listsize:{}",list.size());
    	
    	while(true){
    		long now = System.currentTimeMillis()/1000;
    		
    		for(ITokenManage l:list){
    			try{
    				//提前15分钟刷新
    				long refreshTime = l.nextTimeOut()-ITokenManage.TIME_OUT;
    				log.info("takeLeadership refreshTime:1 {},{}",now,refreshTime);
    				
    				if(now>=refreshTime){
    				log.info("takeLeadership refreshTime:2 {}",l);
    					l.refreshToken();
    				}
        		}catch(Exception e){
        			e.printStackTrace();
        			log.error("exception:{}",e);
        		}
    		}
    		
    		//10秒刷新一次
    		try{
    			Thread.sleep(10000l);
    	    }catch(Exception e){
    			e.printStackTrace();
    			log.error("exception:{}",e);
    		}
    	}
    }

	
}
