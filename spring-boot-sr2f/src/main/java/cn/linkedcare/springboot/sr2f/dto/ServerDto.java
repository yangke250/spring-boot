package cn.linkedcare.springboot.sr2f.dto;

import lombok.Data;

@Data
public class ServerDto {
	private String  ip;
	private int port;
	private String password;
}
