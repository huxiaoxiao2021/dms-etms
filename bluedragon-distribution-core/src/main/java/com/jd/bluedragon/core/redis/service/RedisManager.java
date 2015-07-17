package com.jd.bluedragon.core.redis.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jd.bluedragon.core.redis.QueueKeyInfo;

public interface RedisManager {

	/**
	 * 获取string
	 * @param 
	 * @return
	 * 
	 */
	public String get(String key);
	
	Long lpush(String key, String body);

	public Long rpush(String key, String body);
	
	public long llen(String key) throws Exception;
	
	/**
	 * 写入string
	 * @param 
	 * @return
	 * 
	 */
	public String setex(String key,int timeout, String body);
	
	/**
	 * 删除key
	 * @param 
	 * @return
	 * 
	 */
	public Long del(String key);
	
	public String getCache(String key);
	
	Long hset(String name, String key, String value);

	String hget(String name, String key);

	Set<String> hkeys(String name);

	List<String> hvals(String name);

	public Map<String, String> hgetall(String name);

	public String lpop(String key) throws Exception;
	
	public List<String> lrange(String key, long start, long end) throws Exception;
	
	/**
	 * 查询Redis队列大小
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public Map<String,Long> queryRedisQueueCount(List<String> list) throws Exception;
	
	/**
	 * 将数据从redis移动到数据库里。
	 * @param queueKeyInfo
	 * @return 对应的队列或者null
	 * @throws Exception 
	 */
	public Long moveTaskToDB(QueueKeyInfo queueKeyInfo) throws Exception;

	/**
	 * 判断key是否存在
	 * @param key key
	 * @return
	 * */
	public Boolean exists(String key);

	/**
	 * 设置指定key的生命周期
	 * @param key key
	 * @param seconds expire seconds
	 * */
	public Long expire(String key, Integer seconds);

	/**
	 * 向缓存redis集群列表增加
	 * @param key
	 * @param body
	 * */
	public Long lpushCache(String key, String body);


	/**
	 * 获取缓存redis集群的列表
	 * @param key
	 * @param start
	 * @param end
	 * */
	public List<String> lrangeCache(String key, long start, long end) throws Exception;
}
