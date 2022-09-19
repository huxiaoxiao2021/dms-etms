package com.jd.bluedragon.common.dto.jyexpection.request;

import java.util.List;

public class ExpUploadScanReq extends ExpBaseReq {

    private String barCode;

    private Integer source;

    private String bizId;

    // 近期扫描的 包裹号
    private List<String> recentPackageCodeList;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public List<String> getRecentPackageCodeList() {
        return recentPackageCodeList;
    }

    public void setRecentPackageCodeList(List<String> recentPackageCodeList) {
        this.recentPackageCodeList = recentPackageCodeList;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
