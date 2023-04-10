package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.send.request.BoardCodeSendRequest;
import com.jd.bluedragon.common.dto.send.request.ColdChainSendRequest;
import com.jd.bluedragon.common.dto.send.request.DeliveryVerifyRequest;
import com.jd.bluedragon.common.dto.send.request.DifferentialQueryRequest;
import com.jd.bluedragon.common.dto.send.request.SinglePackageSendRequest;
import com.jd.bluedragon.common.dto.send.request.TransPlanRequest;
import com.jd.bluedragon.common.dto.send.response.CheckBeforeSendResponse;
import com.jd.bluedragon.common.dto.send.response.SendThreeDetailDto;
import com.jd.bluedragon.common.dto.send.response.TransPlanDto;
import com.jd.bluedragon.distribution.external.domain.QueryLoadingRateRequest;
import com.jd.bluedragon.distribution.external.domain.QueryLoadingRateRespone;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.service.DmsDeliveryService;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.DeliveryVerifyService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.external.gateway.service.SendGatewayService;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Deprecated
@Service("dmsDeliveryService")
public class DmsDeliveryServiceImpl implements DmsDeliveryService,SendGatewayService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("deliveryResource")
    private DeliveryResource deliveryResource;

    @Autowired
    @Qualifier("deliveryVerifyService")
    private DeliveryVerifyService deliveryVerifyService;

    @Autowired
    private DeliveryService deliveryService;

    @Override
    public InvokeResult<AbstractMap.Entry<Integer, String>> checkSendCodeStatus(String sendCode) {
        return deliveryResource.checkSendCodeStatus(sendCode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsDeliveryServiceImpl.newPackageSend", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<SendResult> newPackageSend(PackageSendRequest request) {
        // 安卓PDA发货
        request.setBizSource(SendBizSourceEnum.ANDROID_PDA_SEND.getCode());
        return deliveryResource.newPackageSend(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsDeliveryServiceImpl.checkDeliveryInfo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public DeliveryResponse checkDeliveryInfo(String boxCode, String siteCode, String receiveSiteCode, String businessType) {
        return deliveryResource.checkDeliveryInfo(boxCode, siteCode, receiveSiteCode, businessType);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsDeliveryServiceImpl.cancelDeliveryInfo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult cancelDeliveryInfo(DeliveryRequest request) {
        ThreeDeliveryResponse response = deliveryResource.cancelDeliveryInfo(request);
        if (response != null) {
            InvokeResult result = new InvokeResult();
            result.setCode(response.getCode());
            result.setMessage(response.getMessage());
            result.setData(response.getData());
            return result;
        }
        return null;
    }

    @Override
    @Deprecated
    @JProfiler(jKey = "DMSWEB.DmsDeliveryServiceImpl.packageSendVerifyForBox", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdVerifyResponse packageSendVerifyForBox(DeliveryVerifyRequest request) {
        return deliveryVerifyService.packageSendVerifyForBoxCode(request);
    }

    @Override
    @Deprecated
    public JdVerifyResponse newPackageSendGoods(SinglePackageSendRequest request) {
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<List<TransPlanDto>> getTransPlan(TransPlanRequest request){
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<Boolean> checkGoodsForColdChainSend(com.jd.bluedragon.common.dto.send.request.DeliveryRequest request){
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<Boolean> cancelLastDeliveryInfo(com.jd.bluedragon.common.dto.send.request.DeliveryRequest request){
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<Boolean> checkThreeDelivery(ColdChainSendRequest request){
        return null;
    }

    /**
     * 冷链发货校验
     * @param request 单个发货参数
     * @return 校验结果
     * @author fanggang7
     * @time 2021-03-23 11:28:33 周二
     */
    @Override
    public JdCResponse<Boolean> checkColdChainSendDelivery(com.jd.bluedragon.common.dto.send.request.DeliveryRequest request) {
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<Boolean> coldChainSendDelivery(ColdChainSendRequest request){
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<List<SendThreeDetailDto>> differentialQuery(DifferentialQueryRequest request){
        return null;
    }

    @Override
    @Deprecated
    public JdVerifyResponse<CheckBeforeSendResponse> checkBeforeSend(com.jd.bluedragon.common.dto.send.request.DeliveryRequest request){
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<Boolean> sendDeliveryInfo(ColdChainSendRequest request){
        return null;
    }

    @Override
    @Deprecated
    public JdVerifyResponse<Void> boardCodeSend(BoardCodeSendRequest request){
        return null;
    }

    /**
     * 查询箱内发货明细列表
     *
     * @param boxCode 箱号
     * @return 列表明细
     * @author fanggang7
     * @time 2023-01-12 21:40:26 周四
     */
    @Override
    public Result<List<SendDetail>> getCancelSendByBox(String boxCode) {
        Result<List<SendDetail>> result = Result.success();
        result.setData(new ArrayList<SendDetail>());
        try {
            final List<SendDetail> boxSendDetailList = deliveryService.getCancelSendByBox(boxCode);
            if (CollectionUtils.isNotEmpty(boxSendDetailList)) {
                result.setData(boxSendDetailList);
            }
        } catch (Exception e) {
            log.error("DmsDeliveryServiceImpl.getCancelSendByBox exception {}", boxCode, e);
            result.toFail("system exception");
        }
        return result;
    }

    @Override
    public InvokeResult<QueryLoadingRateRespone> queryLoadingRate(QueryLoadingRateRequest request) {
        //根据派车单明细-查询车的装货量，计算装载率
        return null;
    }
}
