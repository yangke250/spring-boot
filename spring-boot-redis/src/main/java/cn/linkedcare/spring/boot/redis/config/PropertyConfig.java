package cn.linkedcare.spring.boot.redis.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;




@Configurable
@Component(value="redisPropertyConfig")
@PropertySource(value = {
		"classpath:spring-boot-redis.properties",
		"classpath:spring-boot-redis-dev.properties",
		"file:/usr/local/property/spring-boot/spring-boot-redis.properties"
		 },
		 ignoreResourceNotFound=true)
public class PropertyConfig {
	
	@Resource
	private Environment env;
	private  String host;
	private  String password;
	private  int port;
	private  int db;
	private  int timeout;
	
	public  String getHost() {
		return env.getProperty("redis.host");
	}
	public String getPassword() {
		return env.getProperty("redis.password");
	}
	public int getPort() {
		return Integer.parseInt(env.getProperty("redis.port"));
	}
	public int getDb() {
		return Integer.parseInt(env.getProperty("redis.db"));
	}
	public int getTimeout() {
		return Integer.parseInt(env.getProperty("redis.timeout"));
	}
	
}
