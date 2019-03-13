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

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.token.constant.KqTokenConstant;
import cn.linkedcare.springboot.token.intercepter.RetryIntercepter;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * token管理
 * 
 * @author wl
 *
 */
@Slf4j
public class KqOldTokenManage implements ITokenManage{

	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static volatile String token;
	private static volatile long nextTimeOut = 0;

	public static final String MEDIA_TYPE = "application/json;charset=utf-8";
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	

	
	private static OkHttpClient client = new OkHttpClient.Builder()
			.connectTimeout(2, TimeUnit.SECONDS)
			.readTimeout(2, TimeUnit.SECONDS)
			.addInterceptor(new RetryIntercepter(2))
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
		long now = System.currentTimeMillis() / 1000;

		if (now < nextTimeOut) {
			return;
		}

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
			token = tokenRes.getToken();
			nextTimeOut = now + getNextExpiredTime(tokenRes.getExpiredTime())/1000 - 300;
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
			return token;
		} finally {
			lock.readLock().unlock();
		}

	}
}
