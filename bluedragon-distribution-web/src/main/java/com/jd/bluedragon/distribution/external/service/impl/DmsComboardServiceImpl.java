package com.jd.bluedragon.distribution.external.service.impl;

import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.common.dto.comboard.response.SendFlowDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.domain.*;
import com.jd.bluedragon.distribution.external.service.DmsComboardService;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.dto.comboard.BoardCountDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.BoardCountReq;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskComboardSourceEnum;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dbs.util.CollectionUtils;
import com.jd.transboard.api.dto.BoardBoxInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

@Service("dmsComboardService")
@Slf4j
public class DmsComboardServiceImpl implements DmsComboardService {


  @Autowired
  private JyBizTaskComboardService jyBizTaskComboardService;

  @Autowired
  UccPropertyConfiguration ucc;

  @Autowired
  GroupBoardManager groupBoardManager;
  
  @Override
  public InvokeResult<BoardQueryResponse> listComboardBySendFlow(BoardQueryRequest request) {
    InvokeResult<BoardQueryResponse> invokeResult = new InvokeResult<>();
    if (request == null || request.getEndSiteId() == null 
            || request.getStartSiteId() == null || request.getPageNo() == null 
            || request.getPageSize() == null) {
      invokeResult.setCode(RESULT_THIRD_ERROR_CODE);
      invokeResult.setMessage(PARAM_ERROR);
      return invokeResult;
    }
    BoardQueryResponse boardQueryResp = new BoardQueryResponse();
    List<BoardDto> boardDtos = new ArrayList<>();
    boardQueryResp.setBoardDtoList(boardDtos);
    invokeResult.setData(boardQueryResp);

    // 获取当前场地未封车的板号
    SendFlowDto sendFlow = new SendFlowDto();
    sendFlow.setEndSiteId(request.getEndSiteId());
    sendFlow.setStartSiteId(request.getStartSiteId());
    Date time = DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -ucc.getJyComboardTaskCreateTimeBeginDay());
    sendFlow.setQueryTimeBegin(time);
    List<Integer> comboardSourceList = new ArrayList<>();
    comboardSourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
    comboardSourceList.add(JyBizTaskComboardSourceEnum.AUTOMATION.getCode());
    sendFlow.setComboardSourceList(comboardSourceList);
    PageHelper.startPage(request.getPageNo(),request.getPageSize());
    List<JyBizTaskComboardEntity> boardList = jyBizTaskComboardService.listBoardTaskBySendFlow(sendFlow);

    if (CollectionUtils.isEmpty(boardList)) {
      invokeResult.setCode(RESULT_SUCCESS_CODE);
      invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
      return invokeResult;
    }

    //查询流向下7天内未封车的板总数
    BoardCountReq boardCountReq = new BoardCountReq();
    boardCountReq.setCreateTime(time);
    List<Integer> endSiteIdList = new ArrayList<>();
    endSiteIdList.add(request.getEndSiteId());
    boardCountReq.setEndSiteIdList(endSiteIdList);
    boardCountReq.setStartSiteId((long) request.getStartSiteId());
    List<Integer> sourceList = new ArrayList<>();
    sourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
    sourceList.add(JyBizTaskComboardSourceEnum.AUTOMATION.getCode());
    boardCountReq.setComboardSourceList(sourceList);
    List<BoardCountDto> entityList = jyBizTaskComboardService.boardCountTaskBySendFlowList(boardCountReq);

    if (!CollectionUtils.isEmpty(entityList) && entityList.get(0) != null && entityList.get(0).getBoardCount() != null) {
      boardQueryResp.setTotal(entityList.get(0).getBoardCount().longValue());
    }

    for (JyBizTaskComboardEntity board : boardList) {
      BoardDto boardDto = new BoardDto();
      boardDto.setSendCode(board.getSendCode());
      boardDto.setBoardCode(board.getBoardCode());
      boardDto.setComboardSource(board.getComboardSource());
      boardDto.setScanCount(board.getHaveScanCount());
      boardDto.setCreateTime(board.getCreateTime());
      boardDto.setStartSiteId(board.getStartSiteId().intValue());
      boardDto.setEndSiteId(board.getEndSiteId().intValue());
      boardDto.setCreateUserErp(board.getCreateUserErp());
      boardDto.setCreateUserName(board.getCreateUserName());
      boardDto.setComboardSiteId(board.getStartSiteId().intValue());
      boardDtos.add(boardDto);
    }
    invokeResult.setCode(RESULT_SUCCESS_CODE);
    invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
    return invokeResult;  
  }
  
  @Override
  public InvokeResult<QueryBelongBoardResponse> queryBelongBoardByBarCode(
      QueryBelongBoardRequest request) {
    if (StringUtils.isEmpty(request.getBarCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    QueryBelongBoardResponse belongBoardResp = new QueryBelongBoardResponse();
    BoardDto boardDto = new BoardDto();
    belongBoardResp.setBoardDto(boardDto);
    // 查询板号信息
    BoardBoxInfoDto boardBoxInfoDto = groupBoardManager.getBoardBoxInfo(request.getBarCode(), request.getStartSiteId());
    if (boardBoxInfoDto != null && boardBoxInfoDto.getCode() != null) {
      boardDto.setBoardCode(boardBoxInfoDto.getCode());
    } else {
      log.error("未找到对应的板信息：{}", JsonHelper.toJson(request.getBarCode()));
      return new InvokeResult<>(NOT_FIND_BOARD_INFO_CODE, NOT_FIND_BOARD_INFO_MESSAGE);
    }

    // 根据板号查询任务信息
    JyBizTaskComboardEntity query = new JyBizTaskComboardEntity();
    query.setStartSiteId((long) request.getStartSiteId());
    query.setBoardCode(boardBoxInfoDto.getCode());
    JyBizTaskComboardEntity board = jyBizTaskComboardService.queryBizTaskByBoardCode(query);

    if (board == null) {
      log.error("未找到板的批次信息：{}", JsonHelper.toJson(boardBoxInfoDto.getCode()));
      return new InvokeResult<>(NOT_FIND_BOARD_INFO_CODE, NOT_FIND_BOARD_INFO_MESSAGE);
    } else {
      boardDto.setSendCode(board.getSendCode());
      boardDto.setBoardCode(board.getBoardCode());
      boardDto.setComboardSource(board.getComboardSource());
      boardDto.setScanCount(board.getHaveScanCount());
      boardDto.setCreateTime(board.getCreateTime());
      boardDto.setStartSiteId(board.getStartSiteId().intValue());
      boardDto.setEndSiteId(board.getEndSiteId().intValue());
      boardDto.setCreateUserErp(board.getCreateUserErp());
      boardDto.setCreateUserName(board.getCreateUserName());
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, belongBoardResp); 
  }
}
