package cn.linkedcare.springboot.token.timer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.Map;


import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

import cn.linkedcare.springboot.token.manage.ITokenManage;
import cn.linkedcare.springboot.token.manage.KqTokenManage;

/**
 * 对于服务重启登，会话结束没有通知的补偿
 * @author wl
 *
 */
@Component
public class ConversationTimer implements SimpleJob,BeanPostProcessor{
	
	public static final Logger logger = LoggerFactory.getLogger(ConversationTimer.class);
	
	private Map<ITokenManage,ITokenManage> map = new HashMap<ITokenManage,ITokenManage>();
	//分片总数
	public static final int SHARDING_TOTAL = 1;
	
	@Value("${zookeeper.url}")
	private static String zkUrl;
	
	private Executor executor = null;


	@PostConstruct
	public void init() {
		executor = Executors.newFixedThreadPool(map.size());
		
		//先刷新的token
		refreshToken();
		//再定时刷新token
        new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();
    }
    
	
	private void refreshToken(){
		CountDownLatch cdl = new CountDownLatch(map.size());

		for(ITokenManage tokenManage:map.values()){
			
			executor.execute(new TokenThread(tokenManage,cdl));
		}
		logger.info("start refreshToken.....{}",System.currentTimeMillis());
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		logger.info("end refreshToken.....{}",System.currentTimeMillis());
	}
	
    private  CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(
        		new ZookeeperConfiguration(zkUrl,"token"));
        regCenter.init();
        return regCenter;
    }

    
    private  LiteJobConfiguration createJobConfiguration() {
        // 创建作业配置
    	JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration.
    			newBuilder(this.getClass().getName(),"0 0/1 * * * ?",SHARDING_TOTAL)
    			.build();
	    // 定义SIMPLE类型配置
	    SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig,ConversationTimer.class.getCanonicalName());
	    // 定义Lite作业根配置
	    LiteJobConfiguration simpleJobRootConfig = LiteJobConfiguration.newBuilder(simpleJobConfig).build();
	    return simpleJobRootConfig;
    }

	@Override
	public void execute(ShardingContext context) {
		logger.info("token begin ...:{},{},{}");
		
		refreshToken();
	}
	
	public static final class TokenThread implements Runnable{

		private ITokenManage tokenManage;
		private CountDownLatch cdl;
		
		
		public TokenThread(ITokenManage tokenManage,CountDownLatch cdl){
			this.tokenManage=tokenManage;
			this.cdl=cdl;
		}
		
		@Override
		public void run() {
			try{
				tokenManage.refreshToken();
			}finally {
				cdl.countDown();
			}
		}
		
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof ITokenManage){
			map.put((ITokenManage)bean,(ITokenManage)bean);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return null;
	}
}
