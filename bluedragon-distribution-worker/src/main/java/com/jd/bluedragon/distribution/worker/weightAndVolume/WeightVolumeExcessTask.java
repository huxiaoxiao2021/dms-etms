package com.jd.bluedragon.distribution.worker.weightAndVolume;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
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
            logger.info("查询体积重量超标数据开始-------");
            tasks = weightAndVolumeCheckService.findLimitedTasks(this.type, this.ownSign);

        } catch (Exception e) {
            this.logger.error("出现异常， 异常信息为：" + e.getMessage(), e);
        }

        return tasks;
    }

    @Override
    public Comparator<Task> getComparator() {
        return new Comparator<Task>() {
            public int compare(Task o1, Task o2) {
                if (null != o1 && null != o2
                        && o1.getKeyword1()!=null&&o1.getKeyword1().equals(o2.getKeyword1())) {
                    return 0;
                } else {
                    return 1;
                }
            }

        };
    }



}
