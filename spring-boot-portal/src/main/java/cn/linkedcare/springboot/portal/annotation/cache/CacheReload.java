package cn.linkedcare.springboot.portal.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法执行完以后，会覆盖缓存
 * @author wl
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheReload {
	/**
	 * 当keymethod值为空的时候，会以appname+uri的值为key
	 * @return
	 */
	public String keyMethod();
	
	/**
	 * 缓存失效的时间
	 * @return
	 */
	public int timeout();
}
