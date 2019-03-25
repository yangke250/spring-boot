package cn.linkedcare.springboot.cachecenter.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.cachecenter.annotation.Cache;
import cn.linkedcare.springboot.cachecenter.annotation.CacheDelete;
import cn.linkedcare.springboot.cachecenter.annotation.CacheReload;
import cn.linkedcare.springboot.redis.template.RedisTemplate;
import lombok.Builder;
import lombok.Data;

/**
 * 缓存的Aop
 * @author wl
 *
 */
@Component
public class RemoteCacheAop implements BaseAop{
	public static Logger logger = LoggerFactory.getLogger(RemoteCacheAop.class);
	
	@Value("${app.name}")
	private static String appName;

	@Resource
	private RedisTemplate redisTemplate;
	
	public static final String ENCODEING ="utf-8";
	//默认为null的时候
	public static final String NULL="NULL$VALUE";
	
	@Data
	public static class CacheResult {
		private boolean result;
		private Object object;
	}
	
	/**
     * 得到缓存的key
     * @param methodName 取key的方法
     * @param target 被代理的类
     * @param method 相关的方法
     * @param args
     * @return
     * @throws NoSuchMethodException
	 * @throws InvocationTargetException 
     */
    public static  String getKey(String methodName,Object target,Method method,Object[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException{
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
    private Object doGetCache(Object target,Method method,Object[] args){
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
        	       	
        	String str = new String(result,ENCODEING);
    	
        	//防止缓存穿透
        	if(str.equals(NULL)){
        		
        		return Void.class;
        	}
        	
        	Class<?> returnType = method.getReturnType();
        	return JSON.parseObject(str,returnType);
        	
    	}catch(Exception e){
    		logger.error("exception:{}",e);
    		return null;
    	}
    }
	
	@Override
	public int order() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public CacheResult executeBefore(Object target, Method method, Object[] args) {
		boolean result = false;
		Object object = doGetCache(target,method,args);
		if(object==null) {
			result=true;
		}
		
		CacheResult cacheResult = new CacheResult();
		
		cacheResult.setObject(object);
		cacheResult.setResult(result);
		
		return cacheResult;
	}

	@Override
	public boolean executeAfter(Object target, Method method, Object[] args, Object result) {
		try{
    		Cache cache = method.getAnnotation(Cache.class);
        	if(cache!=null){
        		String key = getKey(cache.keyMethod(),target,method,args);
            	redisTemplate.setex(key.getBytes(ENCODEING),cache.timeout(),JSON.toJSONString(result).getBytes(ENCODEING));
            	return false;
        	}
        	
        	CacheReload cacheReload = method.getAnnotation(CacheReload.class);
        	if(cacheReload!=null){
        		String key = getKey(cacheReload.keyMethod(),target,method,args);
            	redisTemplate.setex(key.getBytes(ENCODEING),cache.timeout(),JSON.toJSONString(result).getBytes(ENCODEING));
            	return false;
        	}
        	
        	CacheDelete cacheDelete = method.getAnnotation(CacheDelete.class);
        	if(cacheDelete!=null){
        		String key = getKey(cacheDelete.keyMethod(),target,method,args);
            	redisTemplate.del(key);
            	return false;
        	}
    	}catch(Exception e){
    		logger.error("exception:{}",e);
    	}
		return false;
	}
	
}