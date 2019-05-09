package com.jd.bluedragon.distribution.waybill.domain;

import java.util.List;

public class WaybillNoCollectionCondition {

    private String sendCode;

    private String boardCode;

    private String boxCode;

    private Integer createSiteCode;

    private int queryRange;

    private List<String> waybillCodeList;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public int getQueryRange() {
        return queryRange;
    }

    public void setQueryRange(int queryRange) {
        this.queryRange = queryRange;
    }

    public List<String> getWaybillCodeList() {
        return waybillCodeList;
    }

    public void setWaybillCodeList(List<String> waybillCodeList) {
        this.waybillCodeList = waybillCodeList;
    }
}
