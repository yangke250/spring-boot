package cn.linkedcare.springboot.hbase;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import cn.linkedcare.springboot.hbase.api.HbaseTemplate;
import cn.linkedcare.springboot.hbase.api.RowMapper;




@SpringBootApplication
public class Launch {
	public static final Logger logger = LoggerFactory.getLogger(Launch.class);

	private static ConfigurableApplicationContext cac;
	
	public static ConfigurableApplicationContext getBeanFactory(){
		return cac;
	}
	
	/**
	 * 得到相关的bean
	 * @param classz
	 * @return
	 */
	public static <T> T  getBean(Class<T> classz){
		return cac.getBean(classz);
	}
	
	private static HbaseTemplate hbaseTemplate;
	
	@Resource
	public void setConnection(HbaseTemplate hbaseTemplate){
		Launch.hbaseTemplate = hbaseTemplate;
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		cac = SpringApplication.run(Launch.class, args);
		
		Scan scan = new Scan();
		scan.addColumn("address".getBytes(),"city".getBytes());
		scan.addColumn("address".getBytes(),"contry".getBytes());
	
		hbaseTemplate.find("member", scan,new RowMapper<String>(){

			@Override
			public String mapRow(Result result, int rowNum) throws Exception {
				
				byte[] bytes = result.getValue("address".getBytes(),"city".getBytes());
				System.out.println("========="+new String(bytes,"utf-8"));

				
				
				return null;
			}
			
		});
        List<Get> list = new ArrayList<Get>();
        
        List<String> rowKeyList = new ArrayList<String>();
        rowKeyList.add("1000");
        rowKeyList.add("2");
        rowKeyList.add("3");
        
        for(String rowKey:rowKeyList){
            Get get=new Get(Bytes.toBytes(rowKey));
            list.add(get);
        }
        
  
        
        
		logger.info("=====================================");
		logger.info("==========merchant biz launch========");
		logger.info("=====================================");
		while(true){
			Thread.sleep(Long.MAX_VALUE);
		}
	}

	public static ConfigurableApplicationContext getCac() {
		return cac;
	}
	
	
}

