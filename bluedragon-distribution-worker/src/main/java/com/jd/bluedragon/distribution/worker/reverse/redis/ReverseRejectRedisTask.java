package com.jd.bluedragon.distribution.worker.reverse.redis;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.reverse.service.ReverseRejectService;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 逆向同步运单状态到青龙运单系统Worker
 * @author wangzichen
 *
 */
public class ReverseRejectRedisTask extends RedisSingleScheduler{
    
    @Autowired
    private ReverseRejectService reverseRejectService;

	@Override
	protected boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
		reverseRejectService.rejectInspect(task);
		return true;
	}
    
}
