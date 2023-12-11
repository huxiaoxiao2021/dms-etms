package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.JyAviationRailwayPickingGoodsService;
import com.jd.bluedragon.external.gateway.service.JyAviationRailwayPickingGoodsGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/12/4 10:57
 * @Description
 */
@Service
public class JyAviationRailwayPickingGoodsGatewayServiceImpl implements JyAviationRailwayPickingGoodsGatewayService {
    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwayPickingGoodsGatewayServiceImpl.class);


    @Autowired
    private BaseParamValidateService baseParamValidateService;

    @Autowired
    private JyAviationRailwayPickingGoodsService jyAviationRailwayPickingGoodsService;

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }


    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.pickingGoodsScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<PickingGoodsRes> pickingGoodsScan(PickingGoodsReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.pickingGoodsScan:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwayPickingGoodsService.pickingGoodsScan(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "航空提货服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse<Void> finishPickGoods(FinishPickGoodsReq req) {
        return null;
    }

    @Override
    public JdCResponse<Void> submitException(ExceptionSubmitReq req) {
        return null;
    }

    @Override
    public JdCResponse<SendFlowRes> listSendFlowInfo(SendFlowReq req) {
        return null;
    }

    @Override
    public JdCResponse<Void> addSendFlow(SendFlowAddReq req) {
        return null;
    }

    @Override
    public JdCResponse<Void> deleteSendFlow(SendFlowDeleteReq req) {
        return null;
    }

    @Override
    public JdCResponse<Void> finishSendTask(FinishSendTaskReq req) {
        return null;
    }

    @Override
    public JdCResponse<AirportTaskRes> listAirportTaskList(AirportTaskReq req) {
        return null;
    }

    @Override
    public JdCResponse<List<AirportTaskAggDto>> listAirportTaskAggList(AirportTaskAggReq req) {
        return null;
    }
}
