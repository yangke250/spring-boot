package cn.linkedcare.springboot.token.manage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.redis.template.RedisTemplate;
import cn.linkedcare.springboot.token.constant.KqTokenConstant;
import cn.linkedcare.springboot.token.intercepter.RetryIntercepter;
import cn.linkedcare.springboot.token.utils.MD5Util;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import sun.security.provider.MD5;

/**
 * 口腔openApi的token
 * @author wl
 *
 */
@Slf4j
@Component
public class KqOpenApiTokenManage implements ITokenManage{

	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static volatile long nextTimeOut = 0;

	public static final String MEDIA_TYPE = "application/json;charset=utf-8";
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	private static String openUrl;
	private static RedisTemplate redisTemplate;
	
	private static final String OPEN_KEY = TOKEN_PRE+MD5Util.md5(KqOpenApiTokenManage.class.getName());
	
	public static String getOpenUrl() {
		return openUrl;
	}

	@Value("${kq.oldToken.openUrl}")
	public void setOpenUrl(String openUrl) {
		KqOpenApiTokenManage.openUrl = openUrl;
	}

	@Resource 
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		KqOpenApiTokenManage.redisTemplate = redisTemplate;
	}

	private static OkHttpClient client = new OkHttpClient.Builder()
			.connectTimeout(5, TimeUnit.SECONDS)
			.readTimeout(5, TimeUnit.SECONDS)
			.build();

	@Data
	public static class TokenReponse {
		private String token;
		private String tokenType;
		private String expiredTime;
	}

	@Data
	@Builder
	public static class TokenLogin{
		private String tenantId;
		private String ticket;
	}
	
	private long getNextExpiredTime(String date){
		Date d = null;
		try {
			d = sdf.parse(date);
			return d.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 返回超时的下次超时的秒
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public  void refreshToken() {
		//现在时间要除去1000
		long now = System.currentTimeMillis() / 1000;

		log.info("refreshToken open:{},{}",now,nextTimeOut);

		TokenLogin tokenLogin = new TokenLogin.TokenLoginBuilder()
		.tenantId(KqTokenConstant.getOldTokenTenantId())
		.ticket(KqTokenConstant.getOldTokenTicket()).build();
		
		final Request request = new Request.Builder()
				.url(KqTokenConstant.getOldTokenUrl() + "/api/v1/public/logon")
				.post(RequestBody.create(MediaType.get(MEDIA_TYPE),JSON.toJSONString(tokenLogin)))
				.build();

		Call call = client.newCall(request);

		try {
			lock.writeLock().lock();
			String body = call.execute().body().string();

			TokenReponse tokenRes = JSON.parseObject(body, TokenReponse.class);

			// 提前5分钟刷新token
			String token = tokenRes.getToken();
			
			int expiredTime = (int) (getNextExpiredTime(tokenRes.getExpiredTime())/1000);
			
			redisTemplate.setex(OPEN_KEY,expiredTime,token);
			
			//提前刷新
			nextTimeOut = now + expiredTime;
			
			log.info("refreshToken open:{}:{},{}",OPEN_KEY,token,nextTimeOut);

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
			String token = redisTemplate.get(OPEN_KEY);
			
			log.info("getToken open:{},{}",OPEN_KEY,token);
			return token;
		} finally {
			lock.readLock().unlock();
		}

	}

	@Override
	public long nextTimeOut() {
		return nextTimeOut;
	}
}
