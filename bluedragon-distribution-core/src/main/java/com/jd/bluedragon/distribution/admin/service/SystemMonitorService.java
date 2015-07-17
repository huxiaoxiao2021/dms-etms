package com.jd.bluedragon.distribution.admin.service;

import java.util.List;
import java.util.Map;

/**
 * 系统监控业务逻辑
 * @author suihonghua
 *
 */
public interface SystemMonitorService {
	
	/**
	 * 查询Redis队列数据总数(UMP)
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> queryRedisQueueUMP(Map<String,Object> param) throws Exception ;
	
	/**
	 * 查询Redis队列数据个数(页面)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String,Long> queryRedisQueueCount(Map<String,Object> param) throws Exception;
	
	/**
	 * 查询Redis队列数据个数
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public Map<String,Long> queryRedisQueueCount(List<String> list) throws Exception ;
	
	/**
	 * 查询Redis队列数据明细
	 * @param queueKey
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public List<String> queryRedisQueueDetail(String queueKey, int currentPage, int pageSize) throws Exception;
	
}
