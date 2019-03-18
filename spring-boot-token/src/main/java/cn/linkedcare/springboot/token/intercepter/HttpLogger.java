package cn.linkedcare.springboot.token.intercepter;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class HttpLogger implements Interceptor  { 
	@Override 
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request(); 
		log.info("HttpLogger request:{}",request.toString());
		Response response = chain.proceed(request);
		String resString =null; 
		if(response.body()!=null){
			resString = response.body().string();
		}
		log.info("HttpLogger response:{},{}",response.toString(),resString);
		return response; 
	}

}

