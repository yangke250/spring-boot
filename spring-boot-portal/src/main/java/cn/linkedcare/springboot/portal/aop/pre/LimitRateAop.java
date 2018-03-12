package cn.linkedcare.springboot.portal.aop.pre;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.linkedcare.springboot.portal.aop.AbstarctAop.AopType;
import cn.linkedcare.springboot.portal.annotation.limitrate.LimitRate;
import cn.linkedcare.springboot.portal.aop.AbstarctPreAop;
import cn.linkedcare.springboot.portal.dto.LimitRateDto;
import cn.linkedcare.springboot.redis.template.RedisTemplate;

@Component
public class LimitRateAop extends AbstarctPreAop implements ApplicationListener<ContextRefreshedEvent> {

	public static Logger logger = LoggerFactory.getLogger(LimitRateAop.class);

	private Map<String, LimitRate> limitRateMaps = new ConcurrentHashMap<String, LimitRate>();

	// 没有设置超时时间
	public static final long NO_EXPIRE = -1;
	
	@Resource
	private RedisTemplate redisTemplate;

	@Value("${app.name}")
	private String appName;

	@Override
	public boolean doFilter(ProceedingJoinPoint pjp) {
		HttpServletRequest httpServletRequest = getRequest();
		String uri = httpServletRequest.getRequestURI();
		// redisTemplate
		LimitRate limitRate = limitRateMaps.get(uri);
		// 需要对接口进行限流
		if (limitRate != null) {
			boolean result = isLimit(uri, limitRate.rate(), limitRate.timeout());
			if (result) {
				wirte("{\"result\":503}");
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否超过了访问限制
	 * 
	 * @param key
	 * @param rate
	 * @param limit
	 * @return true代表被流程限制
	 */
	private boolean isLimit(String key, long rate, int times) {
		try {
			key = appName + key;// key
			long num = redisTemplate.incr(key);
			if (num == 1) {// 当首次设置限流的时候，这里需要做补偿，防止数据不一致
				redisTemplate.expire(key, times);
			}
			if (num > rate) {
				// 说明存在数据不一致的行为，设置超时时间
				if (NO_EXPIRE == redisTemplate.ttl(key)) {
					redisTemplate.expire(key, times);
				}
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("exception：{}", e);
			return false;
		}

	}

	@Override
	public int order() {
		return 0;
	}

	@Override
	public void finallyMethod(ProceedingJoinPoint pjp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext ac = event.getApplicationContext();

		Map<String, Object> maps = ac.getBeansWithAnnotation(Controller.class);
		Collection<Object> controllers = maps.values();

		// 遍历每个controller
		for (Object o : controllers) {

			RequestMapping requestMapping = o.getClass().getAnnotation(RequestMapping.class);
			if (requestMapping == null) {
				continue;
			}
			// class对应的url mapping
			String[] classPres = requestMapping.value();
			// method对应的mapping
			List<LimitRateDto> methodPres = getLimitRateMethods(o);

			for (LimitRateDto m : methodPres) {
				String[] uris = m.getUris();
				LimitRate lr = m.getLimitRate();
				for (String uri : uris) {
					for (String c : classPres) {
						limitRateMaps.put(c + uri, lr);
					}
				}
			}
		}
		logger.info("limitRateMaps:{}", limitRateMaps);
	}

	/**
	 * 得到需要限流的方法
	 * 
	 * @param c
	 * @return
	 */
	private List<LimitRateDto> getLimitRateMethods(Object c) {
		Method[] methods = c.getClass().getMethods();

		List<LimitRateDto> list = new ArrayList<LimitRateDto>();
		for (Method m : methods) {
			LimitRate limitRate = m.getAnnotation(LimitRate.class);
			if (limitRate == null) {
				continue;
			}

			RequestMapping requestMapping = m.getAnnotation(RequestMapping.class);
			String[] paths = requestMapping.value();
			LimitRateDto dto = new LimitRateDto();
			dto.setLimitRate(limitRate);
			dto.setUris(paths);

			list.add(dto);
		}
		return list;
	}

	

}
