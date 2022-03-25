package com.jd.bluedragon.distribution.ministore.dto;

import java.util.List;

public class MiniStoreBindRelationEvent {
    private Integer eventType;
    private String storeCode;
    private String boxCode;
    private List<String> iceBoardCodes;
    private List<String> packageCodes;
    private String errMsg;
    private String createTime;

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public List<String> getIceBoardCodes() {
        return iceBoardCodes;
    }

    public void setIceBoardCodes(List<String> iceBoardCodes) {
        this.iceBoardCodes = iceBoardCodes;
    }

    public List<String> getPackageCodes() {
        return packageCodes;
    }

    public void setPackageCodes(List<String> packageCodes) {
        this.packageCodes = packageCodes;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
