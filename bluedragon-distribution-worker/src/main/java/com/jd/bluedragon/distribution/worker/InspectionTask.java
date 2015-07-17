package com.jd.bluedragon.distribution.worker;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 验货worker
 * 
 * @author wangzichen
 * 
 */
@Service
public class InspectionTask extends DBSingleScheduler {

	private final static Logger logger = Logger.getLogger(InspectionTask.class);

	@Autowired
	private InspectionService inspectionService;

	@Override
	protected boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
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
