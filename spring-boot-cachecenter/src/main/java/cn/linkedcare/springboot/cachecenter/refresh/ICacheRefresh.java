package cn.linkedcare.springboot.cachecenter.refresh;



/**
 * 缓存永不失效刷新接口
 * @author wl
 *
 */
interface ICacheRefresh<T> {
	
	/**
	 * 任务组描述，相同的名称，代表一组任务，做高可用
	 * @return
	 */
	public String cacheGroupName();
	
	/**
	 * 需要缓存的数据,最终会以json格式保存在redis里
	 * @return
	 */
	public void abstractCache();
	
	/**
	 * 多少时间刷新一次,单位是秒
	 * @return
	 */
	public int refreshTime();

	/**
	 * 得到相关数据
	 * @return
	 */
	public T get();
}
