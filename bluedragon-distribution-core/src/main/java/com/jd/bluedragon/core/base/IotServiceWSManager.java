package com.jd.bluedragon.core.base;

/**
 * @author : xumigen
 * @date : 2019/8/12
 */
public interface IotServiceWSManager {

    Boolean isDeviceCodeEnable(String deviceNo);

    Boolean bindDeviceWaybill(String deviceNo,String waybillCode,String erp);
}
