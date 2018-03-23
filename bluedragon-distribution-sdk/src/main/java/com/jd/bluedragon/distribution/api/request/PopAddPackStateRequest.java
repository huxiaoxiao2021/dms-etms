package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.io.Serializable;

/**
 * POP打印数据至运单 请求对象
 *
 *  原打印系统调用运单WEB接口，改调用JSF接口。
 *  分拣系统兼容不改造打印系统
 *
 *  *{"packageBarcode":"VA00010628892","waybillCode":"VA00010628892","operatorUserId":"10053",
 * "operatorUser":"邢松","operatorSite":"北京马驹桥分拣中心",
 * "operatorSiteId":"910","state":"-250","remark":"驻厂标签打印","createTime":1516019841532}
 */
public class PopAddPackStateRequest implements Serializable{

    private static final long serialVersionUID = 8464341458768408318L;

    private String packageBarcode;

    private String waybillCode;

    private String operatorUserId;

    private String operatorUser;

    private String operatorSite;

    private String operatorSiteId;

    private String state;

    private String remark;

    private Long createTime;

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getOperatorUserId() {
        return operatorUserId;
    }

    public void setOperatorUserId(String operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    public String getOperatorUser() {
        return operatorUser;
    }

    public void setOperatorUser(String operatorUser) {
        this.operatorUser = operatorUser;
    }

    public String getOperatorSite() {
        return operatorSite;
    }

    public void setOperatorSite(String operatorSite) {
        this.operatorSite = operatorSite;
    }

    public String getOperatorSiteId() {
        return operatorSiteId;
    }

    public void setOperatorSiteId(String operatorSiteId) {
        this.operatorSiteId = operatorSiteId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
