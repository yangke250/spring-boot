package cn.linkedcare.springboot.token.manage;


/**
 * token刷新接口
 * @author wl
 *
 */
public interface ITokenManage {
	
	public static final int TIME_OUT=60*15;//提前15分钟刷新数据。
	
	public static final String TOKEN_PRE="springboot_token_";
	
	
	
	/**
	 * 刷新token
	 */
	public void refreshToken();
	
	/**
	 * 下次超时的时间
	 * @return
	 */
	public long nextTimeOut();

}
