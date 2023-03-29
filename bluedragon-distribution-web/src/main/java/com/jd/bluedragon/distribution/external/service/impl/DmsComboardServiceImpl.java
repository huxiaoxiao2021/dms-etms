package com.jd.bluedragon.distribution.external.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.comboard.response.SendFlowDto;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.domain.*;
import com.jd.bluedragon.distribution.external.service.DmsComboardService;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.dto.comboard.CountBoardDto;
import com.jd.bluedragon.distribution.jy.enums.ComboardStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskComboardSourceEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.dbs.util.CollectionUtils;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.BoardBoxInfoDto;
import com.jd.transboard.api.dto.BoardListRequest;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import java.util.stream.Collectors;
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
@UnifiedExceptionProcess
public class DmsComboardServiceImpl implements DmsComboardService {


    @Autowired
    private JyBizTaskComboardService jyBizTaskComboardService;

    @Autowired
    UccPropertyConfiguration ucc;

    @Autowired
    GroupBoardManager groupBoardManager;

    @Autowired
    private SendMDao sendMDao;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.DmsComboardServiceImpl.listComboardBySendFlow", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<BoardQueryResponse> listComboardBySendFlow(BoardQueryRequest request) {
        InvokeResult<BoardQueryResponse> invokeResult = new InvokeResult<>();
        if (request == null || request.getEndSiteId() == null || request.getStartSiteId() == null || request.getPageNo() == null || request.getPageSize() == null) {
            invokeResult.setCode(RESULT_THIRD_ERROR_CODE);
            invokeResult.setMessage(PARAM_ERROR);
            return invokeResult;
        }
        BoardQueryResponse boardQueryResp = new BoardQueryResponse();
        List<BoardDto> boardDtos = new ArrayList<>();
        boardQueryResp.setBoardDtoList(boardDtos);
        invokeResult.setData(boardQueryResp);
        Date time = DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -ucc.getJyComboardTaskCreateTimeBeginDay());

        // 兼容组板新老版本，组板岗推全国后，关闭开关
        if (ucc.getBoardListQuerySwitch()) {
            BoardListRequest boardListRequest = new BoardListRequest();
            boardListRequest.setPageSize(request.getPageSize());
            boardListRequest.setCreateTime(time);
            boardListRequest.setSiteCode(request.getStartSiteId());
            boardListRequest.setDestinationId(request.getEndSiteId());
            boardListRequest.setPageNo(request.getPageNo());
            List<Board> boardList = groupBoardManager.getBoardListBySendFlow(boardListRequest);
            if (!CollectionUtils.isEmpty(boardList)) {
                for (Board board : boardList) {
                    BoardDto boardDto = new BoardDto();
                    boardDto.setBoardCode(board.getCode());
                    boardDto.setComboardSource(board.getBizSource());
                    boardDto.setCreateTime(board.getCreateTime());
                    boardDto.setStartSiteId(request.getStartSiteId());
                    boardDto.setEndSiteId(board.getDestinationId());
                    boardDto.setCreateUserErp(board.getCreateUser());
                    boardDto.setComboardSiteId(request.getStartSiteId());
                    boardDtos.add(boardDto);
                }
            }
        } else {
            // 获取当前场地未封车的板号
            SendFlowDto sendFlow = new SendFlowDto();
            sendFlow.setEndSiteId(request.getEndSiteId());
            sendFlow.setStartSiteId(request.getStartSiteId());
            sendFlow.setQueryTimeBegin(time);
            List<Integer> comboardSourceList = new ArrayList<>();
            comboardSourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
            comboardSourceList.add(JyBizTaskComboardSourceEnum.AUTOMATION.getCode());
            sendFlow.setComboardSourceList(comboardSourceList);
            Page page = PageHelper.startPage(request.getPageNo(), request.getPageSize());
            List<Integer> statusList = new ArrayList<>();
            statusList.add(ComboardStatusEnum.FINISHED.getCode());
            statusList.add(ComboardStatusEnum.CANCEL_SEAL.getCode());
            sendFlow.setStatusList(statusList);
            List<JyBizTaskComboardEntity> boardList = jyBizTaskComboardService.listBoardTaskBySendFlow(sendFlow);

            if (CollectionUtils.isEmpty(boardList)) {
                invokeResult.setCode(RESULT_SUCCESS_CODE);
                invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
                return invokeResult;
            }
            boardQueryResp.setTotal(page.getTotal());

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
        }
        invokeResult.setCode(RESULT_SUCCESS_CODE);
        invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
        return invokeResult;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.DmsComboardServiceImpl.queryBelongBoardByBarCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<QueryBelongBoardResponse> queryBelongBoardByBarCode(QueryBelongBoardRequest request) {
        if (StringUtils.isEmpty(request.getBarCode()) || request.getStartSiteId() == null) {
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

        // 兼容组板新老版本，组板岗推全国后，关闭开关
        if (ucc.getBoardListQuerySwitch()) {
            boardDto.setComboardSiteId(request.getStartSiteId());
            boardDto.setStartSiteId(request.getStartSiteId());
            boardDto.setEndSiteId(boardBoxInfoDto.getDestinationId());
            boardDto.setCreateUserName(boardBoxInfoDto.getOperatorName());
            boardDto.setCreateUserErp(boardBoxInfoDto.getOperatorErp());
            boardDto.setComboardSource(boardBoxInfoDto.getBizSource());
            boardDto.setCreateTime(boardBoxInfoDto.getCreateTime());

            Integer count = groupBoardManager.getBoardBoxCount(boardBoxInfoDto.getCode(), request.getStartSiteId());
            if (count != null) {
                boardDto.setScanCount(count);
            }

            SendM sendM = new SendM();
            sendM.setBoardCode(boardBoxInfoDto.getCode());
            sendM.setCreateSiteCode(request.getStartSiteId());
            SendM sendCodeInfo = sendMDao.findSendMByBoardCode(sendM);
            if (sendCodeInfo!=null) {
                boardDto.setSendCode(sendCodeInfo.getSendCode());
            }
        } else {
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
        }

        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, belongBoardResp);
    }

    @Override
    public InvokeResult<CountBoardResponse> countBoardGroupBySendFlow(CountBoardRequest request) {
        checkCountBoardRequest(request);

        CountBoardDto countBoardDto =assembleCountBoardDto(request);
        List<SendFlowDto> sendFlowDtoList = jyBizTaskComboardService.countBoardGroupBySendFlow(countBoardDto);

        List<ComboardFlowDto> comboardFlowDtoList = BeanUtils.copy(sendFlowDtoList,ComboardFlowDto.class);
        CountBoardResponse response =new CountBoardResponse();
        response.setComboardFlowDtoList(comboardFlowDtoList);
        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,response);
    }

    private void checkCountBoardRequest(CountBoardRequest request) {
        if (!ObjectHelper.isNotNull(request.getStartSiteId())){
            throw new JyBizException("参数错误：始发场地不能为空！");
        }
        if (!CollectionUtils.isEmpty(request.getEndSiteIdList())){
            throw new JyBizException("参数错误：目的场地不能为空！");
        }
    }

    private CountBoardDto assembleCountBoardDto(CountBoardRequest request) {
        CountBoardDto countBoardDto =new CountBoardDto();
        countBoardDto.setStartSiteId(Long.valueOf(request.getStartSiteId()));
        List<Long> endSiteIdList = request.getEndSiteIdList()
            .stream().map(endSite->{ return Long.valueOf(endSite); })
            .collect(Collectors.toList());
        countBoardDto.setEndSiteIdList(endSiteIdList);
        List<Integer> statusList =new ArrayList<>();
        statusList.add(ComboardStatusEnum.PROCESSING.getCode());
        statusList.add(ComboardStatusEnum.FINISHED.getCode());
        statusList.add(ComboardStatusEnum.CANCEL_SEAL.getCode());
        countBoardDto.setStatusList(statusList);
        return countBoardDto;
    }
}
