package com.jd.bluedragon.common.dto.jyexpection.request;

import java.io.Serializable;

public class ExpBaseReq implements Serializable {

    // 用户ERP
    private String userErp;

    private Integer siteId;

    // 岗位码
    private String positionCode;

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }
}
