package com.jd.bluedragon.core.jsf.dms;

import com.jd.transboard.api.dto.AddBoardRequest;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;

import java.util.List;

/**
 * @author lijie
 * @date 2019/10/31 21:31
 */
public interface GroupBoardManager {
    /**
     * 创建新的板号
     * @param var1 操作人信息、目的地、需要创建板号的数量
     * @return 新板号（一个或者多个）
     */
    Response<List<Board>> createBoards(AddBoardRequest var1);

    /**
     * 根据板号获取板信息
     * @param boardCode 板号
     * @return 板的所有信息
     */
    Response<Board> getBoard(String boardCode);
}
