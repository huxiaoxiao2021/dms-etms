package com.jd.bluedragon.distribution.jy.config;

import java.util.Date;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.jy.config
 * @ClassName: JYTransferSiteEntity
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/18 20:57
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class JYTransferSiteEntity {

    private String waybillCode;

    private String waybillSign;

    private Integer operateSiteCode;

    private String operateSiteName;

    private Integer receiveSiteCode;

    private String receiveSiteName;

    private Integer receiveSiteType;

    private Integer preSiteCode;

    private String preSiteName;

    private Date operateTime;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
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

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getReceiveSiteType() {
        return receiveSiteType;
    }

    public void setReceiveSiteType(Integer receiveSiteType) {
        this.receiveSiteType = receiveSiteType;
    }

    public Integer getPreSiteCode() {
        return preSiteCode;
    }

    public void setPreSiteCode(Integer preSiteCode) {
        this.preSiteCode = preSiteCode;
    }

    public String getPreSiteName() {
        return preSiteName;
    }

    public void setPreSiteName(String preSiteName) {
        this.preSiteName = preSiteName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
