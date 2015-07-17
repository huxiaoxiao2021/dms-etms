package com.jd.bluedragon.distribution.admin.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.api.request.WorkerRequest;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface WorkerMonitorService {

	/**
	 * 分页查询任务
	 * 
	 * @param request
	 * @return
	 */
	List<Task> findPageTask(Map<String, Object> params);

	/**
	 * 查询任务总数
	 * 
	 * @param request
	 * @return
	 */
	Integer findCountTask(Map<String, Object> params);

	/**
	 * 根据表名查询任务类型
	 * 
	 * @param tableName
	 * @return
	 */
	List<Integer> findTaskTypeByTableName(String tableName);

	/**
	 * 根据ID重置Task状态,将task_status,execute_count置为0
	 * 
	 * @param params
	 * @return
	 */
	Integer updateTaskById(Map<String, Object> params);

	/**
	 * 批量重置Task状态
	 * 
	 * @param params
	 * @return
	 */
	Integer updateBatchTask(Map<String, Object> params);

}
