package com.jd.bluedragon.distribution.admin.service.impl;

import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.admin.service.RedisMonitorService;
import com.jd.jim.cli.Cluster;
import com.jd.jim.cli.driver.types.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("redisManagementService")
public class RedisMonitorServiceImpl implements RedisMonitorService {

	private static final Logger log = LoggerFactory.getLogger(RedisMonitorServiceImpl.class);

	@Autowired
	private RedisManager redisManager;

	@Autowired
	@Qualifier("redisClient")
	private Cluster redisClient;

	@Override
	public String getValueByKey(String key) {
		log.info("根据key:{}, 获取redis中的value",key);
		return redisManager.getCache(key);
	}

	@Override
	public void deleteByKey(String key) {
		log.info("根据key:{}, 删除redis中的value",key);
		redisManager.del(key);
	}

	@Override
	public DataType getTypeByKey(String key) {
		log.info("根据key:{}, 获取redis中value的type",key);
		return redisClient.type(key);
	}

}
