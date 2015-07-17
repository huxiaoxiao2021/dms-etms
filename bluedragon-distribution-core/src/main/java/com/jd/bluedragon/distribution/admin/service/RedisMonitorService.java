package com.jd.bluedragon.distribution.admin.service;

/**
 * 系统Redis缓存管理
 * @author yangbo7
 *
 */
public interface RedisMonitorService {

	/**
	 * 根据key值,获取value
	 * @param key
	 * @return
	 */
	String getValueByKey(String key);

	/**
	 * 删除key
	 * @param key
	 */
	void deleteByKey(String key);
	
	/**
	 * 根据key值,获取返回值的类型
	 * @param key
	 * @return
	 */
	String getTypeByKey(String key);
	
}
