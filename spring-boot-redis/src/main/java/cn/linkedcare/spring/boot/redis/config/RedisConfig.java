package cn.linkedcare.spring.boot.redis.config;

import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import cn.linkedcare.spring.boot.redis.template.RwSplitRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;
import cn.linkedcare.spring.boot.redis.Launch;


@Configurable
public class RedisConfig {
	
	@Resource(name="redisPropertyConfig")
	private RedisPropertyConfig propertyConfig;
	
	private String host;
	private String password;
	private int port;
	private int db;
	private int timeout;
	
	
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getDb() {
		return db;
	}

	public void setDb(int db) {
		this.db = db;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	private JedisPoolConfig createJedisPoolConfig(){
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(50);//最大连接数
		jedisPoolConfig.setMaxWaitMillis(2000);//最大获得连接的时间
		return jedisPoolConfig;
	}

	@Bean
	private RwSplitRedisTemplate createRwSplitRedisTemplate(){
		RwSplitRedisTemplate rwSplitRedisTemplate = new RwSplitRedisTemplate();
		rwSplitRedisTemplate.setJedisPoolConfig(createJedisPoolConfig());
		rwSplitRedisTemplate.setHost(propertyConfig.getHost());
		rwSplitRedisTemplate.setPort(propertyConfig.getPort());
		rwSplitRedisTemplate.setDb(propertyConfig.getDb());
		rwSplitRedisTemplate.setPassword(propertyConfig.getPassword());
		rwSplitRedisTemplate.setTimeout(propertyConfig.getTimeout());
		rwSplitRedisTemplate.initPool();
		return rwSplitRedisTemplate;
	}
	
	
	

}
