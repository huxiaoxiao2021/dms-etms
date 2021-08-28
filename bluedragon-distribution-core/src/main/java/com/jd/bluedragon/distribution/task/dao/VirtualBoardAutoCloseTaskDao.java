package com.jd.bluedragon.distribution.task.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.task.domain.Task;

public class VirtualBoardAutoCloseTaskDao extends BaseDao<Task> {

	public static final String namespace = VirtualBoardAutoCloseTaskDao.class.getName();

	/**
	 * 新增
	 *
	 * @param task
	 * @return
	 */
	public int add(Task task) {
		return this.getSqlSession().insert(VirtualBoardAutoCloseTaskDao.namespace + ".add", task);
	}
}
