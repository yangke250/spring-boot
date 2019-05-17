package cn.linkedcare.springboot.sr2f.dto;

import lombok.Data;

@Data
public class ServerDto {
	public static final String SPLIT=":";
	
	private String connectServer;
	private String password;
}
