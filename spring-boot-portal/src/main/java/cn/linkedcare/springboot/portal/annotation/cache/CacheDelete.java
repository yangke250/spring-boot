package cn.linkedcare.springboot.portal.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * cache会先从缓里取，如果取不到走方法，再放进缓存
 * @author wl
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheDelete {
	/**
	 * 当keymethod值为空的时候，会以appname+uri的值为key
	 * @return
	 */
	public String keyMethod();
}
