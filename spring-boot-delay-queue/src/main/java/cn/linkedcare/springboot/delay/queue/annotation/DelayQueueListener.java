package cn.linkedcare.springboot.delay.queue.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DelayQueueListener {
	
	/**
	 * 消费延时队列名称
	 * @return
	 */
	public String[] topic();
	
	/**
	 * 是否自动提交，消费异常的时候，也提交消费数据
	 * @return
	 */
	public boolean aotuCommit() default false;
}
