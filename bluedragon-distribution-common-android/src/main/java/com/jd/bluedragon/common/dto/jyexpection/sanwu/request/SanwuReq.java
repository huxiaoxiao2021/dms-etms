package com.jd.bluedragon.common.dto.jyexpection.sanwu.request;

import java.io.Serializable;

public class SanwuReq implements Serializable {

    // 用户ERP
    private String userErp;

    // 岗位码
    private String positionCode;

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }
}
