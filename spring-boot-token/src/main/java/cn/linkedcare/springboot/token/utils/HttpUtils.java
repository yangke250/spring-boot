package cn.linkedcare.springboot.token.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.token.intercepter.HttpLogger;
import cn.linkedcare.springboot.token.intercepter.RetryIntercepter;
import cn.linkedcare.springboot.token.manage.KqTokenManage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import sun.util.logging.resources.logging;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Slf4j
public class HttpUtils {
	private static OkHttpClient client = new OkHttpClient.Builder()
			.connectTimeout(2, TimeUnit.SECONDS)
			.readTimeout(2, TimeUnit.SECONDS)
			.addInterceptor(new RetryIntercepter(1)) //默认重试1次
			.addInterceptor(new HttpLogger())
			.build();
	
	/**
	 * 做post json相关操作
	 * @param url
	 * @param classzz
	 * @return
	 */
	public static  <T> List<T> doPostJSONArray(String url,Object object,Class<T> classzz){
		String body = getPostJSONBody(url,object,classzz);

		return (List<T>)JSON.parseArray(body, classzz);
	}
	
	/**
	 * 做post json相关操作
	 * @param url
	 * @param classzz
	 * @return
	 */
	public static  <T> T doPostJSON(String url,Object object,Class<T> classzz){
		String body = getPostJSONBody(url,object,classzz);
		
		return (T)JSON.parseObject(body, classzz);
	}
	
	/**
	 * 做get相关操作
	 * @param url
	 * @param classzz
	 * @return
	 */
	public static  <T> T doGet(String url,Map<String,String> map,Class<T> classzz){
		String body = getGetJSONBody(url,map,classzz);

		return (T)JSON.parseObject(body, classzz);
	}
	
	/**
	 * 做get相关操作
	 * @param url
	 * @param classzz
	 * @return
	 */
	public static  <T> List<T> doGetArray(String url,Map<String,String> map,Class<T> classzz){
		String body = getGetJSONBody(url,map,classzz);

		return (List<T>)JSON.parseArray(body, classzz);
	}
	
	
	/**
	 * 得到post json body
	 * @param url
	 * @param object
	 * @param classzz
	 * @return
	 */
	public static  String getGetJSONBody(String url,Map<String,String> map,Class<?> classzz){
		
		//组装参数
		if(map!=null){
			url+="?";
			for(String str:map.keySet()){
				url+=str+"="+map.get(str)+"&";
			}
			url=url.substring(0,url.length()-1);//去掉最后多余的字符
		}
		
		
		final Request request = new Request.Builder()
				.url(url)
				.addHeader("Authorization",KqTokenManage.getToken())
				.get()
				.build();

		Call call   = client.newCall(request);
		
		try {
			String body = call.execute().body().string();
			return body;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * 得到post json body
	 * @param url
	 * @param object
	 * @param classzz
	 * @return
	 */
	public static  String getPostJSONBody(String url,Object object,Class<?> classzz){
		String json = JSON.toJSONString(object);
		final Request request = new Request.Builder()
				.url(url)
				.addHeader("Authorization",KqTokenManage.getToken())
				.post(RequestBody.create(MediaType.get("application/json;charset=utf-8"),
						json))
				.build();

		Call call   = client.newCall(request);
		
		try {
			String body = call.execute().body().string();
			return body;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	
	
}
