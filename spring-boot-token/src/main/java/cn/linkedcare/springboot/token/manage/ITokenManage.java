package cn.linkedcare.springboot.token.manage;

/**
 * token刷新接口
 * @author wl
 *
 */
public interface ITokenManage {
	
	/**
	 * 刷新token
	 */
	public void refreshToken();

}
