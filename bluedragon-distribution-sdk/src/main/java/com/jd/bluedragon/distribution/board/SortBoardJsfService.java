package com.jd.bluedragon.distribution.board;

import com.jd.bluedragon.distribution.board.domain.BoardRequestDto;
import com.jd.bluedragon.distribution.board.domain.Request;
import com.jd.bluedragon.distribution.board.domain.Response;

public interface SortBoardJsfService {

    Response<BoardRequestDto> combinationBoardNew(Request request);

}
