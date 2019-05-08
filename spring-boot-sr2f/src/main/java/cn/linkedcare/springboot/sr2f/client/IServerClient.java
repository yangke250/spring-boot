package cn.linkedcare.springboot.sr2f.client;

import java.util.List;

import cn.linkedcare.springboot.sr2f.dto.ServerDto;

/**
 * 服务客户端
 * @author wl
 *
 */
public interface IServerClient {

	/**
	 * 
	 * @param serverDtos
	 */
	public void changeNotify(List<ServerDto> serverDtos);
}
