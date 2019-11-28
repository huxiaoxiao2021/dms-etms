package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import java.io.Serializable;
import java.util.Date;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/19 13:08
 */
public class WaybillFlowDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总重量
     * */
    private Double totalWeight;
    /**
     * 总体积
     * */
    private Double totalVolume;
    /**
     * 操作人id
     * */
    private String operateId;
    /**
     * 操作人erp
     * */
    private String operateErp;
    /**
     * 操作站点
     * */
    private Integer operateSiteCode;
    /**
     * 操作站点名称
     * */
    private String operateSiteName;
    /**
     * 是否信任商家
     * */
    private Boolean isTrustBusi = Boolean.FALSE;
    /**
     * 操作时间
     * */
    private Date operateTime;

    public Boolean getTrustBusi() {
        return isTrustBusi;
    }

    public void setTrustBusi(Boolean trustBusi) {
        isTrustBusi = trustBusi;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public String getOperateId() {
        return operateId;
    }

    public void setOperateId(String operateId) {
        this.operateId = operateId;
    }

    public String getOperateErp() {
        return operateErp;
    }

    public void setOperateErp(String operateErp) {
        this.operateErp = operateErp;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
