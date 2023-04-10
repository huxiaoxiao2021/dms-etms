package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.domain.QueryLoadingRateRequest;
import com.jd.bluedragon.distribution.external.domain.QueryLoadingRateRespone;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.dms.java.utils.sdk.base.Result;

import java.util.AbstractMap;
import java.util.List;
import javax.management.Query;

/**
 * 发往物流网关的接口不要在此类中加方法
 * <p>
 * Created by lixin39 on 2018/5/9.
 */
public interface DmsDeliveryService {

    /**
     * 校验并获取批次号信息，由于物流网关不支持返回参数非JSON格式，故通过该方法转换类型
     * 用途:
     * 1. 扫描发货批次号后，校验批次号是否符合正则表达式
     * 2. 校验批次号是否逆向发货（根据批次号目的地类型校验），该入口不允许操作逆向发货。
     * 3. 校验批次号是否已经封车，已经封车不允许再发货。
     * 4. 获取该批次相关的信息，用于页面展示目的站点名称
     *
     * @param sendCode
     * @return
     */
    InvokeResult<AbstractMap.Entry<Integer, String>> checkSendCodeStatus(String sendCode);

    /**
     * 发货，根据批次号和所扫描的箱子或包裹或板号，进行发货
     *
     * @param request
     * @return
     */
    @Deprecated
    InvokeResult<SendResult> newPackageSend(PackageSendRequest request);

    /**
     * 发货前校验
     *
     * @param boxCode
     * @param siteCode
     * @param receiveSiteCode
     * @param businessType
     * @return
     */
    DeliveryResponse checkDeliveryInfo(String boxCode, String siteCode, String receiveSiteCode, String businessType);

    /**
     * 取消发货
     *
     * @param request
     * @return
     */
    InvokeResult cancelDeliveryInfo(DeliveryRequest request);

    /**
     * 查询箱内发货明细列表
     * @param boxCode 箱号
     * @return 列表明细
     * @author fanggang7
     * @time 2023-01-12 21:40:26 周四
     */
    Result<List<SendDetail>> getCancelSendByBox(String boxCode);


    /**
     * 查询装载率
     * @param request
     * @return
     */
    InvokeResult<QueryLoadingRateRespone>  queryLoadingRate(QueryLoadingRateRequest request);

}
