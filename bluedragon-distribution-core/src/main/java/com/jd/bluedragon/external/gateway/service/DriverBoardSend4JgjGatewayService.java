package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.distribution.api.request.common.KeyValueDto;
import com.jd.bluedragon.distribution.api.request.driver.DriverBoardCancelSendRequest;
import com.jd.bluedragon.distribution.api.request.driver.DriverBoardSendCheckBatchCodeRequest;
import com.jd.bluedragon.distribution.api.request.driver.DriverBoardSendRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.base.ClientResult;

/**
 * 司机按板发货接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-12 16:55:45 周五
 */
public interface DriverBoardSend4JgjGatewayService {

    /**
     * 检查批次
     * @return 检查结果
     * @author fanggang7
     * @time 2021-11-16 19:39:04 周二
     */
    ClientResult<KeyValueDto<Integer, String>> checkBatchCodeStatus(DriverBoardSendCheckBatchCodeRequest request);

    /**
     * 扫条码按整板发货
     * @param request 发货请求
     * @return 结果
     * @author fanggang7
     * @time 2021-11-12 17:00:08 周五
     */
    ClientResult<Boolean> sendForWholeBoard(DriverBoardSendRequest request);

    /**
     * 获取批次信息
     * @return 检查结果
     * @author fanggang7
     * @time 2021-11-16 19:39:04 周二
     */
    ClientResult<BaseResponse> getBatchInfo4CancelSend(DriverBoardSendCheckBatchCodeRequest request);

    /**
     * 扫条码按整板取消发货
     * @param request 发货请求
     * @return 结果
     * @author fanggang7
     * @time 2021-11-12 17:00:30 周五
     */
    ClientResult<Boolean> cancelSendForWholeBoard(DriverBoardCancelSendRequest request);

}
