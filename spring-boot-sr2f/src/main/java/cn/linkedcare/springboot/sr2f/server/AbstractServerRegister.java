package cn.linkedcare.springboot.sr2f.server;

import java.util.UUID;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.sr2f.config.Sr2fConfig;
import cn.linkedcare.springboot.sr2f.dto.ServerDto;
import cn.linkedcare.springboot.sr2f.utils.IPUtils;
import lombok.Data;

@Data
public abstract class AbstractServerRegister {

	
	
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
	
	private static String ip;
	
	private static String password;
	
	private static String path;
	
	private static int port;
	
	public static String getConnectServer(){
		return ip+ServerDto.SPLIT+port;
	}
	
	public static String getJson(){
		ServerDto serverDto = new ServerDto();
		serverDto.setConnectServer(ip+ServerDto.SPLIT+port);
		serverDto.setPassword(password);
		
		return JSON.toJSONString(serverDto);
	}
	
	public AbstractServerRegister(){
		AbstractServerRegister.ip = IPUtils.getIp();
		
		AbstractServerRegister.password = UUID.randomUUID().toString();
		
		AbstractServerRegister.port = Sr2fConfig.getPort();

		AbstractServerRegister.path = Sr2fConfig.getPath();
	}

	public static String getPath() {
		return path;
	}

	
	
	
	
}
