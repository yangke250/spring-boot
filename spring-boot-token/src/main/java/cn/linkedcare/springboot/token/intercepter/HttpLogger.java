package cn.linkedcare.springboot.token.intercepter;

import java.io.IOException;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class HttpLogger implements Interceptor  { 
	@Override 
	public Response intercept(Chain chain) throws IOException {
		String uuid =UUID.randomUUID().toString();
		Request request = chain.request(); 
		log.info("HttpLogger request:{},{}",uuid,request.toString());
		Response response = chain.proceed(request);
		log.info("HttpLogger response:{},{}",uuid,response.toString());
		return response; 
	}

}

