package com.jd.bluedragon.distribution.jsf.domain;

import com.jd.bluedragon.distribution.open.entity.OperatorInfo;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/10/30 20:32
 * @Description:
 */
public class ReassignWaybillReq implements Serializable {

    /**
     * 操作人ERP
     */
    private String operateUserErp;

    private Integer operateUserCode;

    /**
     * 操作人Name
     */
    private String operateUserName;

    /**
     * 操作场地编码
     */
    private Integer operateSiteCode;

    /**
     * 操作场地编码
     */
    private String operateSiteName;


    /**
     * 返调度单号
     */
    private String barCode;



    /**
     * 返调度原因编码
     *  1：预分拣站点无法派送 2：特殊时期管制违禁品 3：邮政拒收 4：无预分拣站点
     */
    private Integer reasonType;

    /**
     * 原预分拣站点编码
     */
    private Integer oldSiteCode;

    /**
     * 原预分拣站点名称
     */
    private String oldSiteName;

    /**
     * 现场调度站点编码
     */
    private Integer siteOfSchedulingOnSiteCode;

    /**
     * 现场调度站点编码
     */
    private String siteOfSchedulingOnSiteName;


    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }



    public Integer getReasonType() {
        return reasonType;
    }

    public void setReasonType(Integer reasonType) {
        this.reasonType = reasonType;
    }



    public Integer getOldSiteCode() {
        return oldSiteCode;
    }

    public void setOldSiteCode(Integer oldSiteCode) {
        this.oldSiteCode = oldSiteCode;
    }

    public String getOldSiteName() {
        return oldSiteName;
    }

    public void setOldSiteName(String oldSiteName) {
        this.oldSiteName = oldSiteName;
    }

    public Integer getSiteOfSchedulingOnSiteCode() {
        return siteOfSchedulingOnSiteCode;
    }

    public void setSiteOfSchedulingOnSiteCode(Integer siteOfSchedulingOnSiteCode) {
        this.siteOfSchedulingOnSiteCode = siteOfSchedulingOnSiteCode;
    }

    public String getSiteOfSchedulingOnSiteName() {
        return siteOfSchedulingOnSiteName;
    }

    public void setSiteOfSchedulingOnSiteName(String siteOfSchedulingOnSiteName) {
        this.siteOfSchedulingOnSiteName = siteOfSchedulingOnSiteName;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Integer getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(Integer operateUserCode) {
        this.operateUserCode = operateUserCode;
    }
}
