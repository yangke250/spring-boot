package cn.linkedcare.springboot.token.intercepter;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class RetryIntercepter implements Interceptor {

    public int maxRetry;//最大重试次数
    private int retryNum = 0;//已请求次数
    
    public RetryIntercepter(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //第一次请求
        Response response = chain.proceed(request);
        //重试请求
        while (!response.isSuccessful() && retryNum < maxRetry) {
            retryNum++;
            
            log.info("retryNum=" + retryNum);
            response = chain.proceed(request);
        }
        return response;
    }
}