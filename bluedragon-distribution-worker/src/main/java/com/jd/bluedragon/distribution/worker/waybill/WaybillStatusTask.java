package com.jd.bluedragon.distribution.worker.waybill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillStatusService;
import com.jd.bluedragon.distribution.worker.AbstractScheduler;

/**
 * 同步运单状态到青龙运单系统Worker
 * 
 * @author Zhipeng Wong
 */
public class WaybillStatusTask extends DBSingleScheduler {
    
    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private TaskService taskService;
    


    @Autowired
    private WaybillService waybillService;
    
    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result =  waybillService.doWaybillStatusTask(task);
        task.setType(Task.TASK_TYPE_WAYBILL);  //无论如何都把类型改变为waybill（9999）
        return result;
    }

    public List<Task> selectTasks(String arg0, int queueNum,
                                  List<String> queryCondition, int fetchNum) throws Exception {
        if(logger.isInfoEnabled()){
            logger.info("任务执行fetchNum is"+fetchNum);
        }
        if (queryCondition.size() == 0) {
            return Collections.emptyList();
        }

        List<Task> tasks = new ArrayList<Task>();
        try {

            if (queryCondition.size() != queueNum) {
                fetchNum = fetchNum * queueNum / queryCondition.size();
            }

            List<Task> Tasks = taskService.findLimitedTasks(fetchNum, queryCondition,this.ownSign);
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
