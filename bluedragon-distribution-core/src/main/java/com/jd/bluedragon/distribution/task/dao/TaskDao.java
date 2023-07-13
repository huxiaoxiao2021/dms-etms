package com.jd.bluedragon.distribution.task.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskQuery;
import com.jd.jsf.gd.util.StringUtils;

public class TaskDao extends BaseDao<Task> {

	public static final String namespace = TaskDao.class.getName();

	@SuppressWarnings("unchecked")
	public List<Task> findTasks(Integer type) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		return super.getSqlSession().selectList(TaskDao.namespace + ".findTasks", request);
	}

	@SuppressWarnings("unchecked")
	public List<Task> findTasks(Integer type, String ownSign) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("ownSign", ownSign);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findTasks", request);
	}

	@SuppressWarnings("unchecked")
	public List<Task> findLimitedTasks(Integer fetchNum,List<String> queueIds,String ownSign) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("tableName", Task.getTaskWaybillTableName());
		request.put("fetchNum", fetchNum);
		request.put("queueIds",queueIds);
		request.put("ownSign", ownSign);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findLimitedTasks", request);
	}

	@SuppressWarnings("unchecked")
	public List<Task> findLimitedTasks(Integer type, Integer fetchNum, List<String> queueIds) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("fetchNum", fetchNum);
		request.put("queueIds", queueIds);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findLimitedTasksByType", request);
	}

	@SuppressWarnings("unchecked")
	public List<Task> findLimitedTasks(Integer type, Integer fetchNum, String ownSign ,List<String> queueIds) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("fetchNum", fetchNum);
		request.put("ownSign", ownSign);
		request.put("queueIds",queueIds);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findLimitedTasksByType", request);
	}


    @SuppressWarnings("unchecked")
    public List<Task> findLimitedTasksWithoutFailed(Integer type, Integer fetchNum, String ownSign,List<String> queueIds) {
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("type", type);
        request.put("tableName", Task.getTableName(type));
        request.put("fetchNum", fetchNum);
        request.put("ownSign", ownSign);
        request.put("queueIds",queueIds);
        return super.getSqlSession().selectList(TaskDao.namespace + ".findLimitedTasksWithoutFailedByType", request);
    }


	@SuppressWarnings("unchecked")
	public List<Task> findSpecifiedTasks(Integer type, Integer fetchNum, String ownSign ,List<String> queueIds) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("fetchNum", fetchNum);
		request.put("ownSign", ownSign);
		request.put("queueIds",queueIds);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findSpecifiedTasks", request);
	}

	@SuppressWarnings("unchecked")
	public List<Task> findTasksByFingerprint(Task task) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("fingerprint", task.getFingerprint());
		request.put("tableName", task.getTableName());
		if (null == task.getTableName()) {
			request.put("tableName", Task.getTaskWaybillTableName());
		}
		return super.getSqlSession().selectList(TaskDao.namespace + ".findTasksByFingerprint", request);
	}

	public int updateBySelective(Task task) {
		task.setTableName(Task.getTableName(task.getType()));
		return super.getSqlSession().update(TaskDao.namespace + ".updateBySelective", task);
	}
	public int updateBySelectiveWithBody(Task task) {
		task.setTableName(Task.getTableName(task.getType()));
		return super.getSqlSession().update(TaskDao.namespace + ".updateBySelectiveWithBody", task);
	}
	@SuppressWarnings("unchecked")
	public List<Task> findTasks(Task task) {
		return super.getSqlSession().selectList(TaskDao.namespace + ".findTasksStatusByBoxcode", task);
	}

	public Task findReverseSendTask(String sendCode) {
		return (Task) super.getSqlSession().selectOne(TaskDao.namespace + ".findReverseSendTask", sendCode);
	}

	public Task findWaybillSendTask(String sendCode) {
		return (Task) super.getSqlSession().selectOne(TaskDao.namespace + ".findWaybillSendTask", sendCode);
	}

	@SuppressWarnings("unchecked")
	public List<Task> findSendTasks(Integer type, Integer fetchNum, String key, List<String> queueIds,String ownSign, List<String> ownSigns) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("fetchNum", fetchNum);
		request.put("key", key);
		request.put("queueIds",queueIds);
		request.put("ownSign",ownSign);
		request.put("ownSigns",ownSigns);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findSendTasks", request);
	}


	public List<Task> findTasksUnderOptimizeSendTask(Integer type, Integer fetchNum, String key, List<String> queueIds,String ownSign, List<String> ownSigns,Integer executeCount) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("fetchNum", fetchNum);
		request.put("key", key);
		request.put("queueIds",queueIds);
		request.put("ownSign",ownSign);
		request.put("ownSigns",ownSigns);
		request.put("executeCount",executeCount);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findTasksUnderOptimizeSendTask", request);
	}
	/**
	 * 查询待处理失败的数据
	 * 
	 * @param type
	 * @param ownSign
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer findFailTasksNumsByType(Integer type, String ownSign,Integer keyword1) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("ownSign", ownSign);
		request.put("keyword1", keyword1);
		return (Integer) super.getSqlSession().selectOne(TaskDao.namespace + ".findFailTasksNumsByType", request);
	}

	/**
	 * 查询处理的数据
	 * 
	 * @param type
	 * @param ownSign
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer findTasksNumsByType(Integer type, String ownSign) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("ownSign", ownSign);
		return (Integer) super.getSqlSession().selectOne(TaskDao.namespace + ".findTasksNumsByType", request);
	}

	/**
	 * 查询待处理失败的数据
	 *
	 * @param type
	 * @param ownSign
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer findFailTasksNumsIgnoreType(Integer type, String ownSign) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("ownSign", ownSign);
		return (Integer) super.getSqlSession().selectOne(TaskDao.namespace + ".findFailTasksNumsIgnoreType", request);
	}

	/**
	 * 查询处理的数据
	 *
	 * @param type
	 * @param ownSign
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer findTasksNumsIgnoreType(Integer type, String ownSign) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("ownSign", ownSign);
		return (Integer) super.getSqlSession().selectOne(TaskDao.namespace + ".findTasksNumsIgnoreType", request);
	}


	/**
	 * 增时能带状态值,接收status, execute_count参数
	 * 
	 * @param task
	 * @return
	 */
	public Integer addWithStatus(Task task) {
		return (Integer) super.getSqlSession().insert(TaskDao.namespace + ".addWithStatus", task);
	}

    public Integer addWithParam(Task task) {
        return (Integer) super.getSqlSession().insert(TaskDao.namespace + ".addWithParam", task);
    }

	@SuppressWarnings("unchecked")
	public List<Task> findPageTask(Map<String, Object> params) {
		return super.getSqlSession().selectList(TaskDao.namespace + ".findPageTask", params);
	}

	public Integer findCountTask(Map<String, Object> params) {
		return (Integer) super.getSqlSession().selectOne(TaskDao.namespace + ".findCountTask", params);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> findTaskTypeByTableName(String tableName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tableName", tableName);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findTaskTypeByTableName", params);
	}

	public Integer updateTaskById(Map<String, Object> params) {
		return (Integer) super.getSqlSession().update(TaskDao.namespace + ".updateTaskById", params);
	}

	public Integer updateBatchTask(Map<String, Object> params) {
		return (Integer) super.getSqlSession().update(TaskDao.namespace + ".updateBatchTask", params);
	}
	
	/**
	 * xumei
	 * @param type
	 * @param fetchNum
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Task> findTaskTypeByStatus(Integer type, Integer fetchNum ,List<String> queueIds){
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("fetchNum", fetchNum);
		request.put("queueIds",queueIds);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findTaskTypeByStatus", request);
	}
	
	/**
	 * xumei
	 * @param params
	 * @return
	 */
	public Integer updateTaskStatus(Task task) {
		task.setTableName(Task.getTableName(task.getType()));
		return (Integer) super.getSqlSession().update(TaskDao.namespace + ".updateTaskStatus", task);
	}

	public List<Task> findDeliveryToFinanceConvertTasks(Integer type, Integer fetchNum,List<String> queueIds) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("fetchNum", fetchNum);
		request.put("queueIds", queueIds);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findDeliveryToFinanceConvertTasks", request);
	}

	public List<Task> findVirtualBoardTasks(Integer type, Integer fetchNum, String ownSign, List<String> queueIds, Integer lazyExecuteDays) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("fetchNum", fetchNum);
		request.put("ownSign", ownSign);
		request.put("queueIds", queueIds);
		request.put("lazyExecuteDays", lazyExecuteDays);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findVirtualBoardTasks", request);
	}

	/**
	 * 查找作业工作台自动关闭任务，一个封车编码只有一条
	 * @author fanggang7
	 * @time 2023-03-21 16:34:55 周二
	 */
	public List<Task> findJyBizAutoCloseTasks(Integer type, Integer fetchNum, List<String> ownSigns, List<String> queueIds){
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("fetchNum", fetchNum);
		request.put("ownSigns", ownSigns);
		request.put("queueIds",queueIds);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findJyBizAutoCloseTasks", request);
	}
	/**
	 * 根据条件查询最近的一条任务数据
	 * @param query
	 * @return
	 */
	public Task findLastTaskByQuery(TaskQuery query){
		if(StringUtils.isBlank(query.getTableName())) {
			return null;
		}
		if(StringUtils.isBlank(query.getKeyword1())) {
			return null;
		}
		return super.getSqlSession().selectOne(TaskDao.namespace + ".findLastTaskByQuery", query);
	}
	/**
	 * 查找任务列表
	 * @author fanggang7
	 * @time 2023-03-21 16:34:55 周二
	 */
	public List<Task> findListForDelayTask(Integer type, Integer fetchNum, String ownSign, List<String> queueIds){
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("fetchNum", fetchNum);
		request.put("ownSign", ownSign);
		request.put("queueIds",queueIds);
		return super.getSqlSession().selectList(TaskDao.namespace + ".findListForDelayTask", request);
	}	
}
