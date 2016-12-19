package com.jd.bluedragon.distribution.gantry.domain;

/**
 * Created by wuzuxiang on 2016/12/15.
 */
public class GantryBatchSendResult {

    private String receiveSiteName;//批次目的地

    private String sendCode;//批次号

    private Integer packageSum;//包裹总数量

    private Double volumeSum;//总体积

    private String createTime;//批次创建时间

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
