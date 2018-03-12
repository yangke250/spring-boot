package cn.linkedcare.springboot.portal.validate;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.cfg.defs.NotNullDef;
import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;

import cn.linkedcare.springboot.portal.dto.ValidateDto;
import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;
/**
 * 普通值验证
 * @author wl
 *
 */
public class PropertyValidate {
	
	public static void main(String[] agrs){
		ConstraintHelper ch = new ConstraintHelper();
		List<ConstraintValidatorDescriptor<NotNull>> list = ch.getAllValidatorDescriptors(NotNull.class);
		for(ConstraintValidatorDescriptor<NotNull> l:list){
			System.out.println(l);
		}
		
		System.out.println(list);
	}
	

//	@NotNull 被注释的元素必须不为 null
//	@NotEmpty 被注释的字符串的必须非空
//	@Min(value) 被注释的元素必须是一个数字，其值必须大于等于指定的最小值
//	@Max(value) 被注释的元素必须是一个数字，其值必须小于等于指定的最大值
//	@Size(max, min) 被注释的元素的大小必须在指定的范围内
//	@Pattern(value) 被注释的元素必须符合指定的正则表达式
//	@Length 被注释的字符串的大小必须在指定的范围内
	
//	@NotNull
//	@NotEmpty
//	private static ValidateDto doValidate(Object o){
//		ValidateDto dto = new ValidateDto();
//		Class classz = o.getClass();
//		Annotation[] annotations = classz.getAnnotations();
//		//不需要验证
//		if(annotations==null){
//			return dto;
//		}
//		
//		ConstraintHelper c = new ConstraintHelper();
//		c.getAllValidatorDescriptors(annotationType)
//		
//		NotNullValidator v = new NotNullValidator();
//		v.isValid(object, constraintValidatorContext)
//		
//		new ConstraintValidatorContextImpl();
//		NotNull o.getClass().getAnnotation(NotNull.class);
//		for(Annotation a:annotations){
//			
//		}
//		
//		NotNullDef notNullDef = new NotNullDef();
//		notNullDef.
//		return dto;
//	}
	
}