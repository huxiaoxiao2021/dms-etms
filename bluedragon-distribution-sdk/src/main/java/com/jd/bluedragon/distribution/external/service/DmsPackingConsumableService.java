package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.packingconsumable.domain.DmsPackingConsumableInfo;
import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import com.jd.ql.dms.common.domain.JdResponse;

/**
 * Created by hanjiaxing1 on 2018/8/10.
 *
 * 包装耗材项目JSF接口
 *
 * 调用方：运输、财务
 * 发往物流网关的接口不要在此类中加方法
 */
public interface DmsPackingConsumableService {

    /*
    * 获取快运中心支持的耗材信息
    */
    JdResponse<DmsPackingConsumableInfo> getPackingConsumableInfoByDmsId(Integer dmsId);

    /*
     * 获取快运中心支持的耗材信息
     */
    JdResponse<DmsPackingConsumableInfo> getPackingConsumableInfoByDmsCode(String dmsCode);

    /*
    * 获取编号对应的耗材信息
    */
    JdResponse<PackingConsumableBaseInfo> getPackingConsumableInfoByCode(String consumableCode);

    /*
     * 获取运单的包装耗材确认状态
     */
    JdResponse<Boolean> getConfirmStatusByWaybillCode(String waybillCode);

}
