package com.jd.bluedragon.distribution.worker.virtualBoard;

import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * 组板自动完结
 * @author fanggang7
 * @time 2021-08-28 21:41:48 周六
 *
 */
public class VirtualBoardAutoCloseTask extends DBSingleScheduler {

    @Autowired
    private VirtualBoardService virtualBoardService;

    @Override
    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        //调用组板自动完结板号处理逻辑
        return virtualBoardService.handleTimingCloseBoard(task);
    }

}
