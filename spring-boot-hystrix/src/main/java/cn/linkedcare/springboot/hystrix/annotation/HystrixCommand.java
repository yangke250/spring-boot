package cn.linkedcare.springboot.hystrix.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HystrixCommand {
	/**
	 * 单位时间内限制访问的次数
	 * @return
	 */
	public long rate();
	
	/**
	 * 单位时间，单位是秒
	 * @return
	 */
	public int timeout();
}
