package cn.linkedcare.spring.boot.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;



@Component(value="redisPropertyConfig")
@ConfigurationProperties(prefix = "redis",
locations = {"classpath:spring-boot-redis.properties",
			 "classpath:spring-boot-redis-dev.properties",
		     "file:/usr/local/property/spring-boot/spring-boot-redis.properties"},
ignoreNestedProperties=true)
public class RedisPropertyConfig {
	private  String host;
	private  String password;
	private  int port;
	private  int db;
	private  int timeout;
	
	public  String getHost() {
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
	
}
