package cn.linkedcare.springboot.sr2f.server;

import java.util.concurrent.TimeUnit;

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

import cn.linkedcare.springboot.sr2f.config.Sr2fConfig;


public class ZkServerRegister  extends AbstractServerRegister{
	  
    public ZkServerRegister(String path, int port) {
		super(path, port);
	}


	private  CuratorFramework client = null;  
  
    
    
    
    @Override
	public void init(String path, String json) {
		// TODO Auto-generated method stub

    	client = CuratorFrameworkFactory.builder().connectString(Sr2fConfig.getZkUrl())  
                .sessionTimeoutMs(60000)  
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE,10000)).build();  
        client.getConnectionStateListenable().addListener((CuratorFramework client, ConnectionState newState)->{
        	
        	if(ConnectionState.CONNECTED==newState){
        		addNodeToZk(path,json);
        	}else if(ConnectionState.RECONNECTED==newState){
        		addNodeToZk(path,json);
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
    public void addNodeToZk(String path,String json){
    	PersistentNode node = new PersistentNode(client,
    			CreateMode.EPHEMERAL,
    			false,
    			path,
    			json.getBytes());
    	node.start();
    }


	
   

}
