package cn.linkedcare.springboot.cachecenter.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;

import cn.linkedcare.springboot.cachecenter.aop.RemoteCacheAop.CacheResult;

/**
 * 抽象aop
 * 
 * @author wl
 *
 */
public interface BaseAop extends Comparable<BaseAop> {

	/**
	 * 执行的顺序
	 * 
	 * @return
	 */
	int order();
	
	
	/**
	 * 方法执行后执行相关方法
	 * @param method 调用的方法
	 * @param args   参数
	 * @param target 对应的引用
	 * @return true代表命中缓存，不执行相关方法
	 */
	CacheResult executeBefore(Object target,Method method,Object[] args);
	
	/**
	 * 方法执行后执行
	 * @param target
	 * @param method
	 * @param args
	 * @param result 执行的方法
	 * @return true代表
	 */
	boolean executeAfter(Object target,Method method,Object[] args,Object result);
	
	

	// 值小的先执行
	@Override
	default public int compareTo(BaseAop o) {
		if (this.order() == o.order()) {
			return 0;
		} else if (this.order() > o.order()) {
			return 1;
		} else {
			return -1;
		}
	}
}
