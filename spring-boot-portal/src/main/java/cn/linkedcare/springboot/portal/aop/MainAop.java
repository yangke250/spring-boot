package cn.linkedcare.springboot.portal.aop;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.portal.annotation.cache.Cache;
import cn.linkedcare.springboot.portal.annotation.cache.CacheDelete;
import cn.linkedcare.springboot.portal.annotation.cache.CacheReload;

/**
 * aop主入口，只针对于网关的controller
 * 
 * @author wl
 *
 */
@Aspect
@Component
public class MainAop implements BeanPostProcessor {

	public static final Logger logger = LoggerFactory.getLogger(MainAop.class);

	@Value("${app.name}")
	private String appName;



	/**
	 * 触发微服务之前，做的时候拦截
	 */
	private List<AbstarctPreAop> preAopList = new ArrayList<AbstarctPreAop>();

	/**
	 * 触发微服务以后，做的时候拦截
	 */
	private List<AbstarctPostAop> postAopList = new ArrayList<AbstarctPostAop>();

	/**
	 * 定义拦截规则
	 * 
	 */
	@Pointcut("execution(* cn.linkedcare..controller..*.*(..))")
	public void methodPointcut() {
	}

	/**
	 * 拦截器具体实现
	 * 
	 * @param pjp
	 * @return JsonResult（被拦截方法的执行结果，或需要登录的错误提示。）
	 * @throws IOException
	 * @throws Throwable
	 */
	@Around("methodPointcut()") // 指定拦截器规则；也可以直接把“execution(*
								// com.xjj.........)”写进这里
	public Object Interceptor(ProceedingJoinPoint pjp) throws IOException {
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod(); // 获取被拦截的方法

		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
		// 只真正requestMapping做相关事情
		try {
			boolean preResult = doPre(pjp, method);
			if (!preResult) {
				return null;// 触发前置拦截
			}

			Object o = pjp.proceed();

			// 只真正requestMapping做相关事情
			if (requestMapping != null) {
				doPost(pjp, method, o);
			}
			return o;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("exception:{}", e);
			String str = "{\"msg\":\"" + e.fillInStackTrace() + "\",\"result\":500}";

			ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletResponse response = sra.getResponse();
			response.getWriter().write(str);
			response.getWriter().close();
			return null;
		} finally {
			dofinally(pjp, method);
		}

	}

	private void dofinally(ProceedingJoinPoint pjp, Method method) {
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

		if (requestMapping != null) {
			for (AbstarctPreAop aop : preAopList) {
				try {
					aop.finallyMethod(pjp);
				} catch (Exception e) {
					logger.error("Exception:{}", e);
				}
			}

			for (AbstarctPostAop aop : postAopList) {
				try {
					aop.finallyMethod(pjp);
				} catch (Exception e) {
					logger.error("Exception:{}", e);
				}
			}
		}
	}

	/**
	 * 处理前置
	 * 
	 * @param pjp
	 * @param method
	 * @return
	 */
	private boolean doPre(ProceedingJoinPoint pjp, Method method) {
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

		if (requestMapping != null) {
			for (AbstarctPreAop aop : preAopList) {
				boolean result = aop.doFilter(pjp);
				if (!result) {// 如果触发相关拦截，直接返回
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 处理后置
	 * 
	 * @param pjp
	 * @param method
	 * @return
	 */
	private void doPost(ProceedingJoinPoint pjp, Method method, Object result) {
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

		if (requestMapping != null) {
			for (AbstarctPostAop aop : postAopList) {
				try {
					aop.doFilter(pjp, result);
				} catch (Exception e) {
					logger.error("exception:{}", e);
				}
			}
		}

	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * 
	 */
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof AbstarctPreAop) {
			preAopList.add((AbstarctPreAop) bean);

			Collections.sort(preAopList);
		}

		if (bean instanceof AbstarctPostAop) {
			postAopList.add((AbstarctPostAop) bean);

			Collections.sort(postAopList);
		}
		return bean;
	}
}
