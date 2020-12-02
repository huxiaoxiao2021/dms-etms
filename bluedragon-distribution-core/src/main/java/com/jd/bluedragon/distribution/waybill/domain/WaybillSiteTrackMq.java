package com.jd.bluedragon.distribution.waybill.domain;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 可换单消息
 *
 * @author fanggang7
 * @time 2020-09-09 16:01:19 周三
 */
public class WaybillSiteTrackMq implements Serializable {

    private static final long serialVersionUID = 5898300649007980062L;

    /**
     * 系统编码
     */
    private String systemCode;

    /**
     * 原始运单号
     */
    private String waybillCode;

    /**
     * 调度时间 格式'yyyy-MM-dd HH:mm:ss'
     */
    private String dispatchTime;

    /**
     * 异常标准编码
     */
    private String abnormalNormCode;

    /**
     * 平台调度结果类型
     */
    private Integer dispatchType;

    /**
     * 平台调度结果类型名称
     */
    private String dispatchTypeName;

    /**
     * 当前站点ID
     */
    private Integer siteId;

    /**
     * 当前站点类型
     */
    private Integer siteType;

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(String dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public String getAbnormalNormCode() {
        return abnormalNormCode;
    }

    public void setAbnormalNormCode(String abnormalNormCode) {
        this.abnormalNormCode = abnormalNormCode;
    }

    public Integer getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(Integer dispatchType) {
        this.dispatchType = dispatchType;
    }

    public String getDispatchTypeName() {
        return dispatchTypeName;
    }

    public void setDispatchTypeName(String dispatchTypeName) {
        this.dispatchTypeName = dispatchTypeName;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
