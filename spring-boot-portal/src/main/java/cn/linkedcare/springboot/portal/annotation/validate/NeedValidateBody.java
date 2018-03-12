package cn.linkedcare.springboot.portal.annotation.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.groups.Default;

/**
 * 对象需要验证的时候
 * @author wl
 *
 */
@Target({ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedValidateBody {
	/**
	 * 需要验证的对象
	 * @return
	 */
	Class<?> group() default Default.class;
}
