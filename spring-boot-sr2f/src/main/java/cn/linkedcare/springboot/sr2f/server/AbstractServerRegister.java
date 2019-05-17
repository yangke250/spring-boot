package cn.linkedcare.springboot.sr2f.server;

import java.util.UUID;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.sr2f.config.Sr2fConfig;
import cn.linkedcare.springboot.sr2f.dto.ServerDto;
import cn.linkedcare.springboot.sr2f.utils.IPUtils;
import lombok.Data;

@Data
public abstract class AbstractServerRegister {

	
	public static final String SPLIT=":";
	
	/**
	 * 初始化
	 * @param path
	 * @param json
	 */
	public  abstract void init();
	
	/**
	 * 销毁资源
	 */
	public  abstract void destory();
	
	private String ip;
	
	private String password;
	
	private String path;
	
	private int port;
	
	public String getJson(){
		ServerDto serverDto = new ServerDto();
		serverDto.setConnectServer(ip+SPLIT+port);
		serverDto.setPassword(password);
		
		return JSON.toJSONString(serverDto);
	}
	
	public AbstractServerRegister(){
		String ip = IPUtils.getIp();
		this.ip = ip;
		
		String password = UUID.randomUUID().toString();
		this.password = password;
		
		this.port = Sr2fConfig.getPort();

		this.path = Sr2fConfig.getPath();
	}
}
