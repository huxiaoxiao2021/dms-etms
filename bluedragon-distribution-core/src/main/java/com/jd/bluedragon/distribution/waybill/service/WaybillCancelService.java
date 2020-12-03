package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.domain.WaybillSiteTrackMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightAndVolumeCheckHandleMessage;

import java.util.List;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 *
 * @author fanggang7
 * @time 2020-09-03 16:13:54 周四
 */
public interface WaybillCancelService {

    Boolean isRefundWaybill(String waybillCode);

    /**
     * 按运单号获取运单取消列表
     * @param waybillCode 运单号
     * @return 结果列表
     * @author fanggang7
     * @time 2020-09-09 11:27:10 周三
     */
    List<CancelWaybill> getByWaybillCode(String waybillCode);

    /**
     * 异常平台发出的可换单消息处理
     * @param waybillSiteTrackMq 消息体
     * @return 处理结果
     * @author fanggang7
     * @time 2020-09-09 16:09:21 周三
     */
    InvokeResult<Boolean> handleWaybillSiteTrackMq(WaybillSiteTrackMq waybillSiteTrackMq);

}