package cn.linkedcare.springboot.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import cn.linkedcare.springboot.portal.aop.HttpSessionInterceptor;


@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

	/**
	 * 注册 拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(new HttpSessionInterceptor());
	}

}