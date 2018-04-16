package com.jd.bluedragon.distribution.worker.board;

import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.transboard.api.dto.AddBoardBox;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xumei3 on 2018/4/13.
 */
public class BoardCombinationCancelTask extends SendDBSingleScheduler {

    @Autowired
    BoardCombinationService boardCombinationService;

    public boolean executeSingleTask(Task task, String ownSign)
            throws Exception {
        //调用组板发货处理逻辑
        AddBoardBox addBoardBox = new AddBoardBox();
        return boardCombinationService.boardCombinationCancel(addBoardBox);
    }
}
