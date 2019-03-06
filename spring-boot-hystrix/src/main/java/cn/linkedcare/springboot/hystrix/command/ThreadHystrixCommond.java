package cn.linkedcare.springboot.hystrix.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import cn.linkedcare.springboot.hystrix.dto.HystrixDto;

/**
 * 默认为线程池隔离
 * 线程池只会有2种
 * 一种普通链路和核心链路
 * @author wl
 *
 */
public class ThreadHystrixCommond extends HystrixCommand<String>{

	//默认方法调用超时的时间
	public static final int TIME_OUT = 5000;
	//熔断时间
	public static final int BREAKER_TIME = 5000;
	//熔断错误的次数
	public static final int MIN_BREAK_TIME = 5;
	
	public static final Logger logger  = LoggerFactory.getLogger(ThreadHystrixCommond.class);
	
	private HystrixDto dto;
	
	public ThreadHystrixCommond(HystrixDto dto){
		super(HystrixCommand.Setter.
                //设置GroupKey 用于dashboard 分组展示
                withGroupKey(HystrixCommandGroupKey.Factory.asKey(dto.getAppName()))
                //设置commandKey 用户隔离线程池，不同的commandKey会使用不同的线程池
                .andCommandKey(HystrixCommandKey.Factory.asKey(dto.getType().name()))
                //设置线程池名字的前缀，默认使用commandKey
//                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(dto.getType().name()))
//                //设置线程池相关参数
//                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
//                .withCoreSize(Runtime.getRuntime().availableProcessors()*10)
//                .withMaxQueueSize(Runtime.getRuntime().availableProcessors()*5)
//                .withQueueSizeRejectionThreshold(2))
                        //设置command相关参数
                
                		.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        //是否开启熔断器机制
                        .withCircuitBreakerEnabled(true)
                         //舱壁隔离策略
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        //超时方法执行超时时间
                        .withExecutionTimeoutInMilliseconds(TIME_OUT)
                        //circuitBreaker打开后多久关闭
                        .withCircuitBreakerSleepWindowInMilliseconds(10000000)
                		.withCircuitBreakerRequestVolumeThreshold(1)
                        //设置每组command可以申请的permit最大数
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)
                		)
                        
                		);
		this.dto = dto;
	}
	
	
	
	protected ThreadHystrixCommond(HystrixCommandGroupKey group) {
		super(group);
	}

	@Override
	protected String run() throws Exception {
		logger.info("{}",dto.getChain());
		logger.info("{}",dto.getRequest());
		logger.info("{}",dto.getResponse());
		dto.getChain().doFilter(dto.getRequest(),dto.getResponse());
		return null;
	}

	protected Exception getExceptionFromThrowable(Throwable t) {
        logger.error("exception:{}",t);
		return super.getExceptionFromThrowable(t);
    }
	
	protected String getFallback() {
		return "error";
	}
}
