package com.jd.bluedragon.distribution.jsf.domain;

/**
 * 分拣验证对象
 * Created by wangtingwei on 2015/3/16.
 */
public class SortingCheck {
    /**
     * 操作类型
     */
    Integer operateType = 1;

    /**
     * 包裹号
     */
    String packageCode;
    /**
     * 分拣中心编码
     */
    Integer createSiteCode;
    /**
     * 分拣中心名称
     */
    String createSiteName;
    /**
     * 操作人编码
     */
    Integer operateUserCode;
    /**
     * 操作人名称
     */
    String operateUserName;
    /**
     * 操作时间
     */
    String operateTime;
    /**
     * 收货站点
     */
    Integer receiveSiteCode;
    /**
     * 业务类型
     */
    Integer businessType;
    /**
     * 箱号
     */
    String boxCode;
    /**
     * 是否报丢 1报丢
     */
    Integer isLoss;

    Integer bizSourceType;
    /**
     * 业务操作节点，区分是分拣、发货等其他节点
     */
    Integer operateNode;

    /**
     * 在线状态，1-在线，2-离线
     */
    Integer onlineStatus;

    /**
     * 忽略验证的条件
     */
    private ValidateIgnore validateIgnore;

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(Integer operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getIsLoss() {
        return isLoss;
    }

    public void setIsLoss(Integer isLoss) {
        this.isLoss = isLoss;
    }

    public Integer getOperateNode() {
        return operateNode;
    }

    public void setOperateNode(Integer operateNode) {
        this.operateNode = operateNode;
    }

    public Integer getBizSourceType() {
        return bizSourceType;
    }

    public void setBizSourceType(Integer bizSourceType) {
        this.bizSourceType = bizSourceType;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public ValidateIgnore getValidateIgnore() {
        return validateIgnore;
    }

    public void setValidateIgnore(ValidateIgnore validateIgnore) {
        this.validateIgnore = validateIgnore;
    }
}
