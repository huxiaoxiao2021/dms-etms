package com.jd.bluedragon.core.redis.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.redis.QueueKeyInfo;
import com.jd.bluedragon.core.redis.RedisTaskHelper;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.jim.cli.Cluster;
import com.jd.tbschedule.dto.ScheduleQueue;
import com.jd.tbschedule.redis.utils.JsonUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 * 
 * @ClassName: RedisManagerImpl
 * @Description: 只限于worker任务存储使用，该类引用了2个不同的Cluster容易造成调用混乱，数据存储推荐使用CacheService
 * @author: wuyoude
 * @date: 2018年5月4日 上午9:15:36
 *
 */
@Service("redisManager")
public class RedisManagerImpl implements RedisManager {

	private static final Logger log = LoggerFactory.getLogger(RedisManagerImpl.class);
	
	/**
	 * redis-worker专用
	 */
	@Autowired
	@Qualifier("redisClient")
	private Cluster redisClient;
	
	@Autowired
	@Qualifier("redisClientCache")
	private Cluster redisClientCache;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private RedisTaskHelper redisTaskHelper;
	
	public String get(String key) {
		return this.redisClient.get(key);
	}

	@JProfiler(jKey = "Bluedragon_dms_center.dms.method.redisManager.lpush", mState = {
			JProEnum.TP, JProEnum.FunctionError })
	public Long lpush(String key, String body) {
		return redisClient.lPush(key, body);
	}

	@JProfiler(jKey = "Bluedragon_dms_center.dms.method.redisManager.rpush", mState = {
			JProEnum.TP, JProEnum.FunctionError })
	public Long rpush(String key, String body) {
		return redisClient.rPush(key, body);
	}
	
	public Boolean hset(String name, String key, String value) {
		return redisClient.hSet(name, key, value);
	}

	public String hget(String name, String key) {
		return this.redisClient.hGet(name, key);
	}

	public Set<String> hkeys(String name) {
		return this.redisClient.hKeys(name);
	}

	public List<String> hvals(String name) {
		return this.redisClient.hVals(name);
	}

	public Map<String, String> hgetall(String name) {
		return this.redisClient.hGetAll(name);
	}

	@JProfiler(jKey = "erp.RedisClientProxy.llen", mState = { JProEnum.TP,
			JProEnum.Heartbeat, JProEnum.FunctionError })
	public long llen(String key) throws Exception{
		if(key==null || key.length() < 1){
			log.warn("llen: parameter is empty,return 0!");
			return 0;
		}
		Long count = redisClient.lLen(key);
		return count==null ? 0 : count.longValue();
	}
	
	@JProfiler(jKey = "erp.RedisClientProxy.lrange", mState = { JProEnum.TP,
			JProEnum.Heartbeat, JProEnum.FunctionError })
	public List<String> lrange(String key, long start, long end) throws Exception{
		if(key==null || key.length() < 1){
			log.warn("llen: parameter is empty,return 0!");
			return null;
		}
		List<String> list = redisClient.lRange(key, start, end);
		return list;
	}
	
	@JProfiler(jKey = "erp.RedisClientProxy.lpop", mState = { JProEnum.TP,
			JProEnum.Heartbeat, JProEnum.FunctionError })
	public String lpop(String key) throws Exception{
		if(key==null || key.length() < 1){
			log.warn("lpop: parameter is empty,return null!");
			return null;
		}
		String result = redisClient.lPop(key);
		return result;
	}
	
	/**
	 * 查询Redis队列大小
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public Map<String,Long> queryRedisQueueCount(List<String> list) throws Exception {
		if(list == null || list.size() < 1){
			return null;
		}
        CallerInfo info = Profiler.registerInfo("erp.RedisClientProxy.queryRedisQueueCount", Constants.UMP_APP_NAME_DMSWEB,false, true);
		Map<String,Long> result = new HashMap<String,Long>();
		try {
			log.info("queryRedisQueueCount: {}" , list);
			long count = 0;
			for (String item : list) {
				if(item == null || item.length() < 1){
					continue;
				}
				count = this.llen(item);
				result.put(item, count);
			}
		} catch (Exception e) {
			Profiler.functionError(info);
			log.error("queryRedisQueueCount-error", e);
		}finally {
			Profiler.registerInfoEnd(info);
		}
		return result;
	}
	
	/**
	 * 将数据从redis移动到数据库里。
	 * @param queueKeyInfo
	 * @return 对应的队列或者null
	 * @throws Exception 
	 */
	public Long moveTaskToDB(QueueKeyInfo queueKeyInfo) throws Exception {
		Long results = 0l;
		
		Map<String, ScheduleQueue> queueMap = redisTaskHelper.getQueueMap(queueKeyInfo);
		if(queueMap==null||queueMap.size()==0) //如果无队列则直接返回空
			return results;
		
		Collection<ScheduleQueue> scedule = queueMap.values();
		
		//将类型对应每个队列中的任务存到数据库中
		for(ScheduleQueue sQueue : scedule){
			Long len = llen(sQueue.getCacheKey());
			for(long i=0;i<len;i++){
				String taskStr = lpop(sQueue.getCacheKey());
				Task task = JsonUtil.jsonToBean(taskStr, Task.class);
				taskService.add(task);
			}
			results+=len;
		}
		return results;
	}

	/**
	 * 写入key
	 * @param 
	 * @return
	 * 
	 */
	@Deprecated
	public void setex(String key, int timeout ,String body) {
		this.redisClientCache.setEx(key,body,timeout, TimeUnit.SECONDS);
	}
	
	/**
	 * 获取KEY
	 * @param 
	 * @return
	 * 
	 */
	@Deprecated
	public String getCache(String key) {
		return this.redisClientCache.get(key);
	}

	/**
	 * 删除key
	 * @param 
	 * @return
	 * 
	 */
	@Deprecated
	public Long del(String key) {
		// TODO Auto-generated method stub
		return redisClientCache.del(key);
	}

	/**
	 * 判断key是否存在
	 * @param key key
	 * @return
	 * */
	@Deprecated
	public Boolean exists(String key){
		return redisClientCache.exists(key);
	}


	/**
	 * 设置指定key的生命周期
	 * @param key key
	 * @param seconds expire seconds
	 * */
	@Deprecated
	public Boolean expire(String key, Integer seconds){
		return redisClientCache.expire(key,seconds,TimeUnit.SECONDS);
	}

	@JProfiler(jKey = "Bluedragon_dms_center.dms.method.redisManager.lpushCache", mState = {
			JProEnum.TP, JProEnum.FunctionError })
	@Override
	@Deprecated
	public Long lpushCache(String key, String body) {
		return redisClientCache.lPush(key, body);
	}

	@JProfiler(jKey = "Bluedragon_dms_center.dms.method.redisManager.lrangeCache", mState = {
			JProEnum.TP, JProEnum.FunctionError })
	@Override
	@Deprecated
	public List<String> lrangeCache(String key, long start, long end) throws Exception {
		if(key==null || key.length() < 1){
			log.warn("lrangeCache: parameter is empty,return 0!");
			return null;
		}
		List<String> list = redisClientCache.lRange(key, start, end);
		return list;
	}
}
