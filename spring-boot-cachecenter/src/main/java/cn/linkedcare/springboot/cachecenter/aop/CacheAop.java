package cn.linkedcare.springboot.cachecenter.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.cachecenter.annotation.Cache;
import cn.linkedcare.springboot.cachecenter.annotation.CacheDelete;
import cn.linkedcare.springboot.cachecenter.annotation.CacheReload;
import cn.linkedcare.springboot.redis.template.RedisTemplate;

/**
 * 缓存aop
 * @author wl
 *
 */
@Aspect  
@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy=true)
@Component  
public class CacheAop implements BeanFactoryAware{
	
	public static final Logger logger = LoggerFactory.getLogger(CacheAop.class);
	
	public static final String ENCODEING = "UTF-8";
	
	@Value("${app.name}")
	private String appName;
	
	@Resource
	private RedisTemplate redisTemplate;
	
	private BeanFactory beanFactory;
	
	/** 
     * 定义拦截规则
     *  and @annotation(org.springframework.web.bind.annotation.RequestMapping)
     */  
    @Pointcut("execution(* cn.linkedcare..service..*.*(..)) or execution(* cn.linkedcare..dao..*.*(..))")  
    public void methodPointcut(){} 
    
    /** 
     * 拦截器具体实现 
     * @param pjp 
     * @return JsonResult（被拦截方法的执行结果，或需要登录的错误提示。） 
     * @throws Throwable 
     */  
    @Around("methodPointcut()") //指定拦截器规则；也可以直接把“execution(* com.xjj.........)”写进这里  
    public Object Interceptor(ProceedingJoinPoint pjp) throws Throwable{  
        MethodSignature signature = (MethodSignature) pjp.getSignature();  
        Method method = signature.getMethod(); //获取被拦截的方法
        Object[] args =  pjp.getArgs();//参数
        Object target = pjp.getTarget();
        
        Object cacheObject  = doGetCache(target,method,args);
        if(cacheObject!=null){
        	return cacheObject;
        }
        Object o = pjp.proceed();
        
        doSaveOrUpdateCache(target,method,args,o);
        return o;
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
    private String getKey(String methodName,Object target,Method method,Object[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException{
    	Method keyMethod =  target.getClass().getMethod(methodName,method.getParameterTypes());
    	keyMethod.setAccessible(true);
    	String key = keyMethod.invoke(target,args).toString();
        return appName+key;
    }
    
    /**
     * 做更新或者修改cache
     * @param method
     */
    private void doSaveOrUpdateCache(Object target,Method method,Object[] args,Object result){
    	try{
    		Cache cache = method.getAnnotation(Cache.class);
        	if(cache!=null){
        		String key = getKey(cache.keyMethod(),target,method,args);
            	redisTemplate.setex(key.getBytes(ENCODEING),cache.timeout(),JSON.toJSONString(result).getBytes(ENCODEING));
            	return;
        	}
        	
        	CacheReload cacheReload = method.getAnnotation(CacheReload.class);
        	if(cacheReload!=null){
        		String key = getKey(cacheReload.keyMethod(),target,method,args);
            	redisTemplate.setex(key.getBytes(ENCODEING),cache.timeout(),JSON.toJSONString(result).getBytes(ENCODEING));
            	return;
        	}
        	
        	CacheDelete cacheDelete = method.getAnnotation(CacheDelete.class);
        	if(cacheDelete!=null){
        		String key = getKey(cacheDelete.keyMethod(),target,method,args);
            	redisTemplate.del(key);
            	return;
        	}
    	}catch(Exception e){
    		logger.error("exception:{}",e);
    	}
    	
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
        	
        	return JSON.parseObject(new String(result,ENCODEING),cache.cacheClass());
       }catch(Exception e){
    		logger.error("exception:{}",e);
    		return null;
    	}
    }

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
