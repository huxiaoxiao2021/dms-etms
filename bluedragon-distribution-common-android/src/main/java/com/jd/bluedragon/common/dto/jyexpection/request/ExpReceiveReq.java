package com.jd.bluedragon.common.dto.jyexpection.request;

public class ExpReceiveReq extends ExpBaseReq {

    private String barCode;

    /**
     * 异常类型 0：三无 1：报废 2：破损
     */
    private Integer type;

    // 场地编码
    private Integer siteCode;


    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }
}
