package cn.linkedcare.springboot.sr2f.server;

import java.util.UUID;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.sr2f.dto.ServerDto;
import cn.linkedcare.springboot.sr2f.utils.IPUtils;
import lombok.Data;

@Data
public abstract class AbstractServerRegister {

	public  abstract void init(String path,String json);
	
	private String ip;
	
	private String password;
	
	public AbstractServerRegister(String path,int port){
		String ip = IPUtils.getIp();
		this.ip = ip;
		
		String password = UUID.randomUUID().toString();
		this.password = password;
		
		ServerDto serverDto = new ServerDto();
		
		serverDto.setPassword(password);
		serverDto.setIp(ip);
		serverDto.setPort(port);
		
		String json = JSON.toJSONString(serverDto);
	
		init(path,json);
	}
}
