package cn.linkedcare.springboot.redis.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import cn.linkedcare.springboot.redis.Launch;
import cn.linkedcare.springboot.redis.template.RwSplitRedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;


@Configurable
public class RedisConfig {
	
	public static final Logger log = LoggerFactory.getLogger(RedisConfig.class);
	

	@Value("${redis.url}")
	private String redisUrl;
	@Value("${redis.db}")
	private int redisDb;
	@Value("${redis.password}")
	private String redisPassword;
	@Value("${redis.timeout}")
	private int redisTimeout;
	@Value("${redis.type}")
	private RedisType redisType;
	
	public static enum RedisType{
		sentinel,//哨兵连接池
		signle;//单连接池
	}
	
	private static final String REDIS_NAME="mymaster";
	
	private Pool<Jedis> createJedisPool(){
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(50);
		jedisPoolConfig.setMaxWaitMillis(2000);
		
		if(StringUtils.isEmpty(redisPassword)){
			redisPassword=null;
		}
		if(RedisType.sentinel==redisType){
			Set<String> sentinels = new HashSet<>(Arrays.asList(redisUrl.split(",")));
	        // 创建连接池
	        JedisSentinelPool pool = new JedisSentinelPool(REDIS_NAME,
	        		sentinels,jedisPoolConfig,redisTimeout,redisTimeout,
	        		redisPassword,redisDb);
			return pool;
		}else{
			String[] args = redisUrl.split(":");
			// 创建连接池
	        JedisPool pool = new JedisPool(jedisPoolConfig,args[0],Integer.parseInt(args[1]),
	        		redisTimeout,redisPassword,redisDb,REDIS_NAME);
			return pool;
		}
		
		
	}


	@Bean
	@ConditionalOnMissingBean(RwSplitRedisTemplate.class)
	private RwSplitRedisTemplate createRwSplitRedisTemplate(){
		RwSplitRedisTemplate rwSplitRedisTemplate = new RwSplitRedisTemplate(createJedisPool());
	
		log.info("=========================================");
		log.info("=============spring redis host==========="+redisUrl+"===="+redisPassword);
		
		return rwSplitRedisTemplate;
	}
	
	
	

}
