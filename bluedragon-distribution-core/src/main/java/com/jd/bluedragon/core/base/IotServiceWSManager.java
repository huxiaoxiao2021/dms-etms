package com.jd.bluedragon.core.base;

import com.jd.bluedragon.utils.ConstantEnums;

/**
 * @author : xumigen
 * @date : 2019/8/12
 */
public interface IotServiceWSManager {

    Boolean isDeviceCodeEnable(String deviceNo);

    /**
     * https://cf.jd.com/pages/viewpage.action?pageId=174828605
     * 鸡毛信设备绑定
     * @param deviceNo
     * @param waybillCode
     * @param erp
     * @return
     */
    Boolean bindDeviceWaybill(String deviceNo,String waybillCode,String erp,ConstantEnums.IotBusiness iotBusiness);
}
