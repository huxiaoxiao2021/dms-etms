package com.jd.bluedragon.common.dto.waybill.request;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class WaybillTrackResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long packageStateId;
    private String packageBarcode;
    private String waybillCode;
    private Integer operatorUserId;
    private String operatorUser;
    private String operatorSite;
    private Integer operatorSiteId;
    private String state;
    private String remark;
    private Date createTime;
    private Integer yn;
    private String createTimeStr;
    private String operatorUserErp;
    private String stateName;
    private Integer category;
    private String categoryName;
    private Map<String, Object> extendParameter;

    public Long getPackageStateId() {
        return this.packageStateId;
    }

    public void setPackageStateId(Long packageStateId) {
        this.packageStateId = packageStateId;
    }

    public String getPackageBarcode() {
        return this.packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public String getWaybillCode() {
        return this.waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getOperatorUserId() {
        return this.operatorUserId;
    }

    public void setOperatorUserId(Integer operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    public String getOperatorUser() {
        return this.operatorUser;
    }

    public void setOperatorUser(String operatorUser) {
        this.operatorUser = operatorUser;
    }

    public String getOperatorSite() {
        return this.operatorSite;
    }

    public void setOperatorSite(String operatorSite) {
        this.operatorSite = operatorSite;
    }

    public Integer getOperatorSiteId() {
        return this.operatorSiteId;
    }

    public void setOperatorSiteId(Integer operatorSiteId) {
        this.operatorSiteId = operatorSiteId;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getYn() {
        return this.yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getCreateTimeStr() {
        return this.createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getOperatorUserErp() {
        return this.operatorUserErp;
    }

    public void setOperatorUserErp(String operatorUserErp) {
        this.operatorUserErp = operatorUserErp;
    }

    public String getStateName() {
        return this.stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Integer getCategory() {
        return this.category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Map<String, Object> getExtendParameter() {
        return this.extendParameter;
    }

    public void setExtendParameter(Map<String, Object> extendParameter) {
        this.extendParameter = extendParameter;
    }

}
