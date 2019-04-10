package cn.linkedcare.springboot.token.timer;

import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Map;


import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.omg.CORBA.Current;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
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
import cn.linkedcare.springboot.token.manage.KqKyzlTokenManage;

/**
 * 对于服务重启登，会话结束没有通知的补偿
 * @author wl
 *
 */
@Component
@Order(value=Integer.MAX_VALUE)
public class TokenTimer implements SimpleJob,BeanPostProcessor,ApplicationListener<SpringApplicationEvent>{
	
	public static final Logger logger = LoggerFactory.getLogger(TokenTimer.class);
	
	//分片总数
	public static final int SHARDING_TOTAL = 1;
	
	@Value("${zookeeper.url}")
	private String zkUrl;

	private Map<ITokenManage,ITokenManage> map = new HashMap<ITokenManage,ITokenManage>();

	private Executor executor = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors()*2);

	private void refreshToken(){
	
		CountDownLatch cdl = new CountDownLatch(map.size());

		logger.info("start 1 refreshToken.....{}",map.size());

		for(ITokenManage tokenManage:map.values()){
			new Thread(new TokenThread(tokenManage,cdl)).start();
		}
		
		long now  = System.currentTimeMillis();
		logger.info("start 2 refreshToken.....{}",now);
		try {//最多阻塞10分钟
			cdl.await(10,TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		logger.info("end 3 refreshToken.....{}",System.currentTimeMillis()-now);
	}
	
    private  CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(
        		new ZookeeperConfiguration(zkUrl,"refreshToken"));
        regCenter.init();
        return regCenter;
    }

    
    private  LiteJobConfiguration createJobConfiguration() {
        // 创建作业配置
    	JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration.
    			newBuilder(this.getClass().getName(),"0 0/5 * * * ?",SHARDING_TOTAL)
    			.build();
	    // 定义SIMPLE类型配置
	    SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig,TokenTimer.class.getCanonicalName());
	    // 定义Lite作业根配置
	    LiteJobConfiguration simpleJobRootConfig = LiteJobConfiguration.newBuilder(simpleJobConfig).build();
	    return simpleJobRootConfig;
    }

	@Override
	public void execute(ShardingContext context) {
		logger.info("token begin ...:{},{}",context.getJobName(),context.getShardingItem());
		refreshToken();
		logger.info("token end   ...:{},{}",context.getJobName(),context.getShardingItem());
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
				logger.info("thread refreshToken.....{}",tokenManage.getClass().getName());

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
		return bean;
	}


	@Override
	public void onApplicationEvent(SpringApplicationEvent event) {
		
		if(event instanceof ApplicationReadyEvent){
			logger.info("onApplicationEvent:========================,{}",map.size());
			
			//先刷新的token
			refreshToken();
			//再定时刷新token
	        new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();
		}
	}


	
}
