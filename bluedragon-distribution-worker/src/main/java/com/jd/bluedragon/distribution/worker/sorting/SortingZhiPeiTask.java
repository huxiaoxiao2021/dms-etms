package com.jd.bluedragon.distribution.worker.sorting;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.sorting.service.SortingReturnService;
import com.jd.bluedragon.distribution.sorting.service.SortingZhiPeiService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;

/**
 * 智配中心分拣任务:推送mq消息给终端
 * Created by lhc on 2017/4/7.
 */
public class SortingZhiPeiTask extends DBSingleScheduler {

	@Autowired
	private SortingZhiPeiService sortingZhiPeiService;

	public boolean executeSingleTask(Task task, String ownSign) throws Exception {
		
		sortingZhiPeiService.doSorting(task);
		return true;
	}

}
