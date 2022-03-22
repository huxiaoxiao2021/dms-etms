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

    /**
     * 自动化组板
     *
     * @param request
     * @return
     */
    Response<String> bindBoard(BindBoardRequest request);


    Response<Void> autoBoardComplete(AutoBoardCompleteRequest request);


    Response<BoardSendDto> addToBoard(BindBoardRequest request);

    List<BoardSendDto> checkAndReplenishDelivery(CheckBoardStatusDto request);
}
