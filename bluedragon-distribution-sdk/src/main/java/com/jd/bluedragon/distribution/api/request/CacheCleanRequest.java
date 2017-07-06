package com.jd.bluedragon.distribution.api.request;

/**
 * Created by zhoutao on 2017/6/13.
 */
public class CacheCleanRequest {


    private static final long serialVersionUID = 5799267676878153721L;

    /**
     * 中转场代码，比如010X
     */
    private String siteNo;

    /**
     * 分拣机代码
     */
    private String machineCode;

    /**
     * 滑槽号
     */
    private String chuteCode1;

    /**
     * 分页页数
     */
    private Integer pageNo;
    /**
     * 每页记录数
     */
    private Integer pageSize;


    public CacheCleanRequest() {
    }

    public CacheCleanRequest(String siteNo) {
        this.siteNo = siteNo;
    }

    public CacheCleanRequest(String siteNo, String machineCode, String chuteCode1) {
        this.siteNo = siteNo;
        this.machineCode = machineCode;
        this.chuteCode1 = chuteCode1;
    }








    public String getChuteCode1() {
        return chuteCode1;
    }

    public void setChuteCode1(String chuteCode1) {
        this.chuteCode1 = chuteCode1;
    }

    public String getSiteNo() {
        return siteNo;
    }

    public void setSiteNo(String siteNo) {
        this.siteNo = siteNo;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
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

