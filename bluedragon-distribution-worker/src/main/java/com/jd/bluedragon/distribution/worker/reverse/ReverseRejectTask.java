package com.jd.bluedragon.distribution.worker.reverse;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.reverse.service.ReverseRejectService;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 同步运单状态到青龙运单系统Worker
 * 
 * @author Zhipeng Wang
 */
public class ReverseRejectTask extends DBSingleScheduler {
    
    @Autowired
    private ReverseRejectService reverseRejectService;
    
	@Override
	protected boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		reverseRejectService.rejectInspect(task);
		return true;
	}
}
