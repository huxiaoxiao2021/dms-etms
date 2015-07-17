package com.jd.bluedragon.distribution.worker;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.popInspection.service.PopInspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-8 上午11:09:49
 * 
 *             POP收货
 */
public class PopReceiveTask extends DBSingleScheduler {
	
	@Autowired
	private PopInspectionService popInspectionService;
	
	@Override
	protected boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		return this.popInspectionService.execute(task);
	}
}
