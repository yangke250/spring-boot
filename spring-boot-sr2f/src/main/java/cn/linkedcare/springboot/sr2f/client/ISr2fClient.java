package cn.linkedcare.springboot.sr2f.client;

import java.util.List;

import cn.linkedcare.springboot.sr2f.dto.ServerDto;

/**
 * 服务客户端
 * @author wl
 *
 */
public interface ISr2fClient {
	
	/**
	 * 监听相关的path
	 * @return
	 */
	public String path();

	/**
	 * 
	 * @param serverDtos
	 */
	public void changeNotify(List<ServerDto> serverDtos);
}
