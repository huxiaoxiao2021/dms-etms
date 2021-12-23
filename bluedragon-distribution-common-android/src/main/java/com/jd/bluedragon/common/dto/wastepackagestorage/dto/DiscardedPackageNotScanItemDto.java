package com.jd.bluedragon.common.dto.wastepackagestorage.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 未扫弃件包裹操作信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-01 21:27:56 周三
 */
public class DiscardedPackageNotScanItemDto implements Serializable {
    private static final long serialVersionUID = 8763317540772958472L;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 操作站点ID
     */
    private Integer operatorSiteId;

    /**
     * 操作站点名称
     */
    private String operatorSiteName;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 操作时间
     */
    private String operateTimeFormative;

    /**
     * 操作人ERP
     */
    private String operatorUserErp;

    /**
     * 操作人姓名
     */
    private String operatorUserName;

    /**
     * 操作节点
     */
    private String operateState;

    /**
     * 操作节点
     */
    private String operateStateName;

    public DiscardedPackageNotScanItemDto() {
    }

    public String getPackageCode() {
        return packageCode;
    }

    public DiscardedPackageNotScanItemDto setPackageCode(String packageCode) {
        this.packageCode = packageCode;
        return this;
    }

    public Integer getOperatorSiteId() {
        return operatorSiteId;
    }

    public DiscardedPackageNotScanItemDto setOperatorSiteId(Integer operatorSiteId) {
        this.operatorSiteId = operatorSiteId;
        return this;
    }

    public String getOperatorSiteName() {
        return operatorSiteName;
    }

    public DiscardedPackageNotScanItemDto setOperatorSiteName(String operatorSiteName) {
        this.operatorSiteName = operatorSiteName;
        return this;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public DiscardedPackageNotScanItemDto setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
        return this;
    }

    public String getOperatorUserErp() {
        return operatorUserErp;
    }

    public DiscardedPackageNotScanItemDto setOperatorUserErp(String operatorUserErp) {
        this.operatorUserErp = operatorUserErp;
        return this;
    }

    public String getOperatorUserName() {
        return operatorUserName;
    }

    public DiscardedPackageNotScanItemDto setOperatorUserName(String operatorUserName) {
        this.operatorUserName = operatorUserName;
        return this;
    }

    public String getOperateTimeFormative() {
        return operateTimeFormative;
    }

    public DiscardedPackageNotScanItemDto setOperateTimeFormative(String operateTimeFormative) {
        this.operateTimeFormative = operateTimeFormative;
        return this;
    }

    public String getOperateState() {
        return operateState;
    }

    public DiscardedPackageNotScanItemDto setOperateState(String operateState) {
        this.operateState = operateState;
        return this;
    }

    public String getOperateStateName() {
        return operateStateName;
    }

    public DiscardedPackageNotScanItemDto setOperateStateName(String operateStateName) {
        this.operateStateName = operateStateName;
        return this;
    }

    @Override
    public String toString() {
        return "DiscardedPackageNotScanItemDto{" +
                "packageCode='" + packageCode + '\'' +
                ", operatorSiteId=" + operatorSiteId +
                ", operatorSiteName='" + operatorSiteName + '\'' +
                ", operateTime=" + operateTime +
                ", operateTimeFormative='" + operateTimeFormative + '\'' +
                ", operatorUserErp='" + operatorUserErp + '\'' +
                ", operatorUserName='" + operatorUserName + '\'' +
                ", operateState='" + operateState + '\'' +
                ", operateStateName='" + operateStateName + '\'' +
                '}';
    }
}
