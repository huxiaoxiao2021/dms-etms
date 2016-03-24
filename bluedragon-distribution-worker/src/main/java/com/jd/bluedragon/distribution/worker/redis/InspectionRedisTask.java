package com.jd.bluedragon.distribution.worker.redis;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 验货worker
 * 
 * @author wangzichen
 * 
 */
public class InspectionRedisTask extends RedisSingleScheduler {

	private final static Logger logger = Logger.getLogger(InspectionRedisTask.class);

	@Autowired
	private InspectionService inspectionService;

	@Override
	protected boolean executeSingleTask(Task task, String ownSign)
			throws Exception {//FIXME：没必要的异常，如需要抛出需要自定义
			try {
				logger.info("验货work开始，task_id: " + task.getId());
				List<Inspection> inspections = inspectionService.parseInspections(task);
				inspectionService.inspectionCore(inspections);
			} catch (Exception e) {
				logger.error("验货worker失败, task id: " + task.getId()
						+ ". 异常信息: " + e.getMessage(), e);
				return false;
			}
		return true;
	}

}
