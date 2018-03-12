package cn.linkedcare.springboot.portal.aop.pre;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import cn.linkedcare.springboot.portal.annotation.validate.NeedValidateBody;
import cn.linkedcare.springboot.portal.aop.AbstarctPreAop;

@Component
public class ValidateAop extends AbstarctPreAop{

	
	
	@Resource(name="validator")
	private Validator validator;
	
	/**
	 * 验证结果描述
	 * @author wl
	 *
	 */
	public static class ValidateResult{
		private boolean result =true;
		private String msg;
		
		public boolean isResult() {
			return result;
		}
		public void setResult(boolean result) {
			this.result = result;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		
		
	}
	
		
//	@NotNull 被注释的元素必须不为 null
//	@NotEmpty 被注释的字符串的必须非空
//	@Min(value) 被注释的元素必须是一个数字，其值必须大于等于指定的最小值
//	@Max(value) 被注释的元素必须是一个数字，其值必须小于等于指定的最大值
//	@Size(max, min) 被注释的元素的大小必须在指定的范围内
//	@Pattern(value) 被注释的元素必须符合指定的正则表达式
	
	
	private Class<? extends Annotation>[] annos = 
		new Class[]{NotNull.class,NotEmpty.class,Min.class,Max.class,
				Size.class,Pattern.class,Size.class,NeedValidateBody.class};
	
	
	

	
	
	/**
	 * 验证参数	
	 * @param arg
	 * @return
	 */
	private ValidateResult doValidate(Object arg,Annotation[] as){
		ValidateResult result = new ValidateResult();
		for(Annotation a:as){			
			if(a instanceof NotNull){
				result.setMsg(((NotNull)a).message());
				result.setResult(arg!=null?true:false);
			}else if(a instanceof NotBlank){
				result.setMsg(((NotBlank)a).message());
				result.setResult(arg!=null&&arg.toString().length()>0?true:false);
			}else if(a instanceof Min){
				long num = Long.parseLong(arg.toString());
				long min =((Min)a).value();
				
				result.setMsg(((Min)a).message());
				result.setResult(num>=min?true:false);
			}else if(a instanceof Max){
				long num = Long.parseLong(arg.toString());
				long max =((Max)a).value();
				
				result.setMsg(((Max)a).message());
				result.setResult(num<=max?true:false);
			}else if(a instanceof Pattern){//正则表达式
				String str = arg.toString();
				String reg = ((Pattern)a).regexp();
				
				java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(reg);
				Matcher m = pattern.matcher(str);
				
				result.setMsg(((Pattern)a).message());
				result.setResult(m.matches());
			}else if(a instanceof Size){
				String str = arg.toString();
				int min = ((Size)a).min();
				int max = ((Size)a).max();
				int length = str.length();
				
				result.setMsg(((Size)a).message());
				result.setResult(str!=null&&length>=min&&length<=max?true:false);
			}else if(a instanceof NeedValidateBody){
				NeedValidateBody needValidateBody = (NeedValidateBody)a;
				Set<ConstraintViolation<Object>> sets =  validator.validate(arg,needValidateBody.group());
				
				if(sets.size()>0){//只取第一错误信息
					result.setMsg(sets.iterator().next().getMessage());
				}
				result.setResult(sets.size()<=0?true:false);
			}
		}
		
		
		return result;
	}
	
	@Override
	public boolean doFilter(ProceedingJoinPoint pjp) {
		Object[] objects = pjp.getArgs();
		
		MethodSignature signature = (MethodSignature) pjp.getSignature();  
        Method method = signature.getMethod(); //获取被拦截的方法
        Annotation[][] as = method.getParameterAnnotations();
        
        int i =0;
		for(Object o:objects){
			ValidateResult result  = doValidate(o,as[i]);
			if(!result.isResult()){
				try {
					super.wirte("{\"msg\":\""+URLEncoder.encode(result.getMsg(), "utf-8") +"\",\"result\":400}");
				} catch (UnsupportedEncodingException e) {
					logger.error("UnsupportedEncodingException:{}",e);
				}
				return false;
			}
			i++;
		}
		
		return true;
	}

	@Override
	public int order() {
		return 2;
	}

	@Override
	public void finallyMethod(ProceedingJoinPoint pjp) {
		
	}



}
