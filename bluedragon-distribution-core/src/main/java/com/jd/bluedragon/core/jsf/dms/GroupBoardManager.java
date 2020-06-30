package com.jd.bluedragon.core.jsf.dms;

import com.jd.transboard.api.dto.AddBoardBox;
import com.jd.transboard.api.dto.AddBoardRequest;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.MoveBoxRequest;
import com.jd.transboard.api.dto.OperatorInfo;
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

    /**
     * 将板的状态由“关闭”变为“组板中”
     * @param boardList 一组板号
     * @param operatorInfo 操作人信息
     */
    void resuseBoards(List<String> boardList, OperatorInfo operatorInfo);

    /**
     * 将板、箱/包裹关系推给TC
     * @param addBoardBox
     * @return
     */
    Response<Integer> addBoxToBoard(AddBoardBox addBoardBox);

    /**
     * 组板转移
     * @param moveBoxRequest
     * @return
     */
    Response<String> moveBoxToNewBoard(MoveBoxRequest moveBoxRequest);

    /**
     * 根据板号获取已绑定箱号/包裹号
     * @param boardCode
     * @return
     */
    Response<List<String>> getBoxesByBoardCode(String boardCode);
}
