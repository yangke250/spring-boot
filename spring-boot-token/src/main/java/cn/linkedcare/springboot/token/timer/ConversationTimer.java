package cn.linkedcare.springboot.token.timer;

import java.util.Calendar;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

import cn.linkedcare.springboot.token.manage.TokenManage;

/**
 * 对于服务重启登，会话结束没有通知的补偿
 * @author wl
 *
 */
@Component
public class ConversationTimer implements SimpleJob{
	
	public static final Logger logger = LoggerFactory.getLogger(ConversationTimer.class);
	
	//分片总数
	public static final int SHARDING_TOTAL = 1;
	
	@Value("${token.zookeeper.url}")
	private String zkUrl;
	
	@Value("${token.url}")
	private String url;
	
	@Value("${token.username}")
	private String username;
	
	@Value("${token.password}")
	private String password;
	
	
	@PostConstruct
	public void init() {
        new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();
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
    			newBuilder(this.getClass().getName(),"0 0/1 * * * ?",SHARDING_TOTAL).build();
	    // 定义SIMPLE类型配置
	    SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(simpleCoreConfig,ConversationTimer.class.getCanonicalName());
	    // 定义Lite作业根配置
	    LiteJobConfiguration simpleJobRootConfig = LiteJobConfiguration.newBuilder(simpleJobConfig).build();
	    return simpleJobRootConfig;
    }

	@Override
	public void execute(ShardingContext arg0) {
		logger.info("token begin ...:{},{},{}",url, username, password);
		//当前时间毫秒
		TokenManage.refreshToken(url, username, password);
	}
}
