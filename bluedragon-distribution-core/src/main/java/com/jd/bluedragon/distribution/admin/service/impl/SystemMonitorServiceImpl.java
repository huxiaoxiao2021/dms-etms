package com.jd.bluedragon.distribution.admin.service.impl;

import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.admin.service.SystemMonitorService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 系统监控业务逻辑
 *
 */
@Service("systemMonitorService")
public class SystemMonitorServiceImpl implements SystemMonitorService {
	
	private static final Logger log = LoggerFactory.getLogger(SystemMonitorServiceImpl.class);
	
	@Autowired
	private RedisManager redisManager;

	public Map<String,Object> queryRedisQueueUMP(Map<String,Object> param) throws Exception {
		if(param == null || param.size() < 1){
			log.warn("queryRedisQueueUMP: map == null || map.size() < 1, return...");
			return param;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		long bCount = 0;//数据队列条数
		try {
			String bKey = (String)param.get("bKey");
			String qName = (String)param.get("qName");
			String qSign = (String)param.get("qSign");
			String qSize = (String)param.get("qSize");
			
			String queueName = qName;
			if(queueName == null || queueName.length() == 0){
				//如果没有配置qName参数，则提取bKey中最后一个"."后面的后缀作为队列名称
				queueName = StringHelper.getFileNameSuffix(bKey);
			}
			if(queueName == null || queueName.length() == 0){
				log.warn("queryRedisQueueUMP: queueName == null || queueName.length() == 0, return...");
				return map;
			}
			
			List<String> queue_list = this.buildQueueNameList(queueName, qSign , qSize);
			if(queue_list == null || queue_list.isEmpty()){
				log.warn("queryRedisQueueUMP: queue_list == null || queue_list.isEmpty(), return...");
				return map;
			}
			Map<String,Long> count_map = this.queryRedisQueueCount(queue_list);
			if(count_map == null || count_map.isEmpty()){
				log.warn("queryRedisQueueUMP: count_map == null || count_map.isEmpty(), return...");
				return map;
			}
			
			Set<Map.Entry<String, Long>> key_set = count_map.entrySet();
			for (Map.Entry<String, Long> key : key_set) {
			
				if (key == null || key.getKey() == null) {
					continue;
				}
				bCount += key.getValue().longValue();
			}
			
			String bTime = DateHelper.formatDate(new Date(), "yyyyMMddHHmmssSSS");
			
			map.put("bKey", bKey);
			map.put("bTime", bTime);
			map.put("bCount", bCount);
		} catch (Exception e) {
			log.error("queryRedisQueueUMP-error", e);
		}
		return map;
	}
	
	public Map<String,Long> queryRedisQueueCount(Map<String,Object> param) throws Exception {
		if(param == null || param.size() < 1){
			log.warn("queryRedisQueueCount: parameter cannot be empty, return...");
			return null;
		}
		try {
			String qName = (String)param.get("qName");
			String qSign = (String)param.get("qSign");
			String qSize = (String)param.get("qSize");
			
			if(qName == null || qName.length() == 0){
				log.warn("queryRedisQueueCount: qName == null || qName.length() == 0, return...");
				return null;
			}
			
			List<String> queue_list = this.buildQueueNameList(qName, qSign , qSize);
			if(queue_list == null || queue_list.isEmpty()){
				log.warn("queryRedisQueueCount: queue_list == null || queue_list.isEmpty(), return...");
				return null;
			}
			Map<String,Long> count_map = this.queryRedisQueueCount(queue_list);
			return count_map;
		} catch (Exception e) {
			log.error("queryRedisQueueCount-error", e);
			return null;
		}
	}
	
	/**
	 * 构建队列集合
	 * @author bjhuangliang
	 * @param queueName		队列名称（必填）
	 * @param queueSign		ownsign（选填）
	 * @param queueSize		队列大小（选填）
	 * @return
	 * @throws Exception
	 */
	private List<String> buildQueueNameList(String queueName,String queueSign,String queueSize)throws Exception {
		List<String> list = new ArrayList<String>();
		try {
			String[] queueName_arr = queueName.split(",");
			String[] queueSign_arr = null;
			if(queueSign!=null && queueSign.length()>0){
				queueSign_arr = queueSign.split(",");
			}
			
			int queue_size = 0;
			if(queueSize!=null && queueSize.length()>0){
				queue_size = Integer.parseInt(queueSize);
			}

			if(queueSign_arr!= null && queueSign_arr.length > 0){
				for (String qSign : queueSign_arr) {
					if(qSign == null || qSign.length() < 1){
						continue;
					}
					list.addAll(this.buildQueueNameList(queueName_arr, "$"+qSign, queue_size));
				}
			}else{
				list.addAll(this.buildQueueNameList(queueName_arr, null, queue_size));
			}
		} catch (Exception e) {
			log.error("buildQueueNameList-error", e);
		}
		return list;
	}
	
	private List<String> buildQueueNameList(String[] queueName_arr,String queueSign,int queueSize)throws Exception {
		List<String> list = new ArrayList<String>();
		try {
			for (String queue : queueName_arr) {
				if(queue == null || queue.length()<1){
					continue;
				}
				if(queueSize > 0){
					for (int i = 0; i < queueSize; i++) {
						list.add(this.buildQueueName(queue, queueSign, String.valueOf(i)));
					}
				} else {
					list.add(this.buildQueueName(queue, queueSign, ""));
				}
			}
		} catch (Exception e) {
			log.error("buildQueueNameList-error", e);
		}
		return list;
	}
	
	private String buildQueueName(String queueName,String queueSign,String queueNo)throws Exception {
		if(queueSign == null || queueSign.length() < 1){
			return (queueName + queueNo);
		} else {
			return (queueName + queueSign + queueNo);
		}
	}
	
	public Map<String,Long> queryRedisQueueCount(List<String> list) throws Exception {
		if(list == null || list.size() < 1){
			return null;
		}
		return redisManager.queryRedisQueueCount(list);
	}
	
	public List<String> queryRedisQueueDetail(String queueKey, int currentPage, int pageSize) throws Exception{
		int begin = (currentPage - 1) * pageSize;
		begin = begin < 0 ? 0 : begin;
		int end = currentPage * pageSize;
		List<String> list = redisManager.lrange(queueKey, begin, end);
		return list;
	}

}
