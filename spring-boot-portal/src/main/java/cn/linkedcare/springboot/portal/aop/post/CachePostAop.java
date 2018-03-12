package cn.linkedcare.springboot.portal.aop.post;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.portal.annotation.cache.Cache;
import cn.linkedcare.springboot.portal.annotation.cache.CacheDelete;
import cn.linkedcare.springboot.portal.annotation.cache.CacheReload;
import cn.linkedcare.springboot.portal.aop.AbstarctPostAop;
import cn.linkedcare.springboot.portal.aop.pre.CachePreAop;
import cn.linkedcare.springboot.redis.template.RedisTemplate;

/**
 * 触发结果以后需要，缓存
 * @author wl
 *
 */
@Component
public class CachePostAop extends AbstarctPostAop {

	public static final String ENCODEING = "UTF-8";

	
	@Resource
	private RedisTemplate redisTemplate;
	
	@Override
	public boolean doFilter(ProceedingJoinPoint pjp, Object result) {
		MethodSignature signature = (MethodSignature) pjp.getSignature();  
        Method method = signature.getMethod(); //获取被拦截的方法
        Object[] args =  pjp.getArgs();//参数
        Object target = pjp.getTarget();
		
		doSaveOrUpdateCache(target,method,args,result);
		return true;
	}

	@Override
	public int order() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	/**
     * 做更新或者修改cache
     * @param method
     */
    private void doSaveOrUpdateCache(Object target,Method method,Object[] args,Object result){
    	try{
    		Cache cache = method.getAnnotation(Cache.class);
        	if(cache!=null){
        		String key = CachePreAop.getKey(cache.keyMethod(),target,method,args);
            	redisTemplate.setex(key.getBytes(ENCODEING),cache.timeout(),JSON.toJSONString(result).getBytes(ENCODEING));
            	return;
        	}
        	
        	CacheReload cacheReload = method.getAnnotation(CacheReload.class);
        	if(cacheReload!=null){
        		String key = CachePreAop.getKey(cacheReload.keyMethod(),target,method,args);
            	redisTemplate.setex(key.getBytes(ENCODEING),cache.timeout(),JSON.toJSONString(result).getBytes(ENCODEING));
            	return;
        	}
        	
        	CacheDelete cacheDelete = method.getAnnotation(CacheDelete.class);
        	if(cacheDelete!=null){
        		String key = CachePreAop.getKey(cacheDelete.keyMethod(),target,method,args);
            	redisTemplate.del(key);
            	return;
        	}
    	}catch(Exception e){
    		logger.error("exception:{}",e);
    	}
    	
    }
	

	@Override
	public void finallyMethod(ProceedingJoinPoint pjp) {
		// TODO Auto-generated method stub
		
	}
	
}
