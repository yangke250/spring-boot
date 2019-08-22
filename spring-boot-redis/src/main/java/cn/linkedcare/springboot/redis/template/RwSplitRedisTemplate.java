package cn.linkedcare.springboot.redis.template;


import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

import java.util.*;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author wl
 *
 */

public class RwSplitRedisTemplate implements RedisTemplate {

    private final static Logger log           = LoggerFactory.getLogger(RwSplitRedisTemplate.class);
    private final static String NULL_PASSWORD = "password";

    private Pool<Jedis> pool;
 
    
    private RwSplitRedisTemplate(){
    	
    }

    public RwSplitRedisTemplate(Pool<Jedis> pool){
        this.pool = pool;
    }

   
    private long executeCommandForReturnLong(JedisCommandExecutor<Long> executor, Object... argArray) {
        Long result = executeCommand(executor, argArray);
        if (null == result) {
            return -1l;
        }
        return result;
    }

    private <T> T executeCommand(Function<Jedis,T> function) {
    	
    	Jedis jedis  = null;
    	try{
        	jedis = pool.getResource();
        	return  function.apply(jedis);
    	}finally {
    		if(jedis!=null)
    			pool.returnResource(jedis);
		}
    }

    
    
    private <T> T executeCommand(JedisCommandExecutor<T> executor, Object... argArray) {
    	Jedis jedis  = null;
    	
    	try{
    		jedis = pool.getResource();
    				
    		return executor.execute(jedis, argArray);
        } catch (JedisConnectionException connEx) {
            log.error("JedisConnectionException error", connEx);
        } catch (Exception e) {
            log.error("redis access exception", e);
        }finally {
    		if(jedis!=null)
    			pool.returnResource(jedis);
		}
        return null;
    }

    public Long ttl(String key) {
    	return executeCommand(new JedisCommandExecutor<Long>() {
            public Long execute(Jedis Jedis, Object... argArray) {
            	return Jedis.ttl((String) argArray[0]);
            }
        }, key);
    }
    public String set(String key, String value) {
        return executeCommand(new JedisCommandExecutor<String>() {
            public String execute(Jedis Jedis, Object... argArray) {
            	return Jedis.set((String) argArray[0],
                        (String) argArray[1]);
            }
        }, key, value);
    }

    public String setex(String key, int seconds, String value) {
        return executeCommand(new JedisCommandExecutor<String>() {
            public String execute(Jedis resource, Object... argArray) {
            	return resource.setex((String) argArray[0],
                        (Integer) argArray[1], (String) argArray[2]);
            }
        }, key, seconds, value);
    }

    public String setex(byte[] key, int seconds,byte[] values) {
        return executeCommand(new JedisCommandExecutor<String>() {
            public String execute(Jedis resource, Object... argArray) {
            	return resource.setex((byte[])argArray[0],
                        (Integer) argArray[1], (byte[])argArray[2]);
            }
        }, key, seconds, values);
    }
    
    public String get(String key) {
        return executeCommand(new JedisCommandExecutor<String>() {
            public String execute(Jedis resource, Object... argArray) {
                return resource.get((String) argArray[0]);
            }
        }, key);
    }
    
    
    public byte[] get(byte[] key) {
        return executeCommand(new JedisCommandExecutor<byte[]>() {
            public byte[] execute(Jedis resource, Object... argArray) {
                return resource.get((byte[]) argArray[0]);
            }
        }, key);
    }
    
    public Boolean exists(String key){
        return executeCommand(new JedisCommandExecutor<Boolean>() {
            public Boolean execute(Jedis resource, Object... argArray) {
                return resource.exists((String) argArray[0]);
            }
        }, key);
    }

    public long incr(String key) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.incr((String) argArray[0]);
                    }
                }, key);
    }

    public long incrBy(String key, long rewards) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.incrBy((String) argArray[0],
                                (Long) argArray[1]);
                    }
                }, key, rewards);

    }

    public long zcount(String key, double min, double max) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.zcount((String) argArray[0],
                                (Double) argArray[1], (Double) argArray[2]);
                    }
                }, key, min, max);

    }

    public long zremrangeByRank(String key, int start, int end) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.zremrangeByRank((String) argArray[0],
                                (Integer) argArray[1], (Integer) argArray[2]);
                    }
                }, key, start, end);
    }

    public long zremrangeByScore(String key, long start, long end) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.zremrangeByScore((String) argArray[0],
                                (Long) argArray[1], (Long) argArray[2]);
                    }
                }, key, start, end);
    }

    public long zadd(String key, double score, String member) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.zadd((String) argArray[0],
                                (Double) argArray[1], (String) argArray[2]);
                    }
                }, key, score, member);
    }

    public long lpush(String key, String member) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.lpush((String) argArray[0],
                                (String) argArray[1]);
                    }
                }, key, member);
    }

    public long llen(String key) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.llen((String) argArray[0]);
                    }
                }, key);
    }

    public double zscore(String key, String member) {
        Double result = executeCommand(
                new JedisCommandExecutor<Double>() {
                    public Double execute(Jedis resource,
                                          Object... argArray) {
                        Double ret = resource.zscore((String) argArray[0],
                                (String) argArray[1]);
                        if (null != ret) {
                            return ret;
                        }
                        return -1d;
                    }
                }, key, member);
        if (null == result) {
            return -1d;
        }
        return result;

    }

    @Override
    public Double getZScore(String key, String member) {
        return executeCommand(
                new JedisCommandExecutor<Double>() {
                    public Double execute(Jedis resource,
                                          Object... argArray) {
                        Double ret = resource.zscore((String) argArray[0],
                                (String) argArray[1]);
                        if (null != ret) {
                            return ret;
                        }
                        return -1d;
                    }
                }, key, member);
    }

    public long del(String key) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.del((String) argArray[0]);
                    }
                }, key);
    }

    public long zrem(String key, String member) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {

                    	
                    	return resource.zrem((String) argArray[0],
                                (String) argArray[1]);
                    }
                }, key, member);
    }

    public long zrem(String key, String[] members) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.zrem((String) argArray[0],
                                (String[]) argArray[1]);
                    }
                }, key, members);
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        return executeCommand(new JedisCommandExecutor<Set<String>>() {
            public Set<String> execute(Jedis resource, Object... argArray) {
                return resource.zrange((String) argArray[0],
                        (Long) argArray[1], (Long) argArray[2]);
            }
        }, key, start, end);
    }

    @Override
    public Set<String> zrevrange(String key, long start, long end) {
        return executeCommand(new JedisCommandExecutor<Set<String>>() {
            public Set<String> execute(Jedis resource, Object... argArray) {
                return resource.zrevrange((String) argArray[0],
                        (Long) argArray[1], (Long) argArray[2]);
            }
        }, key, start, end);
    }
    
    
    public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
        return executeCommand(new JedisCommandExecutor<Set<Tuple>>() {
            public Set<Tuple> execute(Jedis resource, Object... argArray) {
                return resource.zrevrangeWithScores((String) argArray[0],
                        (Integer) argArray[1], (Integer) argArray[2]);
            }
        }, key, start, end);
    }

    public Set<Tuple> zrangeWithScores(String key, int start, int end) {
        return executeCommand(new JedisCommandExecutor<Set<Tuple>>() {
            public Set<Tuple> execute(Jedis resource, Object... argArray) {
                return resource.zrangeWithScores((String) argArray[0],
                        (Integer) argArray[1], (Integer) argArray[2]);
            }
        }, key, start, end);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
                                                 double min) {
        return executeCommand(new JedisCommandExecutor<Set<Tuple>>() {
            public Set<Tuple> execute(Jedis resource, Object... argArray) {
                return resource.zrevrangeByScoreWithScores(
                        (String) argArray[0], (Double) argArray[1],
                        (Double) argArray[2]);
            }
        }, key, max, min);
    }

    public Set<String> zrevrangeByScore(String key, double max, double min) {
        return executeCommand(new JedisCommandExecutor<Set<String>>() {
            public Set<String> execute(Jedis resource, Object... argArray) {
                return resource.zrevrangeByScore(
                        (String) argArray[0], (Double) argArray[1],
                        (Double) argArray[2]);
            }
        }, key, max, min);
    }

    @Override
    public Map<String, Boolean> batchHexists(Set<String> keySet, String field) {
        return executeCommand(new JedisCommandExecutor<Map<String, Boolean>>() {
            @Override
            public Map<String, Boolean> execute(Jedis resource, Object... argArray) {
                Set<String> keySetParam = (Set<String>) argArray[0];
                String fieldParam = (String) argArray[1];
                Pipeline pipeline = resource.pipelined();
                Map<String, Boolean> result = new HashMap<String, Boolean>();
                Map<String, Response<Boolean>> responseMap = new HashMap<String, Response<Boolean>>();
                for (String key : keySetParam) {
                    Response<Boolean> response = pipeline.hexists(key, fieldParam);
                    responseMap.put(key, response);
                }
                pipeline.sync();
                for (Map.Entry<String, Response<Boolean>> entry : responseMap.entrySet()) {
                    Boolean value = entry.getValue().get();
                    result.put(entry.getKey(), entry.getValue().get());
                }
                return result;
            }
        }, keySet, field);
    }

    @Override
    public Boolean zexists(String key, String member) {
        Double result = executeCommand(
                new JedisCommandExecutor<Double>() {
                    public Double execute(Jedis resource,
                                          Object... argArray) {
                        Double ret = resource.zscore((String) argArray[0],
                                (String) argArray[1]);
                        if (null != ret) {
                            return ret;
                        }
                        return -1d;
                    }
                }, key, member);
        if (result == null) {
            return null;
        }
        return result > 0;
    }

    @Override
    public Set<String> batchZrangeByScoreMergedValue(Set<String> keySet, Double min, Double max) {
        return executeCommand(new JedisCommandExecutor<Set<String>>() {
            @Override
            public Set<String> execute(Jedis resource, Object... argArray) {
                Set<String> keySetParam = (Set<String>) argArray[0];
                Double minParam = (Double) argArray[1];
                Double maxParam = (Double) argArray[2];
                Pipeline pipeline = resource.pipelined();
                Set<String> result = new HashSet<String>();
                Map<String, Response<Set<String>>> responseMap = new HashMap<String, Response<Set<String>>>();
                for (String key : keySetParam) {
                    Response<Set<String>> response = pipeline.zrangeByScore(key, minParam, maxParam);
                    responseMap.put(key, response);
                }
                pipeline.sync();
                for (Response<Set<String>> response : responseMap.values()) {
                    result.addAll(response.get());
                }
                return result;
            }
        }, keySet, min, max);
    }

    @Override
    public Map<String, Set<String>> batchZrangeByScore(Set<String> keySet, Double min, Double max) {
        return executeCommand(new JedisCommandExecutor<Map<String, Set<String>>>() {
            @Override
            public Map<String, Set<String>> execute(Jedis resource, Object... argArray) {
                Set<String> keySetParam = (Set<String>) argArray[0];
                Double minParam = (Double) argArray[1];
                Double maxParam = (Double) argArray[2];
                Pipeline pipeline = resource.pipelined();
                Map<String, Set<String>> result = new HashMap<String, Set<String>>();
                Map<String, Response<Set<String>>> responseMap = new HashMap<String, Response<Set<String>>>();
                for (String key : keySetParam) {
                    Response<Set<String>> response = pipeline.zrangeByScore(key, minParam, maxParam);
                    responseMap.put(key, response);
                }
                pipeline.sync();
                for (Map.Entry<String, Response<Set<String>>> entry : responseMap.entrySet()) {
                    Set<String> set = entry.getValue().get();
                    if (set != null && !set.isEmpty()) {
                        result.put(entry.getKey(), set);
                    }
                }
                return result;
            }
        }, keySet, min, max);
    }

    @Override
    public Map<String, Set<String>> batchHkeys(Set<String> keySet) {
        return executeCommand(new JedisCommandExecutor<Map<String, Set<String>>>() {
            @Override
            public Map<String, Set<String>> execute(Jedis resource, Object... argArray) {
                Set<String> keySetParam = (Set<String>) argArray[0];
                Pipeline pipeline = resource.pipelined();
                Map<String, Set<String>> result = new HashMap<String, Set<String>>();
                Map<String, Response<Set<String>>> responseMap = new HashMap<String, Response<Set<String>>>();
                for (String key : keySetParam) {
                    Response<Set<String>> response = pipeline.hkeys(key);
                    responseMap.put(key, response);
                }
                pipeline.sync();
                for (Map.Entry<String, Response<Set<String>>> entry : responseMap.entrySet()) {
                    Set<String> set = entry.getValue().get();
                    if (set != null && !set.isEmpty()) {
                        result.put(entry.getKey(), set);
                    }
                }
                return result;
            }
        }, keySet);
    }

    @Override
    public Map<String, Long> batchDel(Set<String> keySet) {
        return executeCommand(new JedisCommandExecutor<Map<String, Long>>() {
            @Override
            public Map<String, Long> execute(Jedis resource, Object... argArray) {
                Set<String> keySetParam = (Set<String>) argArray[0];
                Pipeline pipeline = resource.pipelined();
                Map<String, Response<Long>> responseMap = new HashMap<String, Response<Long>>();
                for (String key : keySetParam) {
                    Response<Long> response = pipeline.del(key);
                    responseMap.put(key, response);
                }
                pipeline.sync();
                Map<String, Long> result = new HashMap<String, Long>();
                for (Map.Entry<String, Response<Long>> entry : responseMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().get());
                }
                return result;
            }
        }, keySet);
    }

    @Override
    public Map<String, Long> batchZcard(Set<String> keySet) {
        return executeCommand(new JedisCommandExecutor<Map<String, Long>>() {
            @Override
            public Map<String, Long> execute(Jedis resource, Object... argArray) {
                Set<String> keySetParam = (Set<String>) argArray[0];
                Pipeline pipeline = resource.pipelined();
                Map<String, Long> result = new HashMap<String, Long>();
                Map<String, Response<Long>> responseMap = new HashMap<String, Response<Long>>();
                for (String key : keySetParam) {
                    Response<Long> response = pipeline.zcard(key);
                    responseMap.put(key, response);
                }
                pipeline.sync();
                for (Map.Entry<String, Response<Long>> entry : responseMap.entrySet()) {
                    Long num = entry.getValue().get();
                    result.put(entry.getKey(), num);
                }
                return result;
            }
        }, keySet);
    }

    @Override
    public long setnx(String key, String value) {
        return executeCommand(new JedisCommandExecutor<Long>() {
            public Long execute(Jedis resource, Object... argArray) {
                return resource.setnx((String) argArray[0],
                        (String) argArray[1]);
            }
        }, key, value);
    }

    @Override
    public Map<String, Long> batchZadd(Map<String, Long> keyScoreMap,String member) {
        return executeCommand(new JedisCommandExecutor<Map<String, Long>>() {
            @Override
            public Map<String, Long> execute(Jedis resource, Object... argArray) {
                Map<String, Long> paramKeyScoreMap = (Map<String, Long>) argArray[0];
                String paramMember = (String) argArray[1];
                Pipeline pipeline = resource.pipelined();
                Map<String, Response<Long>> responseMap = new HashMap<String, Response<Long>>();
                for (Map.Entry<String, Long> entry : paramKeyScoreMap.entrySet()) {
                    String paramKey = entry.getKey();
                    Response<Long> response = pipeline.zadd(paramKey, entry.getValue(), paramMember);
                    responseMap.put(paramKey, response);
                }
                pipeline.sync();
                Map<String, Long> result = new HashMap<String, Long>();

                for (Map.Entry<String, Response<Long>> entry : responseMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().get());
                }
                log.info("=============batchZadd=======result:"+result+"=====value:"+paramMember+"==");
                return result;
            }
        }, keyScoreMap, member);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
                                                 double min, int offset, int count) {
        return executeCommand(new JedisCommandExecutor<Set<Tuple>>() {
            public Set<Tuple> execute(Jedis resource, Object... argArray) {
                return resource.zrevrangeByScoreWithScores(
                        (String) argArray[0], (Double) argArray[1],
                        (Double) argArray[2], (Integer) argArray[3],
                        (Integer) argArray[4]);
            }
        }, key, max, min, offset, count);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return executeCommand(new JedisCommandExecutor<Set<Tuple>>() {
            public Set<Tuple> execute(Jedis resource, Object... argArray) {
                return resource.zrangeByScoreWithScores((String) argArray[0],
                        (Double) argArray[1], (Double) argArray[2]);
            }
        }, key, min, max);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min,
                                              double max, int offset, int count) {
        return executeCommand(new JedisCommandExecutor<Set<Tuple>>() {
            public Set<Tuple> execute(Jedis resource, Object... argArray) {
                return resource.zrangeByScoreWithScores((String) argArray[0],
                        (Double) argArray[1], (Double) argArray[2],
                        (Integer) argArray[3], (Integer) argArray[4]);
            }
        }, key, min, max, offset, count);
    }

    public Set<String> smembers(String key) {
        return executeCommand(new JedisCommandExecutor<Set<String>>() {
            public Set<String> execute(Jedis resource, Object... argArray) {
                return resource.smembers((String) argArray[0]);
            }
        }, key);
    }

    /**
     * set添加成员
     */
    public long sadd(String key, String member) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.sadd((String) argArray[0],
                                (String) argArray[1]);
                    }
                }, key, member);
    }
    
    /**
     * 查询set成员个数
     */
    public long scard(String key) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.scard((String) argArray[0]);
                    }
                }, key);
    }
    

    /**
     * set删除成员
     * @param key
     * @param member
     * @return
     */
    public long srem(String key, String... member) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.srem((String) argArray[0],
                                (String[]) argArray[1]);
                    }
                }, key, member);
    }
    
    public long hincrBy(String key, String field, long value) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.hincrBy((String) argArray[0],
                                (String) argArray[1], (Long) argArray[2]);
                    }
                }, key, field, value);
    }

    public long expire(String key, int seconds) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.expire((String) argArray[0],
                                (Integer) argArray[1]);
                    }
                }, key, seconds);
    }

    
    
    @Override
    public Map<String, String> batchSetex(Map<String, String> map, int seconds) {
        return executeCommand(new JedisCommandExecutor<Map<String, String>>() {
            @Override
            public Map<String, String> execute(Jedis resource, Object... argArray) {
                Map<String, String> paramMap = (Map<String, String>) argArray[0];
                Integer expireSeconds = (Integer) argArray[1];
                Pipeline pipeline = resource.pipelined();
                Map<String, Response<String>> responseMap = new HashMap<String, Response<String>>();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    Response<String> response = pipeline.setex(entry.getKey(), expireSeconds, entry.getValue());
                    responseMap.put(entry.getKey() + "setex", response);
                }
                pipeline.sync();
                Map<String, String> result = new HashMap<String, String>();
                for (Map.Entry<String, Response<String>> entry : responseMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().get());
                }
                return result;
            }
        }, map, seconds);
    }

    @Override
    public Map<String, String> batchSet(Map<String, String> map) {
        return executeCommand(new JedisCommandExecutor<Map<String, String>>() {
            @Override
            public Map<String, String> execute(Jedis resource, Object... argArray) {
                Map<String, String> paramMap = (Map<String, String>) argArray[0];
                Pipeline pipeline = resource.pipelined();
                Map<String, Response<String>> responseMap = new HashMap<String, Response<String>>();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    Response<String> response = pipeline.set(entry.getKey(), entry.getValue());
                    responseMap.put(entry.getKey() + "set", response);
                }
                pipeline.sync();
                Map<String, String> result = new HashMap<String, String>();
                for (Map.Entry<String, Response<String>> entry : responseMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().get());
                }
                return result;
            }
        }, map);
    }

    @Override
    public Map<String, Long> batchZadd(Map<String, List<String>> keyMemberMap, final int maxCount, double score) {
        return executeCommand(new JedisCommandExecutor<Map<String, Long>>() {
            @Override
            public Map<String, Long> execute(Jedis resource, Object... argArray) {
                Map<String, List<String>> paramKeyMemberMap = (Map<String, List<String>>) argArray[0];
                Integer paramMaxCount = (Integer) argArray[1];
                Double paramScore = (Double) argArray[2];
                Pipeline pipeline = resource.pipelined();
                Map<String, Response<Long>> responseMap = new HashMap<String, Response<Long>>();
                for (Map.Entry<String, List<String>> entry : paramKeyMemberMap.entrySet()) {
                    String paramKey = entry.getKey();
                    List<String> paramMemberList = entry.getValue();
                    for (String id : paramMemberList) {
                        Response<Long> response = pipeline.zadd(paramKey, paramScore, id);
                        responseMap.put(id + "zadd", response);
                    }
                    if (paramMaxCount > 0) {
                        Response<Long> response = pipeline.zremrangeByRank(paramKey, 0, -paramMaxCount);
                        responseMap.put(paramKey + "zremrangeByRank", response);
                    }
                }
                pipeline.sync();
                Map<String, Long> result = new HashMap<String, Long>();
                for (Map.Entry<String, Response<Long>> entry : responseMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().get());
                }
                return result;
            }
        }, keyMemberMap, maxCount, score);
    }

    @Override
    public Set<String> batchZrangeMergedValue(Set<String> keySet, long start, long end) {
        return executeCommand(new JedisCommandExecutor<Set<String>>() {
            @Override
            public Set<String> execute(Jedis resource, Object... argArray) {
                Set<String> keySetParam = (Set<String>) argArray[0];
                Long startParam = (Long) argArray[1];
                Long endParam = (Long) argArray[2];
                Pipeline pipeline = resource.pipelined();
                List<Response<Set<String>>> responseList = new ArrayList<Response<Set<String>>>();
                for (String key : keySetParam) {
                    Response<Set<String>> response = pipeline.zrange(key, startParam, endParam);
                    responseList.add(response);
                }
                pipeline.sync();
                Set<String> result = new HashSet<String>();
                for (Response<Set<String>> response : responseList) {
                    result.addAll(response.get());
                }
                return result;
            }
        }, keySet, start, end);
    }

    @Override
    public Map<String, Set<String>> batchZrange(Set<String> keySet, long start, long end) {
        return executeCommand(new JedisCommandExecutor<Map<String, Set<String>>>() {
            @Override
            public Map<String, Set<String>> execute(Jedis resource, Object... argArray) {
                Set<String> keySetParam = (Set<String>) argArray[0];
                Long startParam = (Long) argArray[1];
                Long endParam = (Long) argArray[2];
                Pipeline pipeline = resource.pipelined();
                Map<String, Response<Set<String>>> responseMap = new HashMap<String, Response<Set<String>>>();
                for (String key : keySetParam) {
                    Response<Set<String>> response = pipeline.zrange(key, startParam, endParam);
                    responseMap.put(key, response);
                }
                pipeline.sync();
                Map<String, Set<String>> result = new HashMap<String, Set<String>>();
                for (Map.Entry<String, Response<Set<String>>> entry : responseMap.entrySet()) {
                    Set<String> valueSet = entry.getValue().get();
                    if (valueSet != null) {
                        result.put(entry.getKey(), valueSet);
                    }
                }
                return result;
            }
        }, keySet, start, end);
    }

    /**
     * 取得hash表的大小 ,时间复杂度O(1)
     */
    public long hlen(String key) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.hlen((String) argArray[0]);
                    }
                }, key);
    }

    @Override
    public Double zincrby(String key, double score, String member) {
        return executeCommand(
                new JedisCommandExecutor<Double>() {
                    public Double execute(Jedis resource,
                                          Object... argArray) {
                        return resource.zincrby((String) argArray[0], (Double) argArray[1], (String) argArray[2]);
                    }
                }, key, score, member);
    }

    @Override
    public List<Long> batchHset(Set<String> keySet, String field, String value) {
        return executeCommand(new JedisCommandExecutor<List<Long>>() {
            @Override
            public List<Long> execute(Jedis resource, Object... argArray) {
                Set<String> keySetParam = (Set<String>) argArray[0];
                String fieldParam = (String) argArray[1];
                String valueParam = (String) argArray[2];
                Pipeline pipeline = resource.pipelined();
                List<Response<Long>> responseList = new ArrayList<Response<Long>>();
                for (String key : keySetParam) {
                    Response<Long> response = pipeline.hset(key, fieldParam, valueParam);
                    responseList.add(response);
                }
                pipeline.sync();
                List<Long> result = new ArrayList<Long>();
                for (Response<Long> response : responseList) {
                    result.add(response.get());
                }
                return result;
            }
        }, keySet, field, value);
    }

    @Override
    public Map<String, String> batchHget(final Set<String> keySet, String field) {
        return executeCommand(new JedisCommandExecutor<Map<String, String>>() {
            @Override
            public Map<String, String> execute(Jedis resource, Object... argArray) {
                Set<String> keySetParam = (Set<String>) argArray[0];
                String fieldParam = (String) argArray[1];
                Pipeline pipeline = resource.pipelined();
                Map<String, String> result = new HashMap<String, String>();
                Map<String, Response<String>> responseMap = new HashMap<String, Response<String>>();
                for (String key : keySetParam) {
                    Response<String> response = pipeline.hget(key, fieldParam);
                    responseMap.put(key, response);
                }
                pipeline.sync();
                for (Map.Entry<String, Response<String>> entry : responseMap.entrySet()) {
                    String value = entry.getValue().get();
                    if (value != null) {
                        result.put(entry.getKey(), entry.getValue().get());
                    }
                }
                return result;
            }
        }, keySet, field);
    }

    @Override
    public Map<String, Long> batchZrem(Map<String, Set<String>> keyMemberSetMap) {
        return executeCommand(new JedisCommandExecutor<Map<String, Long>>() {
            @Override
            public Map<String, Long> execute(Jedis resource, Object... argArray) {
                Map<String, Set<String>> keyMemberSetMapParam = (Map<String, Set<String>>) argArray[0];
                Pipeline pipeline = resource.pipelined();
                Map<String, Response<Long>> responseMap = new HashMap<String, Response<Long>>();
                for (Map.Entry<String, Set<String>> entry : keyMemberSetMapParam.entrySet()) {
                    Response<Long> response = pipeline.zrem(entry.getKey(), entry.getValue().toArray(new String[0]));
                    responseMap.put(entry.getKey(), response);
                }
                pipeline.sync();
                Map<String, Long> result = new HashMap<String, Long>();
                for (Map.Entry<String, Response<Long>> entry : responseMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().get());
                }
                return result;
            }
        }, keyMemberSetMap);
    }

    @Override
    public Map<String, Long> batchZremKeyMemberPairs(Map<String, String> keyMemberMap) {
        return executeCommand(new JedisCommandExecutor<Map<String, Long>>() {
            @Override
            public Map<String, Long> execute(Jedis resource, Object... argArray) {
                Map<String, String> keyMemberMapParam = (Map<String, String>) argArray[0];
                Pipeline pipeline = resource.pipelined();
                Map<String, Response<Long>> responseMap = new HashMap<String, Response<Long>>();
                for (Map.Entry<String, String> entry : keyMemberMapParam.entrySet()) {
                    Response<Long> response = pipeline.zrem(entry.getKey(), entry.getValue());
                    responseMap.put(entry.getKey(), response);
                }
                pipeline.sync();
                Map<String, Long> result = new HashMap<String, Long>();
                for (Map.Entry<String, Response<Long>> entry : responseMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().get());
                }
                return result;
            }
        }, keyMemberMap);
    }

    @Override
    public Map<String, Long> batchHdel(Map<String, String> keyFieldMap) {
        return executeCommand(new JedisCommandExecutor<Map<String, Long>>() {
            @Override
            public Map<String, Long> execute(Jedis resource, Object... argArray) {
                Map<String, String> keyFieldMapParam = (Map<String, String>) argArray[0];
                Pipeline pipeline = resource.pipelined();
                Map<String, Response<Long>> responseMap = new HashMap<String, Response<Long>>();
                for (Map.Entry<String, String> entry : keyFieldMapParam.entrySet()) {
                    Response<Long> response = pipeline.hdel(entry.getKey(), entry.getValue());
                    responseMap.put(entry.getKey(), response);
                }
                pipeline.sync();
                Map<String, Long> result = new HashMap<String, Long>();
                for (Map.Entry<String, Response<Long>> entry : responseMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().get());
                }
                return result;
            }
        }, keyFieldMap);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return executeCommand(new JedisCommandExecutor<Map<String, String>>() {
            @Override
            public Map<String, String> execute(Jedis resource, Object... argArray) {
                return resource.hgetAll((String) argArray[0]);
            }
        }, key);
    }

    @Override
    public Map<String, Double> batchZincrby(String key, Map<String, Integer> valueOffsetMap) {
        return executeCommand(new JedisCommandExecutor<Map<String, Double>>() {
            @Override
            public Map<String, Double> execute(Jedis resource, Object... argArray) {
                String keyParam = (String) argArray[0];
                Map<String, Integer> valueOffsetMapParam = (Map<String, Integer>) argArray[1];
                Pipeline pipeline = resource.pipelined();
                Map<String, Response<Double>> responseMap = new HashMap<String, Response<Double>>();
                for (Map.Entry<String, Integer> entry : valueOffsetMapParam.entrySet()) {
                    Response<Double> response = pipeline.zincrby(keyParam, entry.getValue(), entry.getKey());
                    responseMap.put(entry.getKey(), response);
                }
                pipeline.sync();
                Map<String, Double> result = new HashMap<String, Double>();
                for (Map.Entry<String, Response<Double>> entry : responseMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().get());
                }
                return result;
            }
        }, key, valueOffsetMap);
    }

    @Override
    public Map<String, Double> batchZincrby(Map<String, String> keyValueMap, int offset) {
        return executeCommand(new JedisCommandExecutor<Map<String, Double>>() {
            @Override
            public Map<String, Double> execute(Jedis resource, Object... argArray) {
                Map<String, String> keyValueMapParam = (Map<String, String>) argArray[0];
                Integer offsetParam = (Integer) argArray[1];
                Pipeline pipeline = resource.pipelined();
                Map<String, Response<Double>> responseMap = new HashMap<String, Response<Double>>();
                for (Map.Entry<String, String> entry : keyValueMapParam.entrySet()) {
                    Response<Double> response = pipeline.zincrby(entry.getKey(), offsetParam, entry.getValue());
                    responseMap.put(entry.getKey(), response);
                }
                pipeline.sync();
                Map<String, Double> result = new HashMap<String, Double>();
                for (Map.Entry<String, Response<Double>> entry : responseMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().get());
                }
                return result;
            }
        }, keyValueMap, offset);
    }

    @Override
    public String hget(String key, String field) {
        return executeCommand(new JedisCommandExecutor<String>() {
            public String execute(Jedis resource, Object... argArray) {
                return resource.hget((String) argArray[0], (String) argArray[1]);
            }
        }, key, field);
    }

    /**
     * 删除hash表的中某项 ,时间复杂度O(1)
     */
    public long hdel(String key, String... fields) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.hdel((String) argArray[0],
                                (String[]) argArray[1]);
                    }
                }, key, fields);
    }

    /**
     * 设置hash表的中某项 ,时间复杂度O(N)
     */
    public long hset(String key, String field, String value) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.hset((String) argArray[0],
                                (String) argArray[1], (String) argArray[2]);
                    }
                }, key, field, value);
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        return executeCommand(new JedisCommandExecutor<List<String>>() {
            @Override
            public List<String> execute(Jedis jedis, Object... argArray) {
                return jedis.hmget((String) argArray[0], (String[]) argArray[1]);
            }
        }, key, fields);
    }

    @Override
    public long zcard(String key) {
        return executeCommandForReturnLong(
                new JedisCommandExecutor<Long>() {
                    public Long execute(Jedis resource,
                                        Object... argArray) {
                        return resource.zcard((String) argArray[0]);
                    }
                }, key);
    }

    public long zadd(String key, Map<String, Double> members) {
        long reply = executeCommandForReturnLong(new JedisCommandExecutor<Long>() {
            public Long execute(Jedis resource, Object... argArray) {
                return resource.zadd((String) argArray[0], (Map<String, Double>) argArray[1]);
            }
        }, key, members);
        return reply;
    }

    @Override
    public Boolean hexists(String key, String field) {
        Boolean reply = executeCommand(new JedisCommandExecutor<Boolean>() {
            public Boolean execute(Jedis resource, Object... argArray) {
                return resource.hexists((String) argArray[0], (String) argArray[1]);
            }
        }, key, field);
        return reply;
    }

    @Override
    public Set<String> hkeys(String key) {
        Set<String> reply = executeCommand(new JedisCommandExecutor<Set<String>>() {
            public Set<String> execute(Jedis resource, Object... argArray) {
                return resource.hkeys((String) argArray[0]);
            }
        }, key);
        return reply;
    }

    public Set<String> zrangeByScore(String key, Double min, Double max) {
        Set<String> reply = executeCommand(new JedisCommandExecutor<Set<String>>() {
            public Set<String> execute(Jedis resource, Object... argArray) {
                return resource.zrangeByScore((String) argArray[0], (Double) argArray[1], (Double) argArray[2]);
            }
        }, key, min, max);
        return reply;
    }

	@Override
	public List<String> mget(String... keys) {
		List<String> reply = executeCommand(new JedisCommandExecutor<List<String>>() {
            public List<String> execute(Jedis resource, Object... argArray) {
            	Object arg = argArray[0];
                return resource.mget((String[]) argArray);
            }
        },keys);
        return reply;
	}

	@Override
	public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
		executeCommand(new JedisCommandExecutor() {
			@Override
			public Object execute(Jedis resource, Object... argArray) {
				JedisPubSub jedisPubSub = (JedisPubSub) argArray[0];
				resource.psubscribe(jedisPubSub, "__key*__:*");
				return null;
			}			
        },jedisPubSub,patterns);
	}

	@Override
	public long del(byte[]... keys) {
		Long result = executeCommand(new JedisCommandExecutor<Long>() {
			@Override
			public Long execute(Jedis resource, Object... argArray) {
				return resource.del(keys);
			}			
        },keys);
		
		return result;
	}

	@Override
	public List<byte[]> mget(byte[]... keys) {
		
		List<byte[]> result = executeCommand(new JedisCommandExecutor<List<byte[]>>() {
			@Override
			public List<byte[]> execute(Jedis resource, Object... argArray) {
				return resource.mget(keys);
			}			
        },keys);
		
		return result;
	}
}
