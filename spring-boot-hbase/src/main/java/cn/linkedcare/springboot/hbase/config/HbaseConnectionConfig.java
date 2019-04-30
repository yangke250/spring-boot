package cn.linkedcare.springboot.hbase.config;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import cn.linkedcare.springboot.hbase.api.HbaseTemplate;


@org.springframework.context.annotation.Configuration
@PropertySource(
value = {
		"classpath:application-hbase.properties",
		"classpath:application-hbase-${spring.profiles.active}.properties"},
ignoreResourceNotFound = true, encoding = "UTF-8")
public class HbaseConnectionConfig {
	
	@Value("${hbase.zkServer}")
	private String zkServer;
	

	
	
	/**
	 * 得到hbase相关配置
	 * @return
	 */
	
	private Configuration getConfiguration() {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", zkServer);
		return conf;
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	public HbaseTemplate hbaseTemplate() throws IOException {

		
		HbaseTemplate hbaseTemplate = new HbaseTemplate(getConfiguration());
		
		return hbaseTemplate;
	}

}
