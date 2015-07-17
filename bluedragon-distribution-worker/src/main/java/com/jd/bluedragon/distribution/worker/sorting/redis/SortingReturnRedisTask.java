package com.jd.bluedragon.distribution.worker.sorting.redis;


import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.sorting.service.SortingReturnService;
import com.jd.bluedragon.distribution.task.domain.Task;


public class SortingReturnRedisTask extends RedisSingleScheduler {

	@Autowired
	private SortingReturnService sortingReturnService;

	@Override
	public boolean executeSingleTask(Task task, String ownSign) throws Exception {
		this.sortingReturnService.doSortingReturn(task);
		return true;
	}

}
