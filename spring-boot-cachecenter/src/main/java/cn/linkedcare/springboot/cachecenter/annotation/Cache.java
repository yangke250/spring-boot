package cn.linkedcare.springboot.cachecenter.annotation;

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
public @interface Cache {
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
	
	/**
	 * 值为空时候，是否设置默认值到redis
	 * @return
	 */
	public boolean nullSetDefalutValue() default false;
}
