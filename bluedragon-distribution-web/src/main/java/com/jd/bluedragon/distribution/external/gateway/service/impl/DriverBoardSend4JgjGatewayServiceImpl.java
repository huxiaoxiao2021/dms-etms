package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.common.KeyValueDto;
import com.jd.bluedragon.distribution.api.request.driver.DriverBoardCancelSendRequest;
import com.jd.bluedragon.distribution.api.request.driver.DriverBoardSendCheckBatchCodeRequest;
import com.jd.bluedragon.distribution.api.request.driver.DriverBoardSendRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.base.ClientResult;
import com.jd.bluedragon.distribution.board.service.DriverBoardSendService;
import com.jd.bluedragon.external.gateway.service.DriverBoardSend4JgjGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 司机按板发货接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-12 17:17:15 周五
 */
@Service
public class DriverBoardSend4JgjGatewayServiceImpl implements DriverBoardSend4JgjGatewayService {

    @Autowired
    private DriverBoardSendService driverBoardSendService;

    private final int jgjGatewayResultSuccessCode = 1;
    private final int jgjGatewayResultErrorCode = 0;

    /**
     * 检查批次
     * @return 检查结果
     * @author fanggang7
     * @time 2021-11-16 19:39:04 周二
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.DriverBoardSend4JgjGatewayServiceImpl.checkSendCodeStatus", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public ClientResult<KeyValueDto<Integer, String>> checkBatchCodeStatus(DriverBoardSendCheckBatchCodeRequest request) {
        final ClientResult<KeyValueDto<Integer, String>> result = driverBoardSendService.checkBatchCodeStatus(request);
        return result;
    }

    private void convertJgjResultCode(ClientResult<?> result){
        if(result.isSuccess()){
            result.setCode(jgjGatewayResultSuccessCode);
        }
        if(result.getCode() == jgjGatewayResultSuccessCode){
            result.setCode(jgjGatewayResultErrorCode);
        }
    }

    /**
     * 扫条码按整板发货
     * @param request 发货请求
     * @return 结果
     * @author fanggang7
     * @time 2021-11-12 17:00:08 周五
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.DriverBoardSend4JgjGatewayServiceImpl.sendForWholeBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public ClientResult<Boolean> sendForWholeBoard(DriverBoardSendRequest request) {
        final ClientResult<Boolean> result = driverBoardSendService.sendForWholeBoard(request);
        return result;
    }

    /**
     * 获取批次信息
     * @return 检查结果
     * @author fanggang7
     * @time 2021-11-16 19:39:04 周二
     */
    @Override
    public ClientResult<BaseResponse> getBatchInfo4CancelSend(DriverBoardSendCheckBatchCodeRequest request) {
        return driverBoardSendService.getBatchInfo4CancelSend(request);
    }

    /**
     * 扫条码按整板取消发货
     * @param request 发货请求
     * @return 结果
     * @author fanggang7
     * @time 2021-11-12 17:00:30 周五
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.DriverBoardSend4JgjGatewayServiceImpl.cancelSendForWholeBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public ClientResult<Boolean> cancelSendForWholeBoard(DriverBoardCancelSendRequest request) {
        final ClientResult<Boolean> result = driverBoardSendService.cancelSendForWholeBoard(request);
        return result;
    }

}
