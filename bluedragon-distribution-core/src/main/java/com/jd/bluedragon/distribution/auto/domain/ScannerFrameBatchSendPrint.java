package com.jd.bluedragon.distribution.auto.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wuzuxiang on 2016/12/26.
 */
public class ScannerFrameBatchSendPrint implements Serializable{

    private static final long serialVersionUID = 147883762545714549L;

    private Integer machineId;
    private Integer createSiteCode;
    private String createSiteName;
    private Integer receiveSiteCode;
    private String receiveSiteName;
    private Integer packageSum;
    private Date createTime;

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
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

    public Integer getPackageSum() {
        return packageSum;
    }

    public void setPackageSum(Integer packageSum) {
        this.packageSum = packageSum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
