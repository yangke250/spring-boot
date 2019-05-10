package cn.linkedcare.springboot.sr2f.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Properties;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Configuration
@PropertySource(
value = {
		"classpath:application-sr2f.properties",
		"classpath:application-sr2f-${spring.profiles.active}.properties"},
ignoreResourceNotFound = true, encoding = "UTF-8")
public class Sr2fConfig {
	
	public static final Logger log = LoggerFactory.getLogger(Sr2fConfig.class);
	
	private static String zkUrl;

	private static String path;
	
	private static boolean isServer;
	
	private static boolean isClient;
	
	private static int port;
	
	public static String getZkUrl() {
		return zkUrl;
	}
	public static String getPath() {
		return path;
	}
	public static boolean isServer() {
		return isServer;
	}
	public static boolean isClient() {
		return isClient;
	}
	public static int getPort() {
		return port;
	}

	
	
	@Value("${sr2f.zookeeper.url}")
	public void setZkUrl(String zkUrl) {
		Sr2fConfig.zkUrl = zkUrl;
	}
	@Value("${sr2f.path}")
	public void setPath(String path) {
		Sr2fConfig.path = path;
	}
	@Value("${sr2f.server}")
	public void setServer(boolean isServer) {
		Sr2fConfig.isServer = isServer;
	}
	@Value("${sr2f.client}")
	public void setClient(boolean isClient) {
		Sr2fConfig.isClient = isClient;
	}
	@Value("${sr2f.port}")
	public void setPort(int port) {
		Sr2fConfig.port = port;
	}

	

}
