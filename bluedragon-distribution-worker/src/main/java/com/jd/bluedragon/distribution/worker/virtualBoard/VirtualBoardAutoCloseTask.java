package com.jd.bluedragon.distribution.worker.virtualBoard;

import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * 组板自动完结
 * @author fanggang7
 * @time 2021-08-28 21:41:48 周六
 *
 */
public class VirtualBoardAutoCloseTask extends DBSingleScheduler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private VirtualBoardService virtualBoardService;

    private static final int lazyExecuteDays = 1;

    @Override
    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        //调用组板自动完结板号处理逻辑
        return virtualBoardService.handleTimingCloseBoard(task);
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

        List<Task> tasks = new ArrayList<Task>();
        try {

            if (queryCondition.size() != queueNum) {
                fetchNum = fetchNum * queueNum / queryCondition.size();
            }

            List<Task> Tasks = taskService.findVirtualBoardTasks(this.type, fetchNum, this.ownSign, queryCondition, lazyExecuteDays);
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
