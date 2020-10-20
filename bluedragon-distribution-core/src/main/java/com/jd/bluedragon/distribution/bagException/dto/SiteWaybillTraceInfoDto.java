package com.jd.bluedragon.distribution.bagException.dto;

import com.jd.etms.waybill.domain.PackageState;

import java.io.Serializable;

/**
 * 站点全程跟踪数据
 *
 * @author fanggang7
 * @time 2020-10-20 10:47:28 周二
 */
public class SiteWaybillTraceInfoDto implements Serializable {

    private static final long serialVersionUID = 4264145831580578147L;

    /**
     * 发货数据
     */
    private PackageState sendData;

    /**
     * 装箱数据
     */
    private PackageState boxingData;

    public PackageState getSendData() {
        return sendData;
    }

    public void setSendData(PackageState sendData) {
        this.sendData = sendData;
    }

    public PackageState getBoxingData() {
        return boxingData;
    }

    public void setBoxingData(PackageState boxingData) {
        this.boxingData = boxingData;
    }

    @Override
    public String toString() {
        return "SiteWaybillTraceInfoDto{" +
                "sendData=" + sendData +
                ", boxingData=" + boxingData +
                '}';
    }
}
