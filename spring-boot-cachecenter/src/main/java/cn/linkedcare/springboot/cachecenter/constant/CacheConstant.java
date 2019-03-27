package cn.linkedcare.springboot.cachecenter.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(
value = {
		"classpath:application-cachecenter.properties",
		"classpath:application-cachecenter-${spring.profiles.active}.properties"},
ignoreResourceNotFound = true, encoding = "UTF-8")
@Component
public class CacheConstant {
	
	private static String zkUrl;

	public static String getZkUrl() {
		return zkUrl;
	}

	@Value("${zookeeper.url}")
	public void setZkUrl(String zkUrl) {
		CacheConstant.zkUrl = zkUrl;
	}
	
	
}
