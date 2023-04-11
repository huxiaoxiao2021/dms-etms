package com.jd.bluedragon.distribution.worker.jy;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.JyBizTaskAutoCloseService;
import com.jd.bluedragon.distribution.task.domain.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 自动关闭作业任务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 19:46:56 周二
 */
@Slf4j
public class JyBizTaskAutoCloseTask extends DBSingleScheduler {

    @Autowired
    private JyBizTaskAutoCloseService jyBizTaskAutoCloseService;

    @Override
    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        //调用组板自动完结板号处理逻辑
        return jyBizTaskAutoCloseService.handleTimingCloseTask(task);
    }

    /**
     * @param arg0
     * @param queueNum
     * @param queryCondition
     * @param fetchNum
     */
    @Override
    public List<Task> selectTasks(String arg0, int queueNum, List<String> queryCondition, int fetchNum) throws Exception {

        if (queryCondition.size() == 0) {
            return Collections.emptyList();
        }

        List<Task> tasks = new ArrayList<>();
        try {

            if (queryCondition.size() != queueNum) {
                fetchNum = fetchNum * queueNum / queryCondition.size();
            }

            List<Task> Tasks = taskService.findJyBizAutoCloseTasks(this.type, fetchNum, this.ownSign, queryCondition);
            for (Task task : Tasks) {
                if (!isMyTask(queueNum, task.getId(), queryCondition)) {
                    continue;
                }
                tasks.add(task);
            }
        } catch (Exception e) {
            this.log.error("出现异常， 异常信息为：{}", e.getMessage(), e);
        }
        return tasks;
    }
}