package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * @author dudong
 * @date 2015/9/15
 */
public class AutoSortingPackageDto implements Serializable{
    private static final long serialVersionUID = 4694596991354445968L;
    /**主键ID*/
    private Integer id;
    /**运单号*/
    private String waybillCode;
    /**箱号*/
    private String boxCode;
    /**落格时间*/
    private String createTime;
    /**预分拣站点编码*/
    private String siteCode;
    /**操作人编号*/
    private Integer operatorID;
    /**操作人姓名*/
    private String operatorName;
    /**操作人erp*/
    private String operatorErp;
    /**分拣中心ID*/
    private Integer distributeID;
    /**分拣中心名称*/
    private String distributeName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(Integer operatorID) {
        this.operatorID = operatorID;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public Integer getDistributeID() {
        return distributeID;
    }

    public void setDistributeID(Integer distributeID) {
        this.distributeID = distributeID;
    }

    public String getDistributeName() {
        return distributeName;
    }

    public void setDistributeName(String distributeName) {
        this.distributeName = distributeName;
    }
}
