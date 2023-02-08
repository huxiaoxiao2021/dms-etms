package com.jd.bluedragon.distribution.board;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.board.domain.*;

import java.util.List;

public interface SortBoardJsfService {

    Response<BoardRequestDto> combinationBoardNew(Request request);

    /**
     * 分拣自动化创建板号
     * @param request
     * @return
     */
    InvokeResult<List<Board>> createBoard(AddBoardRequest request);


    Response<Void> autoBoardComplete(AutoBoardCompleteRequest request);

    Response<BoardSendDto> addToBoard(BindBoardRequest request);

    List<BoardSendDto> checkAndReplenishDelivery(CheckBoardStatusDto request);
    Response<List<String>> calcBoard(AutoBoardCompleteRequest domain);

    /**
     * 自动化扫描(组板+发货)
     */
    Response<BoardSendDto> sortMachineComboard(BindBoardRequest request);

    /**
     * 分拣机取消组板发货
     * @param request
     * @return
     */
    Response<Void> cancelSortMachineComboard(BindBoardRequest request);
}
