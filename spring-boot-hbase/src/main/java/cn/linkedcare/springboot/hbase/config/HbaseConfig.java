package cn.linkedcare.springboot.hbase.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class HbaseConfig {
	
	@Value("${hbase.rootDir}")
	private String rootDir;
	
	@Value("${hbase.zkServer}")
	private String zkServer;
	
	@Value("${hbase.port}")
	private String port;

	@Value("${hbase.minThreads}")
	private int minThreads;

	@Value("${hbase.maxThreads}")
	private int maxThreads;
	
	
	/**
	 * 得到hbase相关配置
	 * @return
	 */
	@Bean
	public Configuration getConfiguration() {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.rootdir", rootDir);
		conf.set("hbase.zookeeper.quorum", zkServer);
		conf.set("hbase.zookeeper.property.clientPort", port);

		return conf;
	}
	
	/**
	 * 相关线程池
	 * @return
	 */
	@Bean
	public ExecutorService getExecutorService() {
		ExecutorService es = new ThreadPoolExecutor(minThreads, maxThreads,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(1));
		
		return es;
	}
	
	
	
//	@Bean
//	public void hbaseTemplate() {
//		Configuration conf = HBaseConfiguration.create();
//		conf.set("hbase.rootdir", rootDir);
//		conf.set("hbase.zookeeper.quorum", zkServer);
//		conf.set("hbase.zookeeper.property.clientPort", port);
//
//		Connection connection = ConnectionFactory.createConnection(conf, pool);
//		
//	}

}
