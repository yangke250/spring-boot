package cn.linkedcare.springboot.dubbo.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component("dubboPropertyConfig")
@PropertySource(value = {
	     "classpath:spring-boot-dubbo.properties",
		 "classpath:spring-boot-dubbo-dev.properties",
	     "file:/usr/local/property/springboot/spring-boot-dubbo.properties"},
		 ignoreResourceNotFound=true)
public class PropertyConfig {
	
	@Resource
	private Environment env;

	public String getProtocolName() {
		return env.getProperty("dubbo.protocolName");
	}

	public int getProtocolPort() {
		return Integer.parseInt(env.getProperty("dubbo.protocolPort"));
	}

	public String getAppName() {
		return env.getProperty("dubbo.appName");
	}

	public String getAddress() {
		return env.getProperty("dubbo.address");
	}

	
}
