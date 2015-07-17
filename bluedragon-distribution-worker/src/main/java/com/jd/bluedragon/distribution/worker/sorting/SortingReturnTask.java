package com.jd.bluedragon.distribution.worker.sorting;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.sorting.service.SortingReturnService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class SortingReturnTask extends DBSingleScheduler {

	@Autowired
	private SortingReturnService sortingReturnService;

	public boolean executeSingleTask(Task task, String ownSign) throws Exception {
		sortingReturnService.doSortingReturn(task);
		return true;
	}

}
