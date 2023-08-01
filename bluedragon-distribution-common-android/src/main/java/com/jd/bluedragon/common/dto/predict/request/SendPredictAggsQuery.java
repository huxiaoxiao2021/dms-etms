package com.jd.bluedragon.common.dto.predict.request;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/11 16:11
 * @Description:
 */
public class SendPredictAggsQuery implements Serializable {


    /**
     * 站点id
     */
   private Integer siteCode;

    /**
     * 1 当前波次 2 上波次
     */
   private Integer flag;

    /**
     * 产品类型
     */
    private String productType;

    private Integer pageNumber;

    private Integer pageSize;

    /**
     * 目的场地ids
     */
    private List<Long> endSiteIds;

    /**
     * 业务主键
     */
    private String sendVehicleBizId;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
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

    public List<Long> getEndSiteIds() {
        return endSiteIds;
    }

    public void setEndSiteIds(List<Long> endSiteIds) {
        this.endSiteIds = endSiteIds;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }
}
