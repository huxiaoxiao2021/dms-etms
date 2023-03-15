package com.jd.bluedragon.common.dto.revokeException.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

/**
 * @author liwenji
 * @description 异常提报查询
 * @date 2023-01-04 14:19
 */
public class QueryExceptionReq extends BaseReq {
    
    private Integer currentPage;
    
    private Integer pageSize;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
