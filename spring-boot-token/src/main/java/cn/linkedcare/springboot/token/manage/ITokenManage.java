package cn.linkedcare.springboot.token.manage;

import javax.annotation.Resource;

import cn.linkedcare.springboot.redis.template.RedisTemplate;

/**
 * token刷新接口
 * @author wl
 *
 */
public interface ITokenManage {
	
	public static final int TIME_OUT=60*15;//超时时间
	
	public static final String TOKEN_PRE="springboot_token_";
	
	
	
	/**
	 * 刷新token
	 */
	public void refreshToken();

}
