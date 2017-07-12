package cn.linkedcare.springboot.dubbo.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.schema.DubboNamespaceHandler;

@Configurable
public class DubboConfig {
	
	@Resource(name="dubboPropertyConfig")
	private PropertyConfig propertyConfig;

	@Bean
	@ConditionalOnMissingBean(ApplicationConfig.class)
	public ApplicationConfig getApplicationConfig(){
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(propertyConfig.getAppName());
		return applicationConfig;
	}
	
    @Bean
    @ConditionalOnMissingBean(RegistryConfig.class)
	public RegistryConfig getRegistryConfig(){
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setProtocol("zookeeper");
		registryConfig.setAddress(propertyConfig.getAddress());
		return registryConfig;
	}

	@Bean
	@ConditionalOnMissingBean(ConsumerConfig.class)
	public ConsumerConfig consumer() {
		ConsumerConfig cc = new ConsumerConfig();
		cc.setCheck(false);
		return cc;
	}
    
	@Bean
	@ConditionalOnMissingBean(ProtocolConfig.class)
	public ProtocolConfig getProtocolConfig(){
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setName(propertyConfig.getProtocolName());
		protocolConfig.setPort(propertyConfig.getProtocolPort());
		
		return protocolConfig;
	}
}
