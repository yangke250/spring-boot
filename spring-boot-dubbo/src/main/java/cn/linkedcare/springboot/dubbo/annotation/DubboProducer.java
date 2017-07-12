package cn.linkedcare.springboot.dubbo.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface DubboProducer {
	//dubbo实现类的服务
	public Class<?> refClass();
	//dubbo引用类的服务
	public Class<?> interfaceClass();
	//超时时间
	public long timeout() default 1000l;
	//错误的时候重试的次数
	public int retry() default 0;
}
