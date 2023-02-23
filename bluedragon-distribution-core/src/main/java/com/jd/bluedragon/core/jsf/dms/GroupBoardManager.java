package com.jd.bluedragon.core.jsf.dms;

import com.jd.transboard.api.dto.*;

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
     * 允许二次组板（返调度场景）
     * @param addBoardBox
     * @return
     */
    Response<Integer> addBoxToBoardV2(AddBoardBox addBoardBox);

    /**
     * 将板、箱/包裹关系推给TC(不校验板状态)
     * @param addBoardBox
     * @return
     */
    Response<Integer> addBoxToBoardIgnoreStatus(AddBoardBox addBoardBox);

    Response<Integer> addBoxesToBoard(AddBoardBoxes addBoardBox);

    /**
     * 组板转移
     * @param moveBoxRequest
     * @return
     */
    Response<String> moveBoxToNewBoard(MoveBoxRequest moveBoxRequest);

    /**
     * 组板转移(不校验板状态)
     * @param moveBoxRequest
     * @return
     */
    Response<String> moveBoxToNewBoardIgnoreStatus(MoveBoxRequest moveBoxRequest);

    /**
     * 根据板号获取已绑定箱号/包裹号
     * @param boardCode
     * @return
     */
    Response<List<String>> getBoxesByBoardCode(String boardCode);

    /**
     * 关闭板
     * @param boardCode 板号
     * @return 关闭结果
     */
    Response<Boolean> closeBoard(String boardCode);

    /**
     * 根据箱号取所在的板信息
     * @param boxCode 箱号
     * @param siteCode 操作场站
     * @return 板对象
     */
    Response<Board> getBoardByBoxCode(String boxCode, Integer siteCode);


    /**
     * 获取板上该运单的包裹信息
     * @param boardCode
     * @param waybillCode
     * @return
     */
    List<PackageDto> getPackageCodeUnderComBoard(String boardCode,String waybillCode);

    /**
     * 查询板上统计数据： 箱号 运单号 及运单中包裹号
     * @param boardCode
     * @return
     */
    Response<BoardBoxStatisticsResDto> getBoardStatisticsByBoardCode(String boardCode);

    /**
     * 批量取消组板
     * @param removeBoardBoxDto
     * @return
     */
    public Response batchRemoveBardBoxByBoxCodes(RemoveBoardBoxDto removeBoardBoxDto);

    /**
     * 查询板上统计信息
     * @param boardCode
     * @return
     */
    Response<BoardBoxCountDto> getBoxCountInfoByBoardCode(String boardCode);



    /**
     * 根据运单取消组板
     * @param removeBoardBoxDto
     * @return
     */
    Response removeBoardBoxByWaybillCode (RemoveBoardBoxDto removeBoardBoxDto);

    /**
     * 根据包裹、场地获取板信息、板箱信息
     * @param barCode
     * @param siteCode
     * @return
     */
    BoardBoxInfoDto getBoardBoxInfo(String barCode, int siteCode);

    /**
     * 获取当前板号的包裹总数
     * @param boardCode
     * @param siteCode
     * @return
     */
    Integer getBoardBoxCount(String boardCode, Integer siteCode);

    /**
     * 根据流向获取板列表
     * @param request
     * @return
     */
    List<Board> getBoardListBySendFlow(BoardListRequest request);
}
