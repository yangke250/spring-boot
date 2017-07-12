package cn.linkedcare.spring.boot.redis.config;

import java.util.Properties;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import cn.linkedcare.spring.boot.redis.template.RwSplitRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;
import cn.linkedcare.spring.boot.redis.Launch;


@Configurable
public class RedisConfig {
	
	public static final Logger log = LoggerFactory.getLogger(RedisConfig.class);
	
	@Resource(name="redisPropertyConfig")
	private PropertyConfig propertyConfig;
	
	private JedisPoolConfig createJedisPoolConfig(){
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(50);//最大连接数
		jedisPoolConfig.setMaxWaitMillis(2000);//最大获得连接的时间
		return jedisPoolConfig;
	}

	@Bean
	@ConditionalOnMissingBean(RwSplitRedisTemplate.class)
	private RwSplitRedisTemplate createRwSplitRedisTemplate(){
		RwSplitRedisTemplate rwSplitRedisTemplate = new RwSplitRedisTemplate();
		rwSplitRedisTemplate.setJedisPoolConfig(createJedisPoolConfig());
		
		log.info("=========================================");
		log.info("=============spring boot redis===========");
		log.info("=============spring redis host==========="+propertyConfig.getHost());
		rwSplitRedisTemplate.setHost(propertyConfig.getHost());
		rwSplitRedisTemplate.setPort(propertyConfig.getPort());
		rwSplitRedisTemplate.setDb(propertyConfig.getDb());
		rwSplitRedisTemplate.setPassword(propertyConfig.getPassword());
		rwSplitRedisTemplate.setTimeout(propertyConfig.getTimeout());
		rwSplitRedisTemplate.initPool();
		return rwSplitRedisTemplate;
	}
	
	
	

}
