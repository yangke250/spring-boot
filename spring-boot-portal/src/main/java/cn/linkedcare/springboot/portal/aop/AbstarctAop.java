package cn.linkedcare.springboot.portal.aop;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;


/**
 * 所有请求经过网关路由前，必须做和业务无关的逻辑
 * pre   限流->熔断->验证->授权->缓存->路由
 * post  统一返回处理
 * error 统一错误处理
 * @author wl
 *
 */
public abstract class AbstarctAop implements Comparable<AbstarctAop>{

	public static enum AopType{
		pre,
		post,
		error;
	}
	
	/**
	 * 根据值的大小，定义执行的顺序
	 * @return
	 */
	public abstract int order();
	
	/**
	 * filter类型
	 * @return
	 */
	public abstract AopType type();
	
	
	/**
	 * 最终回调的方法
	 */
	public abstract void finallyMethod(ProceedingJoinPoint pjp);
	
	
	
	//值小的先执行
	@Override
	public int compareTo(AbstarctAop o) {
		if(this.order()==o.order()){
			return 0;
		}else if(this.order()>o.order()){
			return 1;
		}else{
			return -1;
		}
	}


}
