package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import java.io.Serializable;
import java.util.List;

/**
 *  待派车列表请求体
 */
public class WaitingVehicleDistributionRequest implements Serializable {
    private static final long serialVersionUID = 7730552814347494070L;

    /**
     * 始发网点
     */
    private Integer sourceSiteCode;

    /**
     * 目的网点
     */
    private Integer destSiteCode;

    /**
     * 用户erp
     */
    private String userErp;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 待派车任务状态
     * @see com.jd.bluedragon.common.dto.operation.workbench.enums.TmsDistributeVehicleStatusEnum
     */
    private List<Integer> statusList;

    /**
     * 当前页数
     */
    private Integer pageNumber;

    /**
     * 页大小
     */
    private Integer pageSize;

    public Integer getSourceSiteCode() {
        return sourceSiteCode;
    }

    public void setSourceSiteCode(Integer sourceSiteCode) {
        this.sourceSiteCode = sourceSiteCode;
    }

    public Integer getDestSiteCode() {
        return destSiteCode;
    }

    public void setDestSiteCode(Integer destSiteCode) {
        this.destSiteCode = destSiteCode;
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

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
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
