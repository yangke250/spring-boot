package cn.linkedcare.springboot.sr2f.server;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.linkedcare.springboot.sr2f.config.Sr2fConfig;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;



@Component
@Slf4j
public class ZkServerRegister  extends AbstractServerRegister{
	
	private CuratorFramework client;
 
	
	@Override
	public void destory(){
		if(client!=null){
			client.close();
		}
	}
    
    
    @PostConstruct
	@Override
	public void init() {
    	boolean isServer = Sr2fConfig.isServer();
    	log.info("server status:{}",isServer);
    	if(!isServer){
    		return;
    	}
    	

    	String json = super.getJson();
    	
    	CuratorFramework client = CuratorFrameworkFactory.builder().connectString(Sr2fConfig.getZkUrl())  
                .sessionTimeoutMs(60000)  
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE,10000)).build();  
        client.getConnectionStateListenable().addListener((CuratorFramework cf, ConnectionState newState)->{
        	
        	if(ConnectionState.CONNECTED==newState){
        		addNodeToZk(cf,super.getPath(),json);
        	}else if(ConnectionState.RECONNECTED==newState){
        		addNodeToZk(cf,super.getPath(),json);
        	}
        });
        // 客户端注册监听，进行连接配置  
        client.start();  
        //阻塞到服务连接上
        try {
			client.blockUntilConnected(2,TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
      
  
    /**
     * 添加指定的节点
     * @param path
     */
    private void addNodeToZk(CuratorFramework client,String path,String json){
    	PersistentNode node = new PersistentNode(client,
    			CreateMode.EPHEMERAL,
    			false,
    			path+"/"+UUID.randomUUID().toString(),
    			json.getBytes());
    	node.start();
    }


	

}
