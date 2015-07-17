package com.jd.bluedragon.distribution.admin.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.admin.service.RedisMonitorService;
import com.jd.cachecloud.driver.jedis.ShardedXCommands;

@Service("redisManagementService")
public class RedisMonitorServiceImpl implements RedisMonitorService {

	private static final Logger logger = Logger.getLogger(RedisMonitorServiceImpl.class);

	@Autowired
	private RedisManager redisManager;

	@Autowired
	@Qualifier("redisClient")
	private ShardedXCommands redisClient;

	@Override
	public String getValueByKey(String key) {
		logger.info("根据key:" + key + ", 获取redis中的value");
		return redisManager.get(key);
	}

	@Override
	public void deleteByKey(String key) {
		logger.info("根据key:" + key + ", 删除redis中的value");
		redisManager.del(key);
	}

	@Override
	public String getTypeByKey(String key) {
		logger.info("根据key:" + key + ", 获取redis中value的type");
		return redisClient.type(key);
	}

}
