package com.jd.bluedragon.distribution.worker.work;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerBusinessService;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 任务线上化-扫描任务
 * 定时扫描task_work_grid_manager_site_scan数据记录，生成网格巡检任务，并分配给相应的岗位
 * @author wuyoude
 *
 */
public class WorkGridManagerSiteScanTask extends DBSingleScheduler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
	@Qualifier("jyWorkGridManagerBusinessService")
    private JyWorkGridManagerBusinessService jyWorkGridManagerBusinessService;
    
    public WorkGridManagerSiteScanTask(){
    	super();
    }
    @Override
    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        return jyWorkGridManagerBusinessService.executeWorkGridManagerSiteScanTask(task);
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

            List<Task> Tasks = taskService.findListForDelayTask(this.type, fetchNum, this.ownSign, queryCondition);
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
