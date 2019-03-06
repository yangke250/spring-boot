package cn.linkedcare.springboot.hystrix.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.linkedcare.springboot.hystrix.command.SemaphoreHystrixCommond;
import cn.linkedcare.springboot.hystrix.dto.HystrixDto;
import rx.Observer;




/**
 * 熔断相关filter
 * @author wl
 *
 */
@Component
@WebFilter(urlPatterns = "/*",filterName = "hystrixFilter")
@Order(2)
public class HystrixFilter  implements Filter,ApplicationListener<ContextRefreshedEvent>{

	public static final Logger logger = LoggerFactory.getLogger(HystrixFilter.class);
	
	@Value("${application.name}")
	private String appName;
	   
	
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HystrixDto dto = new HystrixDto();
		dto.setChain(chain);
		dto.setRequest(request);
		dto.setResponse(response);
		dto.setAppName(appName);
		
		SemaphoreHystrixCommond commond  = new SemaphoreHystrixCommond(dto);
		String string = commond.execute();
		if(string!=null){
			response.getWriter().write(string);
			response.getWriter().close();
		}
	}

	public void destroy() {
		
	}
   

	

}
