package com.jd.bluedragon.core.jmq.domain;


/**
 * 运单预分拣站点变更MQ实体
 * Created by shipeilin on 2018/1/30.
 */
public class SiteChangeMqDto {
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 新站点Id
     */
    private Integer newSiteId;
    /**
     * 新站点名称
     */
    private String newSiteName;
    /**
     * 新站点路区编号
     */
    private String newSiteRoadCode;
    /**
     * 操作人ID
     */
    private Integer operatorId;
    /**
     * 操作人名称
     */
    private String operatorName;
    /**
     * 操作单位ID
     */
    private Integer operatorSiteId;
    /**
     * 操作人站点名称
     */
    private String operatorSiteName;
    /**
     * 操作时间：yyyy-MM-dd HH:mm:ss格式的时间串
     */
    private String operateTime;

    public Integer getNewSiteId() {
        return newSiteId;
    }

    public void setNewSiteId(Integer newSiteId) {
        this.newSiteId = newSiteId;
    }

    public String getNewSiteName() {
        return newSiteName;
    }

    public void setNewSiteName(String newSiteName) {
        this.newSiteName = newSiteName;
    }

    public String getNewSiteRoadCode() {
        return newSiteRoadCode;
    }

    public void setNewSiteRoadCode(String newSiteRoadCode) {
        this.newSiteRoadCode = newSiteRoadCode;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
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

    public Integer getOperatorSiteId() {
        return operatorSiteId;
    }

    public void setOperatorSiteId(Integer operatorSiteId) {
        this.operatorSiteId = operatorSiteId;
    }

    public String getOperatorSiteName() {
        return operatorSiteName;
    }

    public void setOperatorSiteName(String operatorSiteName) {
        this.operatorSiteName = operatorSiteName;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
