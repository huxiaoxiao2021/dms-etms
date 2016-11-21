package com.jd.bluedragon.distribution.worker;

import java.util.List;

import com.jd.bluedragon.distribution.inspection.exception.InspectionException;
import com.jd.bluedragon.utils.SerialRuleUtil;
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
    private static final String SPLIT_CHAR="$";
	@Override
	protected boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
			try {
				logger.info("验货work开始，task_id: " + task.getId());
				List<Inspection> inspections = inspectionService.parseInspections(task);
				inspectionService.inspectionCore(inspections);
			}catch (InspectionException inspectionEx){
                StringBuilder sb=new StringBuilder("验货执行失败,已知异常");
                sb.append(inspectionEx.getMessage());
                sb.append(SPLIT_CHAR).append(task.getBoxCode());
                sb.append(SPLIT_CHAR).append(task.getKeyword1());
                sb.append(SPLIT_CHAR).append(task.getKeyword2());
                logger.warn(sb.toString());

                return false;
            }catch (Exception e) {
				logger.error("验货worker失败, task id: " + task.getId()
						+ ". 异常信息: " + e.getMessage(), e);
				return false;
			}
		return true;
	}

}
