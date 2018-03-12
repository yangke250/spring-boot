package cn.linkedcare.springboot.portal.aop;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public abstract class AbstarctPostAop extends AbstarctAop{
	
	public static Logger logger = LoggerFactory.getLogger(AbstarctPreAop.class);
	

	/**
	 * filter类型
	 * @return
	 */
	public AopType type(){
		return AopType.post;
	} 
	
	/**
	 * 返回true的时候，调用链继续向下，
	 * 返回false的时候，调用直接返回
	 * @param request
	 * @param response
	 * @param filterChain
	 * @return
	 */
	public abstract boolean doFilter(ProceedingJoinPoint pjp,Object result);
	
	/**
	 * 得到http response
	 * @return
	 */
	protected HttpServletResponse getResponse(){
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletResponse response = sra.getResponse();
		return response;
	}
	
	/**
	 * 得到http response
	 * @return
	 */
	protected void wirte(String str){
		HttpServletResponse response = getResponse();
		
		try {
			response.getWriter().write(str);
			response.getWriter().close();
		} catch (IOException e) {
			logger.error("IOException:{}",e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 得到http request
	 * @return
	 */
	protected HttpServletRequest getRequest(){
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = sra.getRequest();
		return request;
	}
	
	
	/**
	 * 验证完以后的结果
	 * @author wl
	 *
	 */
	public static class FilterResult{
		private boolean result = true;//结果是否通过，true的时候，进行下一个调用链的
		private String  msg;//返回的结果
		
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
}
