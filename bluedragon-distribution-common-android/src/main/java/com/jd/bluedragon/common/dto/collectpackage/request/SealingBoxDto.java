package com.jd.bluedragon.common.dto.collectpackage.request;

/**
 * @author liwenji
 * @description 
 * @date 2023-10-13 10:42
 */
public class SealingBoxDto {
    
    private String bizId;
    
    private String boxCode;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
}
