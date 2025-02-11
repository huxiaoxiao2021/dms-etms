package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bk.common.util.string.StringUtils;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.BizSourceEnum;
import com.jd.bluedragon.common.dto.board.request.CombinationBoardRequest;
import com.jd.bluedragon.common.dto.board.response.BoardCheckDto;
import com.jd.bluedragon.common.dto.board.response.BoardDetailDto;
import com.jd.bluedragon.common.dto.board.response.BoardInfoDto;
import com.jd.bluedragon.common.dto.board.response.VirtualBoardResultDto;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.rest.board.BoardCombinationResource;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.SortBoardGatewayService;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.domain.JdResponseStatusInfo;
import com.jd.transboard.api.dto.BoardBoxInfoDto;
import com.jd.transboard.api.dto.Response;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * SortBoardGatewayServiceImpl
 * 组板相关处理
 *
 * @author jiaowenqiang
 * @date 2019/7/9
 */
@Service("sortBoardGatewayServiceImpl")
public class SortBoardGatewayServiceImpl implements SortBoardGatewayService {


    @Autowired
    private BoardCombinationResource boardCombinationResource;

    @Autowired
    protected InspectionDao inspectionDao;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private BoardCombinationService boardCombinationService;

    @Autowired
    private VirtualBoardService virtualBoardService;

    private static final Logger log = LoggerFactory.getLogger(SortBoardGatewayServiceImpl.class);

    /**
     * 组板校验
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SortBoardGatewayServiceImpl.validateBoardCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<BoardCheckDto> validateBoardCode(String boardCode) {

        JdCResponse<BoardCheckDto> jdCResponse = new JdCResponse<>();
        JdResponse<BoardResponse> response = boardCombinationResource.validation(boardCode);

        //data转化
        BoardCheckDto boardCheckDto = new BoardCheckDto();
        boardCheckDto.setReceiveSiteCode(response.getData().getReceiveSiteCode());
        boardCheckDto.setReceiveSiteName(response.getData().getReceiveSiteName());
        jdCResponse.setData(boardCheckDto);

        if (response.getCode() != 200) {
            jdCResponse.setMessage(convertMessage(response.getData().getStatusInfo()));
        } else {
            jdCResponse.setMessage(response.getMessage());
        }

        //code转化
        if (response.getCode() > 30000 && response.getCode() < 40000) {
            jdCResponse.setCode(JdCResponse.CODE_CONFIRM);
        } else {
            jdCResponse.setCode(response.getCode());
        }

        return jdCResponse;
    }

    /**
     * 组板
     */
    @Override
    @BusinessLog(sourceSys = 1,bizType = 2005,operateType = 20051)
    @JProfiler(jKey = "DMSWEB.SortBoardGatewayServiceImpl.combinationBoard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse combinationBoard(CombinationBoardRequest request) {

        JdCResponse jdCResponse = new JdCResponse<>();
        JdResponse<BoardResponse> response = boardCombinationResource.combinationNew(convertToBoardCombinationRequest(request));

        if (response.getCode() != 200) {
            jdCResponse.setMessage(convertMessage(response.getData().getStatusInfo()));
        } else {
            jdCResponse.setMessage(response.getMessage());
        }

        //code转化
        if (response.getCode() > 30000 && response.getCode() < 40000) {
            jdCResponse.setCode(JdCResponse.CODE_CONFIRM);
        } else {
            jdCResponse.setCode(response.getCode());
        }
        jdCResponse.setCode(response.getCode());


        return jdCResponse;
    }

    /**
     * 组板(自动生成板号)
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SortBoardGatewayServiceImpl.combinationBoardNew",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<BoardCheckDto> combinationBoardNew(CombinationBoardRequest request) {
        JdCResponse<BoardCheckDto> jdcResponse = new JdCResponse<>();
        // 增加暂存校验
        final JdCResponse<BoardCheckDto> checkResult = this.checkDiscarded4BoardCombine(request);
        if(!checkResult.isSucceed()){
            return checkResult;
        }
        JdResponse<BoardResponse> response = boardCombinationResource.combinationBoardNew(request);
        BoardCheckDto boardCheckDto = new BoardCheckDto();
        boardCheckDto.setBoardCode(response.getData().getBoardCode());
        boardCheckDto.setReceiveSiteCode(response.getData().getReceiveSiteCode());
        boardCheckDto.setReceiveSiteName(response.getData().getReceiveSiteName());
        jdcResponse.setData(boardCheckDto);
        // 如果是错组
        if(response.getData() != null && response.getData().getSingleStatusInfo() != null 
                && Objects.equals(response.getData().getSingleStatusInfo().getStatusCode(), SortingResponse.CODE_CROUTER_ERROR)){
            boardCheckDto.setFlowDisaccord(1);
        }
        jdcResponse.setCode(response.getCode());
        jdcResponse.setMessage(response.getMessage());
        return jdcResponse;
    }

    private JdCResponse<BoardCheckDto> checkDiscarded4BoardCombine(CombinationBoardRequest request){
        JdCResponse<BoardCheckDto> result = new JdCResponse<>();
        result.toSucceed();
        try {
            if ((WaybillUtil.isWaybillCode(request.getBoxOrPackageCode()) || WaybillUtil.isPackageCode(request.getBoxOrPackageCode())) &&
                    waybillTraceManager.isOpCodeWaste(request.getBoxOrPackageCode())){
                result.toError("弃件货物禁止组板，请拉至弃件暂存区操作弃件扫描");
                return result;
            }
        } catch (Exception e) {
            log.error("checkDiscarded4BoardCombine exception ", e);
        }
        return result;
    }

    /**
     * 取消组板
     */
    @Override
    @BusinessLog(sourceSys = 1,bizType = 2005,operateType = 20052)
    @JProfiler(jKey = "DMSWEB.SortBoardGatewayServiceImpl.combinationBoardCancel",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse combinationBoardCancel(CombinationBoardRequest request) {

        JdCResponse jdCResponse = new JdCResponse<>();
        JdResponse<BoardResponse> response = boardCombinationResource.cancel(convertToBoardCombinationRequest(request));

        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMessage());

        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SortBoardGatewayServiceImpl.combinationBoardCancelNew",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<BoardCheckDto> combinationBoardCancelNew(CombinationBoardRequest request) {
        JdCResponse<BoardCheckDto> jdcResponse = new JdCResponse<>();
        JdResponse<BoardResponse> response = boardCombinationResource.combinationBoardCancelNew(request);
        BoardCheckDto boardCheckDto = new BoardCheckDto();
        if (response.getData() != null) {
            boardCheckDto.setBoardCode(response.getData().getBoardCode());
            boardCheckDto.setReceiveSiteName(response.getData().getReceiveSiteName());
            boardCheckDto.setReceiveSiteCode(response.getData().getReceiveSiteCode());
        }
        jdcResponse.setData(boardCheckDto);
        jdcResponse.setCode(response.getCode());
        jdcResponse.setMessage(response.getMessage());

        return jdcResponse;
    }

    /**
     * 根据板号查询板号下的组板明细
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SortBoardGatewayServiceImpl.queryBoardDetail",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<BoardDetailDto> queryBoardDetail(String boardCode) {

        JdCResponse<BoardDetailDto> jdCResponse = new JdCResponse<>();
        JdResponse<BoardResponse> response = boardCombinationResource.getBoxCodesByBoardCode(boardCode);

        if (response.getCode() == 200) {
            BoardDetailDto boardDetailDto = new BoardDetailDto();
            boardDetailDto.setBoardDetails(response.getData().getBoardDetails());
            boardDetailDto.setBoxNum(response.getData().getBoxNum());
            boardDetailDto.setPackageNum(response.getData().getPackageNum());
            jdCResponse.setData(boardDetailDto);
        }

        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMessage());

        return jdCResponse;
    }

    /**
     * 查询箱子或包裹所属板号
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SortBoardGatewayServiceImpl.queryBoardInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<BoardInfoDto> queryBoardInfo(Integer siteCode, String packageOrBoxCode) {

        JdCResponse<BoardInfoDto> jdCResponse = new JdCResponse<>();
        Response<BoardBoxInfoDto> response = boardCombinationService.getBoardBoxInfo(siteCode, packageOrBoxCode);

        if (response.getCode() == 200) {
            BoardInfoDto boardInfoDto = new BoardInfoDto();
            boardInfoDto.setCode(response.getData().getCode());
            boardInfoDto.setDestination(response.getData().getDestination());
            boardInfoDto.setOperatorErp(response.getData().getOperatorErp());
            boardInfoDto.setOperatorName(response.getData().getOperatorName());
            //根据板号查询扫描件量
            JdCResponse<VirtualBoardResultDto> virtualBoard = virtualBoardService.getBoxCountByBoardCode(response.getData().getCode());
            if(virtualBoard.getCode() == 200 && virtualBoard.getData() != null){
                int packageTotal = 0;
                int boxTotal = 0;
                if(virtualBoard.getData().getPackageTotal() != null){
                    packageTotal = virtualBoard.getData().getPackageTotal();
                }
                if(virtualBoard.getData().getBoxTotal() != null){
                    boxTotal =virtualBoard.getData().getBoxTotal();
                }
                boardInfoDto.setScanQuantity(packageTotal + boxTotal);
            }
            jdCResponse.setData(boardInfoDto);
        }

        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMesseage());

        return jdCResponse;
    }

    @Override
    public JdCResponse<Void> combinationComplete(CombinationBoardRequest request) {
        JdCResponse<Void> jdcResponse = new JdCResponse<>();
        if (request == null || StringUtils.isBlank(request.getBoardCode())) {
            jdcResponse.toFail("板号不能为空");
            return jdcResponse;
        }
        JdResponse<Void> jdResponse = boardCombinationResource.combinationComplete(request);
        jdcResponse.setCode(jdResponse.getCode());
        jdcResponse.setMessage(jdResponse.getMessage());
        return jdcResponse;
    }


    //实体转化
    private BoardCombinationRequest convertToBoardCombinationRequest(CombinationBoardRequest param) {

        BoardCombinationRequest boardCombinationRequest = new BoardCombinationRequest();

        boardCombinationRequest.setBoxOrPackageCode(param.getBoxOrPackageCode());
        boardCombinationRequest.setBoardCode(param.getBoardCode());
        boardCombinationRequest.setReceiveSiteCode(param.getReceiveSiteCode());
        boardCombinationRequest.setReceiveSiteName(param.getReceiveSiteName());
        boardCombinationRequest.setIsForceCombination(param.isForceCombination());
        boardCombinationRequest.setSiteCode(param.getCurrentOperate().getSiteCode());
        boardCombinationRequest.setSiteName(param.getCurrentOperate().getSiteName());
        boardCombinationRequest.setUserCode(param.getUser().getUserCode());
        boardCombinationRequest.setUserName(param.getUser().getUserName());
        boardCombinationRequest.setBizSource(BizSourceEnum.PDA.getValue());
        return boardCombinationRequest;

    }

    //message转化
    private String convertMessage(List<JdResponseStatusInfo> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (JdResponseStatusInfo s : list) {
            stringBuilder.append("   ");
            stringBuilder.append(s.getStatusMessage());
        }

        return stringBuilder.toString();
    }


}
