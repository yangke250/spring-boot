package cn.linkedcare.springboot.redis.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import cn.linkedcare.springboot.redis.config.RedisConfig.RedisType;

@Configuration
@PropertySource(value = { "classpath:application.properties",
		"classpath:application-${spring.profiles.active}.properties" }, ignoreResourceNotFound = true, encoding = "UTF-8")
public class RedisConstant {

	private static String redisUrl;
	
	private static int redisDb;
	
	private static String redisPassword;
	
	private static int redisTimeout;
	
	private static RedisType redisType;
	
	
	public static String getRedisUrl() {
		return redisUrl;
	}
	@Value("${redis.url}")
	public void setRedisUrl(String redisUrl) {
		RedisConstant.redisUrl = redisUrl;
	}
	
	public static int getRedisDb() {
		return redisDb;
	}
	
	@Value("${redis.db}")
	public void setRedisDb(int redisDb) {
		RedisConstant.redisDb = redisDb;
	}
	
	public static String getRedisPassword() {
		return redisPassword;
	}
	
	@Value("${redis.password}")
	public void setRedisPassword(String redisPassword) {
		RedisConstant.redisPassword = redisPassword;
	}
	
	public static int getRedisTimeout() {
		return redisTimeout;
	}
	
	@Value("${redis.timeout}")
	public void setRedisTimeout(int redisTimeout) {
		RedisConstant.redisTimeout = redisTimeout;
	}
	
	
	public static RedisType getRedisType() {
		return redisType;
	}
	@Value("${redis.type}")
	public void setRedisType(RedisType redisType) {
		RedisConstant.redisType = redisType;
	}

	

}
