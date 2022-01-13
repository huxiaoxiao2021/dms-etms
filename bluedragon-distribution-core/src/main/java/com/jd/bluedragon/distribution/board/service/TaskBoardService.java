package com.jd.bluedragon.distribution.board.service;


import com.jd.bluedragon.common.dto.board.request.CombinationBoardRequest;

/**
 * @author lvyuan21
 */
public interface TaskBoardService {

    /**
     * 保存任务和组板的关系
     */
    void saveTaskBoard(CombinationBoardRequest request);


}
