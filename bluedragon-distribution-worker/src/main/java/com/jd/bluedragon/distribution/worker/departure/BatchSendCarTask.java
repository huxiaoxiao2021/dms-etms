package com.jd.bluedragon.distribution.worker.departure;

import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yanghongqiang on 2015/1/15.
 */
public class BatchSendCarTask extends DBSingleScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchSendCarTask.class);

    @Autowired
    private DepartureService   departureService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result=false;

        try {
            log.info("task id is {} task type is {}",task.getId(),task.getType());
            result = departureService.dealDepartureTmpToSend(task);
        } catch (Exception e) {
            log.error("批量发车执行失败,task id is {} task type is {}",task.getId(),task.getType(),e);
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

            List<Task> Tasks = taskService.findSpecifiedTasks(this.type, fetchNum, this.ownSign, queryCondition);
            for (Task task : Tasks) {
                if (!isMyTask(queueNum, task.getId(), queryCondition)) {
                    continue;
                }
                tasks.add(task);
            }
        } catch (Exception e) {
            this.log.error("出现异常， 异常信息为：{}" , e.getMessage(), e);
        }
        return tasks;
    }
}
