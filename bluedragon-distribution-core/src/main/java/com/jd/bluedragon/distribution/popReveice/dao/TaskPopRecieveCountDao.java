package com.jd.bluedragon.distribution.popReveice.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.popReveice.domain.TaskPopRecieveCount;
import com.jd.bluedragon.distribution.task.domain.Task;

public class TaskPopRecieveCountDao extends BaseDao<TaskPopRecieveCount> {

	public static final String namespace = TaskPopRecieveCountDao.class.getName();

	public int insert(TaskPopRecieveCount taskPopRecieveCount) {
		int m = this.getSqlSession().insert(TaskPopRecieveCountDao.namespace + ".insert",
		        taskPopRecieveCount);
		return m;
	}

	public int update(TaskPopRecieveCount taskPopRecieveCount) {
		int m = this.getSqlSession().update(TaskPopRecieveCountDao.namespace + ".update",
		        taskPopRecieveCount);
		return m;
	}

	public TaskPopRecieveCount getTaskPopRevieveCountById(Long taskId) {
		TaskPopRecieveCount obj = (TaskPopRecieveCount) this.getSqlSession().selectOne(
		        TaskPopRecieveCountDao.namespace + ".getTaskPopRevieveCountById", taskId);
		return obj;
	}

	@SuppressWarnings("unchecked")
	public List<TaskPopRecieveCount> getTaskPopRevieveCountByWaybillCode(String waybillCode) {
		List<TaskPopRecieveCount> dataList = this.getSqlSession().selectList(
		        TaskPopRecieveCountDao.namespace + ".getTaskPopRevieveCountByWaybillCode",
		        waybillCode);
		return dataList;

	}

	@SuppressWarnings("unchecked")
	public List<TaskPopRecieveCount> findLimitedTasks(Integer type, Integer fetchNum, String ownSign ,List<String> queueIds) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("type", type);
		request.put("tableName", Task.getTableName(type));
		request.put("fetchNum", fetchNum);
		request.put("ownSign", ownSign);
		request.put("queueIds",queueIds);
		return super.getSqlSession().selectList(
		        TaskPopRecieveCountDao.namespace + ".findLimitedTasksByType", request);
	}

}
