package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.distribution.sorting.domain.SortingReturn;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface SortingZhiPeiService {


	/**
	 * 根据分拣任务数据生成分拣退货操作信息
	 *
	 * @param task
	 */
	void doSorting(Task task) throws Exception;

	
}
