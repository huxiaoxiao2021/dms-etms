package com.jd.bluedragon.distribution.api.request;

import java.util.List;

public class SendCodeExceptionRequest {

    /**
     * 单号
     */
    private String barCode;

    /**
     * 类型 ：1全部；2已操作；3未操作
     */
    private Integer type;

    /**
     * 场地编号
     */
    private Integer siteCode;

    /**
     * 批次号列表
     */
    private List<String> sendCodes;

    private Integer pageNo;

    private Integer pageSize;

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

    public List<String> getSendCodes() {
        return sendCodes;
    }

    public void setSendCodes(List<String> sendCodes) {
        this.sendCodes = sendCodes;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
