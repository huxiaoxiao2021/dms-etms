package com.jd.bluedragon.distribution.capability.send.handler.init;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.capability.send.domain.SendDimensionEnum;
import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/9/5
 * @Description: 构建发货上下文处理器
 */
@Service
public class SendInItContextHandler extends SendDimensionStrategyHandler {

    private static Logger logger = LoggerFactory.getLogger(SendInItContextHandler.class);


    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private GroupBoardManager groupBoardManager;

    /**
     * 构建上下文处理器
     * @param context
     * @return
     */
    @Override
    public boolean doHandler(SendOfCAContext context) {
        //初始化发货维度
        if(!initSendDimension(context)){
            logger.error("SendInItContextHandler initSendDimension fail! , req barCode:{}",context.getBarCode());
            return false;
        }
        //初始化sendM实体
        context.setRequestTurnToSendM(toSendMDomain(context.getRequest()));
        //初始换返回内容
        context.getResponse().setData(new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK));

        return super.doHandler(context);
    }

    /**
     * 按包裹处理逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doPackHandler(SendOfCAContext context) {
        //初始化运单对象
        initWaybillDomain(context);
        return true;
    }

    /**
     * 按运单处理逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doWaybillHandler(SendOfCAContext context) {
        //初始化运单对象
        initWaybillDomain(context);
        return true;
    }

    /**
     * 按板处理逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doBoardHandler(SendOfCAContext context) {
        //初始化板号字段
        context.getRequestTurnToSendM().setBoardCode(context.getBarCode());
        //初始化板对象
        Response<Board> boardResponse = groupBoardManager.getBoard(context.getBarCode());
        if (boardResponse.getCode() == ResponseEnum.SUCCESS.getIndex() && boardResponse.getData() != null) {
            Board board = boardResponse.getData();
            com.jd.bluedragon.distribution.board.domain.Board sortingBoard = new com.jd.bluedragon.distribution.board.domain.Board();
            BeanUtils.copyProperties(board, sortingBoard);
            context.setBoard(sortingBoard);
        }else{
            context.getResponse().toFail(String.format("未能加载到板信息，请检查板号%s是否正确！",context.getBarCode()));
            return false;
        }
        return true;
    }

    /**
     * 是否必须执行，返回true时不允许跳过,必须执行。
     * @return
     */
    @Override
    public boolean mustHandler() {
        return true;
    }

    /**
     * 初始化发货维度
     * @param context
     * @return
     */
    private boolean initSendDimension(SendOfCAContext context){

        if(WaybillUtil.isPackageCode(context.getBarCode())){
            context.setSendDimension(SendDimensionEnum.PACK);
        } else if (WaybillUtil.isWaybillCode(context.getBarCode())) {
            context.setSendDimension(SendDimensionEnum.WAYBILL);
        } else if (BusinessUtil.isBoxcode(context.getBarCode())) {
            context.setSendDimension(SendDimensionEnum.BOX);
        } else if (BusinessUtil.isBoardCode(context.getBarCode())) {
            context.setSendDimension(SendDimensionEnum.BOARD);
        } else if (BusinessUtil.isSendCode(context.getBarCode())) {
            context.setSendDimension(SendDimensionEnum.SEND_CODE);
        }else {
            return false;
        }

        return true;
    }

    /**
     * 初始化运单对象
     * 仅在运单和包裹维度时初始化
     * @param context
     */
    private void initWaybillDomain(SendOfCAContext context){

        context.setWaybill(waybillCommonService.findByWaybillCode(WaybillUtil.getWaybillCode(context.getBarCode())));

    }

    /**
     * 请求拼装SendM发货对象
     *
     * @param request
     * @return
     */
    private SendM toSendMDomain(PackageSendRequest request) {
        SendM domain = new SendM();
        domain.setBoxCode(request.getBoxCode());
        domain.setReceiveSiteCode(request.getReceiveSiteCode());
        domain.setSendCode(request.getSendCode());
        domain.setCreateSiteCode(request.getSiteCode());
        String turnoverBoxCode = request.getTurnoverBoxCode();
        if (StringUtils.isNotBlank(turnoverBoxCode) && turnoverBoxCode.length() > 30) {
            domain.setTurnoverBoxCode(turnoverBoxCode.substring(0, 30));
        } else {
            domain.setTurnoverBoxCode(turnoverBoxCode);
        }
        domain.setCreateUser(request.getUserName());
        domain.setCreateUserCode(request.getUserCode());
        domain.setSendType(request.getBusinessType());
        domain.setTransporttype(request.getTransporttype());
        domain.setBizSource(request.getBizSource());
        domain.setYn(Constants.YN_YES);
        domain.setCreateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        domain.setOperateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        domain.setOperatorTypeCode(request.getOperatorTypeCode());
        domain.setOperatorId(request.getOperatorId());
        domain.setOperatorData(request.getOperatorData());
        return domain;
    }
}
