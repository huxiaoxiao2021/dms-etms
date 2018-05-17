package com.jd.bluedragon.distribution.board.service;

import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;

import java.util.List;

/**
 * Created by xumei3 on 2018/3/27.
 */
public interface BoardCombinationService {
    /**
     * 根据板标获取板标基本信息
     * @param boardCode
     */
    public BoardResponse getBoardByBoardCode(String boardCode) throws Exception;


    /**
     * 回传板标绑定的箱号或包裹号
     */
    public Integer sendBoardBindings(BoardCombinationRequest request, BoardResponse boardResponse) throws Exception;

    /**
     * 回传板标的发货状态
     */
    public Response<Boolean> closeBoard(String boardCode);


    /**
     * 查询发货信息
     * @param sendM
     * @return
     */
    public List<SendM> selectBySendSiteCode(SendM sendM);

    /**
     * 获取组板明细
     * @param boardCode
     * @return
     */
    public Response<List<String>> getBoxesByBoardCode(String boardCode);

    /**
     * 清除组板时加的板号缓存
     * @param boardCode
     * @return
     */
    public boolean clearBoardCache(String boardCode);


    /**
     * 取消组板
     * @param request
     * @return
     */
    public BoardResponse boardCombinationCancel(BoardCombinationRequest request) throws Exception;

    /**
     * 根据板号列表查询托盘体积
     * @param boardCodes
     * @return
     * @throws Exception
     */
    public List<Board> getBoardVolumeByBoardCode(List<String> boardCodes) throws Exception;


}
