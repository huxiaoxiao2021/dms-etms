package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.saf.WaybillSafResponse;

/**
 * 发往物流网关的接口不要在此类中加方法
 * <p>
 * Created by lixin39 on 2018/5/28.
 */
public interface DmsWaybillService {

    /**
     * PDA POST请求查询锁定订单信息 参数PdaOperateRequest(属性:getPackageCode)
     * 返回值 WaybillResponse(属性Integer:code String:message)
     * 锁定信息内容：取消订单，删除订单，锁定订单，退款100分，拦截订单
     * 返回值 CODE范围 取消订单(29302)，删除订单(29302) ，锁定订单(29301) ，退款100分(29303) ，拦截订单(29305)
     *
     * @param pdaOperateRequest
     * @return
     */
    WaybillSafResponse isCancel(PdaOperateRequest pdaOperateRequest);

}
