package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.distribution.auto.service.SortingPrepareService;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dudong on 2014/10/18.
 */
public class InspectionPrepareTask extends DBSingleScheduler {
    private static final Log logger= LogFactory.getLog(InspectionPrepareTask.class);

    @Autowired
    private InspectionService inspectionService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result=false;

        try {
            logger.info("自动分拣写入交接表DB 任务, task id is " + task.getId());
            result = inspectionService.dealHandoverPackages(task);
        } catch (Exception e) {
            logger.error("自动分拣写入交接表DB任务执行失败，task id is" + task.getId());
            logger.error("自动分拣写入交接表DB任务执行失败，异常信息为：" + e.getMessage(), e);

            return Boolean.FALSE;
        }
        return result;
    }


    @Override
    public List<Task> selectTasks(String arg0, int queueNum, List<String> queryCondition, int fetchNum) throws Exception {
        if (queryCondition.size() == 0) {
            return Collections.emptyList();
        }

        List<Task> tasks = new ArrayList<Task>();
        try {

            if (queryCondition.size() != queueNum) {
                fetchNum = fetchNum * queueNum / queryCondition.size();
            }

            List<Task> Tasks = taskService.findSpecifiedTasks(this.type, fetchNum, this.ownSign,queryCondition);
            for (Task task : Tasks) {
                if (!isMyTask(queueNum, task.getId(), queryCondition)) {
                    continue;
                }
                tasks.add(task);
            }
        } catch (Exception e) {
            this.logger.error("出现异常， 异常信息为：" + e.getMessage(), e);
        }
        return tasks;
    }
}
