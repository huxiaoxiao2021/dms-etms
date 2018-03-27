package com.jd.bluedragon.distribution.half.domain;

import java.util.Date;
import java.util.List;

/**
 * 包裹半收协商再投之运单状态
 */
public class PackageHalfRedeliveryDto {


    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * eclp反馈时间
     */
    private Date operateTime;

    /**
     * 操作站点ID
     */
    private Integer operateSiteId;

    /**
     * 操作站点名称
     */
    private String operateSiteName;

    /**
     * 操作人Id
     */
    private Integer operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 运单状态（妥投150拒收160再投待审核550再投审核完成560部分签收600）
     */
    private Integer waybillState;

    /**
     * 预计到达时间 格式：HH:mm
     */
    private List<PackageHalfRedeliveryDetailDto> packageSyncPartParameterList;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getWaybillState() {
        return waybillState;
    }

    public void setWaybillState(Integer waybillState) {
        this.waybillState = waybillState;
    }

    public List<PackageHalfRedeliveryDetailDto> getPackageSyncPartParameterList() {
        return packageSyncPartParameterList;
    }

    public void setPackageSyncPartParameterList(List<PackageHalfRedeliveryDetailDto> packageSyncPartParameterList) {
        this.packageSyncPartParameterList = packageSyncPartParameterList;
    }
}
