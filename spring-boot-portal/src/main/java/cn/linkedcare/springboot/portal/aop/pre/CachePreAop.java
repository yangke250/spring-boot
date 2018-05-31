package cn.linkedcare.springboot.portal.aop.pre;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.portal.annotation.cache.Cache;
import cn.linkedcare.springboot.portal.aop.AbstarctPreAop;
import cn.linkedcare.springboot.redis.template.RedisTemplate;

@Component
public class CachePreAop extends AbstarctPreAop{

	@Value("${app.name}")
	private static String appName;

	public static final String ENCODEING = "UTF-8";

	
	@Resource
	private RedisTemplate redisTemplate;
	
	
	@Override
	public boolean doFilter(ProceedingJoinPoint pjp) {
		
		MethodSignature signature = (MethodSignature) pjp.getSignature();  
        Method method = signature.getMethod(); //获取被拦截的方法
        Object[] args =  pjp.getArgs();//参数
        Object target = pjp.getTarget();
        

        String cacheStr =  doGetCache(target,method,args);
        if(cacheStr!=null){
        	super.wirte(cacheStr);
        	return false;
        }
		return true;
	}

	@Override
	public int order() {
		return 10;
	}

	@Override
	public void finallyMethod(ProceedingJoinPoint pjp) {
		
	}

	
	/**
     * 得到缓存的key
     * @param methodName 取key的方法
     * @param target 被代理的类
     * @param method 相关的方法
     * @param args
     * @return
     * @throws NoSuchMethodException
     */
    public static  String getKey(String methodName,Object target,Method method,Object[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException{
    	Method keyMethod =  target.getClass().getMethod(methodName,method.getParameterTypes());
    	keyMethod.setAccessible(true);
    	String key = keyMethod.invoke(target,args).toString();
        return appName+key;
    }
    
    /**
     * 取缓存的值
     * @param target 代理的类
     * @param method 代理的方法
     * @param args   参数相关
     * @return
     */
    private String doGetCache(Object target,Method method,Object[] args){
    	try{
    		Cache cache = method.getAnnotation(Cache.class);
    		if(cache==null){
        		return null;
        	}
    		
    		String key = getKey(cache.keyMethod(),target,method,args);
        	byte[] result  = redisTemplate.get(key.getBytes(ENCODEING));
    		if(result==null){
    			return null;
    		}
        	       	
        	return new String(result,ENCODEING);
    	}catch(Exception e){
    		logger.error("exception:{}",e);
    		return null;
    	}
    }


}
