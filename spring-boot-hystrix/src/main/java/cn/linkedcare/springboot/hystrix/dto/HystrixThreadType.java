package cn.linkedcare.springboot.hystrix.dto;

/**
 * hystrix线程池类型
 * @author wl
 *
 */
public enum HystrixThreadType {
	normal,//普通线程池
	core;//核心链路
}
