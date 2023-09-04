package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import java.io.Serializable;

/**
 *  待派车列表请求体
 */
public class WaitingVehicleDistributionRequest implements Serializable {
    private static final long serialVersionUID = 7730552814347494070L;

    /**
     * 始发网点
     */
    private Integer siteCode;

    /**
     * 用户erp
     */
    private String userErp;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 当前页数
     */
    private Integer pageNumber;

    /**
     * 页大小
     */
    private Integer pageSize;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
