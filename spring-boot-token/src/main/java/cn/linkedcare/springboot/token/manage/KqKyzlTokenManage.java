package cn.linkedcare.springboot.token.manage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.sun.media.jfxmedia.logging.Logger;

import cn.linkedcare.springboot.redis.template.RedisTemplate;
import cn.linkedcare.springboot.token.constant.KqTokenConstant;
import cn.linkedcare.springboot.token.intercepter.RetryIntercepter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 看牙助理token管理
 * 
 * @author wl
 *
 */
@Slf4j
@Component
public class KqKyzlTokenManage implements ITokenManage{

	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static volatile String token;
	private static volatile long nextTimeOut = 0;

	public static final String MEDIA_TYPE = "application/x-www-form-urlencoded;charset=utf-8";

	public static final String BEARER="bearer ";

	private static RedisTemplate redisTemplate;
	
	@Resource 
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		KqKyzlTokenManage.redisTemplate = redisTemplate;
	}
	
	
	private static OkHttpClient client = new OkHttpClient.Builder()
			.connectTimeout(2, TimeUnit.SECONDS)
			.readTimeout(2, TimeUnit.SECONDS)
			.addInterceptor(new RetryIntercepter(2))
			.build();

	@Data
	public static class TokenReponse {
		private String access_token;
		private long expires_in;
		private String token_type;
	}

	/**
	 * 返回超时的下次超时的秒
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public void refreshToken() {
		long now = System.currentTimeMillis() / 1000;

		if (now < nextTimeOut) {
			return;
		}

		String token = "Basic " + Base64.getEncoder().encodeToString(
				(KqTokenConstant.getTokenUsername() + ":" + KqTokenConstant.getTokenPassword()).getBytes());

		
		final Request request = new Request.Builder()
				.url(KqTokenConstant.getTokenUrl() + "/connect/token")
				.addHeader("Authorization", token)
				.post(RequestBody.create(MediaType.get(MEDIA_TYPE), "grant_type=client_credentials"))
				.build();

		Call call = client.newCall(request);

		try {
			lock.writeLock().lock();
			String body = call.execute().body().string();

			TokenReponse tokenRes = JSON.parseObject(body, TokenReponse.class);

			// 提前5分钟刷新token
			token = BEARER+tokenRes.getAccess_token();
			
			int expireTime = (int) tokenRes.getExpires_in();
			
			redisTemplate.setex(TOKEN_PRE+KqKyzlTokenManage.class.getName(),expireTime,token);
			
			log.info("refreshToken:{}",JSON.toJSONString(tokenRes));
			
			nextTimeOut = now + tokenRes.getExpires_in() - 300;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("exception:{}", e);
		} finally {
			lock.writeLock().unlock();
		}
	}

	public static String getToken() {
		try {
			lock.readLock().lock();
			String token = redisTemplate.get(TOKEN_PRE+KqKyzlTokenManage.class.getName());

			log.info("getToken KqTokenManage:{}",token);

			return token;
		} finally {
			lock.readLock().unlock();
		}

	}
}
