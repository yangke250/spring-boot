package cn.linkedcare.springboot.portal.aop;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.LastModified;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.cachecenter.aop.BaseAop;
import cn.linkedcare.springboot.cachecenter.aop.RemoteCacheAop.CacheResult;
import cn.linkedcare.springboot.portal.annotation.cache.BrowserCache;


/**
 * aop主入口，只针对于网关的controller
 * 
 * @author wl
 *
 */
@Component
public class BrowserCacheAop implements BaseAop {

	@Override
	public int order() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * 得到http response
	 * @return
	 */
	public static HttpServletResponse getResponse(){
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletResponse response = sra.getResponse();
		return response;
	}

	/**
	 * 得到http response
	 * @return
	 */
	public static HttpServletRequest getRequest(){
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = sra.getRequest();
		return request;
	}
	
	@Override
	public CacheResult executeBefore(Object target, Method method, Object[] args) {
		HttpServletRequest request = getRequest();
		
		CacheResult cacheResult = new CacheResult();
		
		
		BrowserCache b = method.getAnnotation(BrowserCache.class);
		//是否设置了http缓存
		if(b==null) {
			cacheResult.setResult(false);
			return cacheResult;
		}
		
        long header = request.getDateHeader("If-Modified-Since");
		long now    = System.currentTimeMillis();
		
		//走协商缓存
		if(now>(header+b.timeOut()*1000)) {
			HttpServletResponse response = getResponse();
			response.setStatus(304);
		
			cacheResult.setResult(true);
			cacheResult.setObject(Void.class);
		}
		
		return cacheResult;
	}

	@Override
	public boolean executeAfter(Object target, Method method, Object[] args, Object result) {
		
		BrowserCache b = method.getAnnotation(BrowserCache.class);
		//是否设置了http缓存
		if(b==null) {
			return false;
		}
		//浏览器缓存，后面的缓存不需要做
		HttpServletResponse response = getResponse();
		CacheControl cc = CacheControl.maxAge(b.timeOut(),TimeUnit.SECONDS);
		//支持http协议 1.0
		response.addDateHeader("Expires", System.currentTimeMillis() + b.timeOut()*1000);
		//支持http协议 1.1
		response.setHeader("Cache-Control", cc.getHeaderValue());
		
		response.addDateHeader("Last-Modified", System.currentTimeMillis());
        
        return true;
	}
	
	
}
