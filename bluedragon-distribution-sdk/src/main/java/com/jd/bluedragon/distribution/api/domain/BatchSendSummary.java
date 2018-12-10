package com.jd.bluedragon.distribution.api.domain;

/**
 * Created by wuzuxiang on 2018/11/22.
 */
public class BatchSendSummary {

    private Integer createSiteCode;

    private Integer receiveSiteCode;//批次目的地

    private String sendCode;//批次号

    private Integer packageSum;//包裹总数量

    private Double volumeSum;//总体积

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getPackageSum() {
        return packageSum;
    }

    public void setPackageSum(Integer packageSum) {
        this.packageSum = packageSum;
    }

    public Double getVolumeSum() {
        return volumeSum;
    }

    public void setVolumeSum(Double volumeSum) {
        this.volumeSum = volumeSum;
    }


}
