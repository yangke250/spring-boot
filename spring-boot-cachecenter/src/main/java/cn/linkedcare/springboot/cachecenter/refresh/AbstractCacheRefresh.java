package cn.linkedcare.springboot.cachecenter.refresh;

import java.lang.reflect.ParameterizedType;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.redis.template.RedisTemplate;

/**
 * 抽象的定时cache刷新的类
 * @author wl
 *
 * @param <T>
 */
public abstract class AbstractCacheRefresh<T> implements ICacheRefresh<T>{
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReadLock   readLock =  lock.readLock();
	private final WriteLock writeLock =  lock.writeLock();
	private T t;
	@Resource
	private RedisTemplate redisTemplate;
	
	private ParameterizedType p;
	
	public Class<T> getTClass() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
	
	
	public abstract T cache();
	
	public static final String PRE="springboot_cache_";
	
	
	
	@Override
	public void abstractCache() {
		T cache  = cache();
		String key = PRE+this.getClass().getName();
		try {
			writeLock.lock();
			this.t = cache;
			
			redisTemplate.set(key,JSON.toJSONString(cache));
		}finally {
			writeLock.unlock();
		}
	}
	
	@Override
	public T get() {
		String key = PRE+this.getClass().getName();
		try {
			readLock.lock();
			String result = redisTemplate.get(key);
			if(result==null) {
				return null;
			}
		
			return JSON.parseObject(result,getTClass());
		}finally {
			readLock.unlock();
		}
	}
	
}
