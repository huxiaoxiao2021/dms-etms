package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * 重量上传入参
 *
 * [{"packageBarcode":"VA00013129830-1-1-","againWeight":12.3,
 * "waybillCode":"VA00013129830","operatorUserId":10053,"operatorUser":"邢松",
 * "operatorSiteId":910,
 * "operatorSite":"北京马驹桥分拣中心"}]
 */
public class EditWeightRequest implements Serializable {

    private static final long serialVersionUID = 8464349458768408318L;


    private String packageBarcode;

    private Double againWeight;

    private String waybillCode;

    private Integer operatorUserId;

    private String operatorUser;

    private Integer operatorSiteId;

    private String operatorSite;

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public Double getAgainWeight() {
        return againWeight;
    }

    public void setAgainWeight(Double againWeight) {
        this.againWeight = againWeight;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getOperatorUserId() {
        return operatorUserId;
    }

    public void setOperatorUserId(Integer operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    public String getOperatorUser() {
        return operatorUser;
    }

    public void setOperatorUser(String operatorUser) {
        this.operatorUser = operatorUser;
    }

    public Integer getOperatorSiteId() {
        return operatorSiteId;
    }

    public void setOperatorSiteId(Integer operatorSiteId) {
        this.operatorSiteId = operatorSiteId;
    }

    public String getOperatorSite() {
        return operatorSite;
    }

    public void setOperatorSite(String operatorSite) {
        this.operatorSite = operatorSite;
    }
}
