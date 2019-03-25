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
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.cachecenter.annotation.Cache;
import cn.linkedcare.springboot.cachecenter.annotation.CacheDelete;
import cn.linkedcare.springboot.cachecenter.annotation.CacheReload;
import cn.linkedcare.springboot.cachecenter.aop.RemoteCacheAop.CacheResult;
import cn.linkedcare.springboot.redis.template.RedisTemplate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


/**
 * 缓存aop
 * @author wl
 *
 */
@Aspect  
@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy=true)
@Component 
@Order(value=Ordered.LOWEST_PRECEDENCE)
//确保cache的aop最后一个纸箱
public class CacheInterceptor implements BeanPostProcessor{
	
	public static final Logger logger = LoggerFactory.getLogger(CacheInterceptor.class);
	
	public static final String ENCODEING = "UTF-8";
	
	@Value("${app.name}")
	private String appName;
	
	@Resource
	private RedisTemplate redisTemplate;
	
	private BeanFactory beanFactory;
	
	//执行方法的aop
	private List<BaseAop> cacheAopList = new ArrayList<BaseAop>();

	
	
	
	/** 
     * 定义拦截规则
     *  and @annotation(org.springframework.web.bind.annotation.RequestMapping)
     */  
    @Pointcut("execution(* cn.linkedcare..controller..*.*(..)) or cn.linkedcare..service..*.*(..)) or execution(* cn.linkedcare..dao..*.*(..))")  
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
        
        for(BaseAop cacheAop:cacheAopList) {
        	CacheResult cacheResult = cacheAop.executeBefore(target, method, args);
        	if(cacheResult.isResult()) {
        		Object object = cacheResult.getObject();
        		if(object==Void.class) {
        			return null;
        		}
        		return object;
        	}
        }
       
        Object o = pjp.proceed();
        
        for(BaseAop cacheAop:cacheAopList) {
        	boolean result  =  cacheAop.executeAfter(target, method, args,o);
        	if(result) {
        		break;
        	}
        }
        return o;
    }
    
    

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof BaseAop) {
			cacheAopList.add((BaseAop)bean);
			
			Collections.sort(cacheAopList);
		}
		
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return bean;
	}
}
