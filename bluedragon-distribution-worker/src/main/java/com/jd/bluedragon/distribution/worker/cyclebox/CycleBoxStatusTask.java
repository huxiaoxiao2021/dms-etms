package com.jd.bluedragon.distribution.worker.cyclebox;

import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;

public class CycleBoxStatusTask extends DBSingleScheduler {
    @Autowired
    private CycleBoxService cycleBoxService;

    public boolean executeSingleTask(Task task, String ownSign)
            throws Exception {
        //同步青流箱状态
        return cycleBoxService.pushCycleBoxStatus(task);
    }
}
