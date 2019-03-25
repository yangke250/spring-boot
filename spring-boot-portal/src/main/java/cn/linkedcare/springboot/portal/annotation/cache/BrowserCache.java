package cn.linkedcare.springboot.portal.annotation.cache;


/**
 * 浏览器缓存，支持浏览器重启缓存和协商缓存
 * @author wl
 *
 */
public @interface BrowserCache {
	
	/**
	 * 是否使用浏览器缓存，默认60秒超时
	 * @return
	 */
	public int timeout() default 60;
}
