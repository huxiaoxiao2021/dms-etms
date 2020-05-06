package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.send.request.DeliveryVerifyRequest;
import com.jd.bluedragon.common.dto.send.request.SinglePackageSendRequest;
import com.jd.bluedragon.common.dto.send.request.TransPlanRequest;
import com.jd.bluedragon.common.dto.send.response.SendThreeDetailDto;
import com.jd.bluedragon.common.dto.send.response.TransPlanDto;
import com.jd.bluedragon.common.dto.send.request.DeliveryRequest;
import com.jd.bluedragon.common.dto.send.request.DifferentialQueryRequest;

import java.util.List;

/**
 * 发货相关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/6/27
 */
public interface SendGatewayService {
    /**
     * 新发货：按箱号发货校验
     *
     * @param request
     * @return
     */
    JdVerifyResponse packageSendVerifyForBox(DeliveryVerifyRequest request);

    JdVerifyResponse newPackageSendGoods(SinglePackageSendRequest request);

    /**
     * 根据始发和目的站点获取当日的运输计划信息
     * @param request
     * @return
     */
    JdCResponse<List<TransPlanDto>> getTransPlan(TransPlanRequest request);

    /**
     * 金鹏运单校验
     * @param request
     * @return
     */
    JdCResponse<Boolean> checkJpWaybill(DeliveryRequest request);

    /**
     *发货校验-判断是否在路由范围内
     * @param request
     * @return
     */
    JdCResponse<Boolean> checkThreeDeliveryNew(DeliveryRequest request);

    /**
     * 取消上一次发货
     * @param request
     * @return
     */
    JdCResponse<Boolean> cancelLastDeliveryInfo(DeliveryRequest request);

    /**
     *发货前不齐校验
     * @param request
     * @return
     */
    JdCResponse<Boolean> checkThreeDelivery(List<DeliveryRequest> request);

    /**
     * 冷链发货
     * @param request
     * @return
     */
    JdCResponse<Boolean> coldChainSendDelivery(List<DeliveryRequest> request);

    /**
     * 快运发货、冷链发货差异查询
     * @param request
     * @return
     */
    JdCResponse<List<SendThreeDetailDto>> differentialQuery(DifferentialQueryRequest request);

}
