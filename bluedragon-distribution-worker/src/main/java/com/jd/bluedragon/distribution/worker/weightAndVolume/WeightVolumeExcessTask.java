package com.jd.bluedragon.distribution.worker.weightAndVolume;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class WeightVolumeExcessTask extends DBSingleScheduler {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    //执行任务
    public boolean executeSingleTask(Task task, String ownSign)
            throws Exception {

        return weightAndVolumeCheckService.excuteWeightVolumeExcessTask(task);
    }

    //查询所有满足条件的任务
    public List<Task> selectTasks(String arg0, int queueNum,
                                  List<String> queryCondition, int fetchNum) throws Exception {
        if(logger.isInfoEnabled()){
            logger.info("任务执行fetchNum is"+fetchNum);
        }

        List<Task> tasks = new ArrayList<Task>();
        try {

            tasks = weightAndVolumeCheckService.findLimitedTasks(this.type, this.ownSign);

        } catch (Exception e) {
            this.logger.error("出现异常， 异常信息为：" + e.getMessage(), e);
        }

        return tasks;
    }

    public boolean updateTask(Long id, int executeCount, int status, int type) {
        Task task = new Task();
        task.setId(id);
        task.setExecuteCount(executeCount);
        task.setStatus(status);
        task.setType(type);
        this.taskService.updateBySelective(task);
        return true;
    }

    public boolean updateTask(Long id, int status, int type) {
        Task task = new Task();
        task.setId(id);
        task.setStatus(status);
        task.setType(type);
        this.taskService.updateBySelective(task);
        return true;
    }



}
