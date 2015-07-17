package com.jd.bluedragon.core.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;

import com.jd.cachecloud.driver.jedis.ShardedXCommands;
import com.jd.tbschedule.dto.ScheduleQueue;
import com.jd.tbschedule.redis.CacheEntry;
import com.jd.tbschedule.redis.QueueCacheEntry;

/**
 * 公共数据类
 * 
 * @author huangliang
 * 
 */
public class RedisTaskHelper {
	
	private static Log log = LogFactory.getLog(RedisTaskHelper.class);
	private ShardedXCommands redisClient;
	private String queuePrefix;//任务列表key前缀，用于如：WORKER_QUEUE_JSONSealBoxRedisTask， WORKER_QUEUE_JSON就是任务列表key前缀
	private static ConcurrentHashMap<String, Map<String, ScheduleQueue>> taskListMap = new ConcurrentHashMap<String, Map<String, ScheduleQueue>>();//用于记录所有的任务列表与任务类型的对应关系

	private static Integer queueNum ;
	
	/**
	 * 验证task自身计算出的队列key在redis中存不存在，或者能否被worker初始化出来
	 * @param queueKeyInfo
	 * @return
	 */
	public boolean validateQueueKey(QueueKeyInfo queueKeyInfo){
		boolean result = false;
		if(redisClient==null){
			log.error("RedisTaskHelper.redisClient 为空, 请检查redis服务器是否存活、系统启动有无异常!");
			return result;//如果redis客户端注入不成功，则直接返回false
		}
		
		if(queueKeyInfo!=null){ //校验参数
			Map<String, ScheduleQueue> queueMap = getQueueMap(queueKeyInfo);
			
			//如果queueKeyInfo对应的下标在队列的范围内，则比较cacheKey是否一致
			if(queueKeyInfo.getQueueId()>=0&&queueMap!=null&&queueKeyInfo.getQueueId()<queueMap.size()){
				ScheduleQueue queue = queueMap.get(queueKeyInfo.getQueueKey());
				if(queue!=null){
					result = true;
				}
			}
			
		}
		
		return result;
	}

	public ShardedXCommands getRedisClient() {
		return redisClient;
	}

	public void setRedisClient(ShardedXCommands redisClient) {
		this.redisClient = redisClient;
	}

	public String getQueuePrefix() {
		return queuePrefix;
	}

	public void setQueuePrefix(String queuePrefix) {
		this.queuePrefix = queuePrefix;
	}

	/**
	 * 根据队列键信息, 查找对应的队列。
	 * @param queueKeyInfo
	 * @return 对应的队列或者null
	 */
	@Profiled(tag = "RedisTaskHelper.getQueueMap")
	public Map<String, ScheduleQueue> getQueueMap(QueueKeyInfo queueKeyInfo) {
		if(redisClient==null){
			log.error("RedisTaskHelper.redisClient 为空, 请检查redis服务器是否存活、系统启动有无异常!");
			return null;//如果redis客户端注入不成功，则直接返回null
		}
		
		if(queueKeyInfo==null) return null;//校验参数
			
		//如对应任务类型的队列不为空，则直接返回列表
		Map<String, ScheduleQueue> queueMap = taskListMap.get(queueKeyInfo.getTaskType()+"$"+queueKeyInfo.getOwnSign());
		if(queueMap!=null)
			return queueMap;
		
		try {
			CacheEntry<List<ScheduleQueue>> queueCacheEntry;
			//1.根据基础任务类型键查得对应的所有任务
			queueCacheEntry = new QueueCacheEntry(redisClient, queueKeyInfo.getTaskType(), queueKeyInfo.getOwnSign());
			List<ScheduleQueue> queueList = queueCacheEntry.getValue();
			
			if(queueList.size()>0){
				//将所有的任务放到map中，以队列Key为键值
				queueMap = new HashMap<String, ScheduleQueue>();
				for(ScheduleQueue queue : queueList){
					queueMap.put(queue.getCacheKey(), queue);
				}
				
				//2.相同基础任务类型的放入一个entry中
				taskListMap.put(queueKeyInfo.getTaskType()+"$"+queueKeyInfo.getOwnSign(), queueMap);
			}
			
		} catch (Exception e) {
			log.error("initQueueList:",e);
		}
		return queueMap;
	}

	public static Integer getQueueNum() {
		return queueNum;
	}

	public static void setQueueNum(Integer queueNum) {
		RedisTaskHelper.queueNum = queueNum;
	}
}
