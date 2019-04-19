package cn.linkedcare.springboot.token.manage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.sun.media.jfxmedia.logging.Logger;

import cn.linkedcare.springboot.redis.template.RedisTemplate;
import cn.linkedcare.springboot.token.constant.KqTokenConstant;
import cn.linkedcare.springboot.token.intercepter.RetryIntercepter;
import cn.linkedcare.springboot.token.utils.MD5Util;
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

	private static volatile String openUrl;
	
	private static volatile long nextTimeOut = 0;

	public static final String MEDIA_TYPE = "application/x-www-form-urlencoded;charset=utf-8";

	public static final String BEARER="bearer ";

	private static RedisTemplate redisTemplate;
	
	private static String KY_KEY=TOKEN_PRE+MD5Util.md5(KqKyzlTokenManage.class.getName());
	
	public static void main(String[] args){
		System.out.println(KY_KEY);
	}
	
	public static String getOpenUrl() {
		return openUrl;
	}

	@Value("${kq.token.openUrl}")
	public void setOpenUrl(String openUrl) {
		KqKyzlTokenManage.openUrl = openUrl;
	}

	@Resource 
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		KqKyzlTokenManage.redisTemplate = redisTemplate;
	}
	
	
	private static OkHttpClient client = new OkHttpClient.Builder()
			.connectTimeout(5, TimeUnit.SECONDS)
			.readTimeout(5, TimeUnit.SECONDS)
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
		//现在时间要除去1000
		long now = System.currentTimeMillis() / 1000;

		log.info("refreshToken:{},{}",now,nextTimeOut);

		

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
			
			int expireTime = (int) tokenRes.getExpires_in();//超时时间
			
			redisTemplate.setex(KY_KEY,expireTime,token);
			
			nextTimeOut = now + expireTime;
			log.info("refreshToken:{},{}",KY_KEY,token,nextTimeOut);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("exception:{}", e);
		} finally {
			lock.writeLock().unlock();
		}
	}


	@Override
	public long nextTimeOut() {
		return nextTimeOut;
	}
	

	public static String getToken() {
		try {
			lock.readLock().lock();
			
			String token = redisTemplate.get(KY_KEY);
			log.info("getToken kyzl:{},{}",KY_KEY,token);
			
			return token;
		} finally {
			lock.readLock().unlock();
			//补偿
		}

	}

	
}
