package cn.linkedcare.springboot.redis.template;

import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * redis相关的操作
 * @author wl
 *
 */
public interface RedisTemplate {
	
	/**
	 * redis增加相关监听
	 */
	public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns); 
	
	/**
	 * set数据结构，增加
	 * @param key
	 * @param value
	 */
	public long sadd(String key,String value);
	
	/**
	 * 得到string类型的多个值
	 * @param keys
	 * @return
	 */
	public List<String> mget(String... keys);

	/**
	 * 得到set数据结构所有成员列表
	 * @param key
	 * @return
	 */
	public Set<String> smembers(String key);
	
	
	/**
	 * 查询set成员个数
	 * @param key
	 * @return
	 */
    public long scard(String key);
	
	/**
	 * 删除set数据结构的成员
	 * @param key
	 * @param member
	 * @return
	 */
	public long srem(String key, String... member);
	
	/**
	 * 查看key还存在的时间
	 * @param key
	 * @return
	 */
	public Long ttl(String key);
    
	/**
	 * string的数据结构
	 * @param key
	 * @param value
	 * @return
	 */
    public String set(String key, String value);

    /**
     * 设置key的数据结构和超时时间
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public String setex(String key, int seconds, String value);

    /**
     * 设置key的数据结构和超时时间
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public String setex(byte[] key, int seconds,byte[] values);
    
    public String get(String key);
    
    public byte[] get(byte[] key);

    public long incr(String key);

    public long incrBy(String key, long rewards);

    public long zcount(String key, double min, double max);

    public long zremrangeByRank(String key, int start, int end);

    public long zremrangeByScore(String key, long start, long end);

    public long zadd(String key, double score, String member);

    /**
     * NotNull when redis server is working stably
     * @param key
     * @return
     */
    Boolean exists(String key);

    public long lpush(String key, String member);

    public long llen(String key);

    public double zscore(String key, String member);
    Double getZScore(String key, String member);
    public long del(String key);

    public long zrem(String key, String member);

    public long zrem(String key, String[] members);

    public Set<String> zrange(String key, long start, long end);
    public Set<Tuple> zrevrangeWithScores(String key, int start, int end);

    public Set<Tuple> zrangeWithScores(String key, int start, int end);
    Set<String> zrangeByScore(String key, Double min, Double max);
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
                                                 double min);

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
                                                 double min, int offset, int count);

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max);

    public Set<Tuple> zrangeByScoreWithScores(String key, double min,
                                              double max, int offset, int count);
    public long hset(String key, String field, String value);
    public List<String> hmget(String key, String...fields);
    public long zcard(String key);
    public long hdel(String key, String... fields);
    public long expire(String key, int seconds);
    public Map<String, String> batchSetex(Map<String, String> map, int seconds);
    public Map<String, String> batchSet(Map<String, String> map);

    public Map<String, Long> batchZadd(Map<String, List<String>> indexIdMap, int maxCount, double score);

    Set<String> batchZrangeMergedValue(Set<String> keySet, long start, long end);
    Map<String, Set<String>> batchZrange(Set<String> keySet, long start, long end);
    long zadd(String key, Map<String, Double> members);
    Boolean hexists(String key, String field);
    Set<String> hkeys(String key);
    long hlen(String key);
    Double zincrby(String key, double score, String member);
    List<Long> batchHset(Set<String> keySet, String field, String value);
    Map<String, String> batchHget(Set<String> keySet, String field);
    Map<String, Long> batchZrem(Map<String, Set<String>> keyMemberSetMap);
    Map<String, Long> batchZremKeyMemberPairs(Map<String,String> keyMemberMap);
    Map<String, Long> batchHdel(Map<String, String> keyFieldMap);
    Map<String, String> hgetAll(String key);

    Map<String, Double> batchZincrby(String key, Map<String, Integer> valueOffsetMap);

    Map<String, Double> batchZincrby(Map<String, String> keyValueMap, int offset);

    String hget(String key, String field);
    Set<String> zrevrangeByScore(String key, double max, double min);
    Map<String, Boolean> batchHexists(Set<String> keySet, String field);
    Boolean zexists(String key, String member);
    Set<String> batchZrangeByScoreMergedValue(Set<String> keySet, Double min, Double max);
    Map<String, Set<String>> batchZrangeByScore(Set<String> keySet, Double min, Double max);
    Map<String, Set<String>> batchHkeys(Set<String> keySet);
    Map<String, Long> batchDel(Set<String> keySet);
    Map<String, Long> batchZcard(Set<String> keySet);
    long setnx(String key, String value);
    Map<String, Long> batchZadd(Map<String, Long> keyScoreMap,String member);
}
