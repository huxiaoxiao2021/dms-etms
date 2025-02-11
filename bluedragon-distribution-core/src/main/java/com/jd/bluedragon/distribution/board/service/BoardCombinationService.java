package com.jd.bluedragon.distribution.board.service;

import com.jd.bluedragon.common.dto.board.request.CombinationBoardRequest;
import com.jd.bluedragon.distribution.api.dto.BoardDto;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.transboard.api.dto.*;

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
     * 校验板号是否可以操作发货
     */
    public BoardResponse checkBoardCanSend(String boardCode) throws Exception ;

    /**
     * 回传板标绑定的箱号或包裹号
     */
    public Integer sendBoardBindings(BoardCombinationRequest request, BoardResponse boardResponse) throws Exception;

    /**
     * 回传板标绑定的箱号或包裹号(新版)
     */
    public Integer sendBoardBindingsNew(BoardCombinationRequest request, BoardResponse boardResponse, Board oldBoard,
                                        CombinationBoardRequest combinationBoardRequest) throws Exception;

    /**
     * 回传板标的发货状态
     */
    public Response<Boolean> closeBoard(String boardCode);

    /**
     * 改变板号状态为发货状态
     * @param boardCode 板号
     * @return 处理结果
     * @author fanggang7
     * @time 2021-09-01 21:51:45 周三
     */
    Response<Boolean> changeBoardStatusSend(String boardCode);


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
     * 获取箱号所属的板号
     * @param siteCode
     * @param boxCode
     * @return
     */
    public Response<Board> getBoardByBoxCode(Integer siteCode, String boxCode);

    /**
     * 获取板信息 板箱信息
     * @param siteCode
     * @param boxCode
     * @return
     */
    public Response<BoardBoxInfoDto> getBoardBoxInfo(Integer siteCode, String boxCode);

    /**
     * 创建新的板号
     * @param request
     * @return
     */
    public InvokeResult<List<BoardDto>> createBoard(AddBoardRequest request);

    /**
     * 根据板号获取板信息
     * @param boardCode 板号
     * @return 板信息
     */
    public InvokeResult<BoardDto> getBoard(String boardCode);

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
    public List<BoardMeasureDto> getBoardVolumeByBoardCode(List<String> boardCodes) throws Exception;

    /**
     * 将板的重量体积信息持久化
     * @param request
     */
    public Response<Void> persistentBoardMeasureData(BoardMeasureRequest request);


}
