package com.jd.bluedragon.common.dto.operation.workbench.unload.request;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/8 10:46
 * @Description: 卸车扫描请求
 */
public class UnsealVehicleSacnRequest implements Serializable {

    private static final long serialVersionUID = -4901223213875214602L;

    //运单号
    private String waybillCode;

    //卸车扫描时间
    private String scanTime;

    //当前操作场地编码
    private Integer siteCode;

    //当前操作场地名称
    private String siteName;


    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
