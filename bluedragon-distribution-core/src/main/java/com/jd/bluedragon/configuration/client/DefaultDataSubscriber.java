package com.jd.bluedragon.configuration.client;

import java.util.List;
import java.util.Map;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.utils.SimpleCache;
import com.jd.bluedragon.utils.SpringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDataSubscriber implements DataSubscriber {
	private static Logger log = LoggerFactory.getLogger(DefaultDataSubscriber.class);
	private final SimpleCache<String> cache = new SimpleCache<String>();
	/**
	 * 配置集合缓存Map在Redis对应的队列名
	 */
	public static final String CONFIGURATION_REDIS_QUEUE_NAME = "CONFIGURATION_REDIS_QUEUE_NAME";

	/**
	 * 1、先从本地缓存取数据，再往redis取数据,然后再从数据库取数据
	 */
	@Override
	public String getConfigure(String key) {
		String value = cache.get(key);
		log.info("配置管理从本地缓存获取值为{}:{}" ,key, value);
		if (value == null) {
			value = getKeyFromRedis(key);
			log.error("配置管理从redis获取值为{}:{}" ,key, value);
			if (value != null) {
				cache.put(key, value);
			}
		}

		if (value == null) {

		}
		return value;
	}

	/**
	 * 只从缓存中拿缓存,保证数据的实时性
	 */
	@Override
	public String getAvailableConfigure(String key) {
		String value = null;
		value = getKeyFromRedis(key);
		return value;
	}

	@Override
	public void init() {
		RedisManager redisManager = (RedisManager) SpringHelper
				.getBean("redisManager");
		if (redisManager != null) {
			Map<String, String> redisMap = redisManager
					.hgetall(this.CONFIGURATION_REDIS_QUEUE_NAME);
			if (redisMap != null && (!redisMap.isEmpty())) {
				for (Map.Entry<String, String> entry : redisMap.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					cache.put(key, value);
				}
				return;

			}
		}
		log.debug("初始化系统配置未从redis中拿到缓存...");
		BaseService baseService = getBaseService();
		if (baseService == null) {
			throw new RuntimeException("Spring 上下文未加载baseService对象...");
		}
		List<SysConfig> configList = baseService.queryConfigByKey("a");
		if (configList != null && (!configList.isEmpty())) {
			for (SysConfig config : configList) {
				String key = config.getConfigName();
				String value = config.getConfigContent();
				cache.put(key, value);

			}

		} else {
			log.error("数据库未配置数据...");
		}

	}

	private String getKeyFromRedis(String key) {
		RedisManager redisManager = getResiManager();
		String value = null;
		try {
			value = redisManager.hget(this.CONFIGURATION_REDIS_QUEUE_NAME, key);
		} catch (Exception e) {
			log.error("开关读取redis异常...", e);
			e.printStackTrace();
		}

		return value;
	}

	private RedisManager getResiManager() {
		RedisManager redisManager = (RedisManager) SpringHelper
				.getBean("redisManager");
		return redisManager;
	}

	private BaseService getBaseService() {
		BaseService baseService = (BaseService) SpringHelper
				.getBean("baseService");
		return baseService;
	}

}
