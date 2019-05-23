package cn.linkedcare.springboot.cachecenter.refresh.task;

import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import cn.linkedcare.springboot.cachecenter.refresh.AbstractCacheRefresh;

import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
public class CacheTimeWheel {
	@Data
	public static class TaskDto{
		private int count;//需要遍历几遍
		private AbstractCacheRefresh<?> t;
	}
	
	private volatile int current = 0;
	private final int timeWheelSize = 60;
	
	
	private static List<CopyOnWriteArrayList<TaskDto>> wheelList = 
			new CopyOnWriteArrayList<CopyOnWriteArrayList<TaskDto>>();

	static{
		for(int i=0;i<60;i++) {
			wheelList.add(new CopyOnWriteArrayList<TaskDto>());
		}
	}
	
	public static void main(String[] args) {
		System.out.println(86400/60);
		System.out.println(50%60);
	}
	
	
	
	public void add(AbstractCacheRefresh<?> t,int timeout) {
			TaskDto task = new TaskDto();
			//计算需要几轮
			int count    = timeout/timeWheelSize;
			int position = timeout%timeWheelSize+current;
			
			//和当前的时间轮做比较
			if(position>=timeWheelSize) {
				count=count+1;
				position=position-timeWheelSize;
			}
			
			log.info("CacheTimeWheel add: {},{}",t.getClass(),count);
			task.setCount(count);
			task.setT(t);
			
			wheelList.get(position).add(task);
	}
	
	
	
	public void start() throws InterruptedException {
		while(true) {
			try{
				CopyOnWriteArrayList<TaskDto> list = wheelList.get(current);
				
				for(TaskDto t:list) {
					if(t.count==0) {
						list.remove(t);
						
						CacheQueueHandler.add(t.getT());
						
						add(t.getT(),t.getT().refreshTime());
					}else {
						t.setCount(t.getCount()-1);
					}
				}
				//当时间轮超过上限的时候，
				current++;
				if(current>=60) {
					current=0;
				}
			
				Thread.sleep(1000l);
			}catch(Exception e){
				e.printStackTrace();
				log.error(e.getMessage(),e);
			}
			
		}
	}

}
