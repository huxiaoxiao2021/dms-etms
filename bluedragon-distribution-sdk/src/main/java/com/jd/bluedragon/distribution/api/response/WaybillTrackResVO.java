package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 查询运单全程跟踪返回体
 *
 * @author hujiping
 * @date 2023/3/8 2:41 PM
 */
public class WaybillTrackResVO implements Serializable {

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
        return packageStateId;
    }

    public void setPackageStateId(Long packageStateId) {
        this.packageStateId = packageStateId;
    }

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

    public String getOperatorSite() {
        return operatorSite;
    }

    public void setOperatorSite(String operatorSite) {
        this.operatorSite = operatorSite;
    }

    public Integer getOperatorSiteId() {
        return operatorSiteId;
    }

    public void setOperatorSiteId(Integer operatorSiteId) {
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getOperatorUserErp() {
        return operatorUserErp;
    }

    public void setOperatorUserErp(String operatorUserErp) {
        this.operatorUserErp = operatorUserErp;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Map<String, Object> getExtendParameter() {
        return extendParameter;
    }

    public void setExtendParameter(Map<String, Object> extendParameter) {
        this.extendParameter = extendParameter;
    }
}
