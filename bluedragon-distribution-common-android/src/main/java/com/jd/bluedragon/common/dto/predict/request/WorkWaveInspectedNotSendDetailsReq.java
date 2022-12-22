package com.jd.bluedragon.common.dto.predict.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class WorkWaveInspectedNotSendDetailsReq implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 时间
     */
    private Date queryTime;
    /**
     * 当前场地
     */
    private Integer currentSiteCode;

    /**
     * 下游场地
     */
    private List<Integer> nextSiteCodes;

    /**
     * 产品类型
     * Luxury 奢侈品
     * EasyFrozen 易冻品
     * Fresh 生鲜
     * KA KA
     * Medicine 医药
     * Fast 特快送
     * None 其他
     *
     */
    private String productType;

    /**
     * 运单号
     * 查询当前波次已验未发的运单明细不需要
     * 查询当前波次已验未发的包裹明细需要
     */
    private String waybillCode;

    private Integer pageSize;
    private Integer pageNum;
    private User user;

    private CurrentOperate currentOperate;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }



    public Date getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Date queryTime) {
        this.queryTime = queryTime;
    }

    public Integer getCurrentSiteCode() {
        return currentSiteCode;
    }

    public void setCurrentSiteCode(Integer currentSiteCode) {
        this.currentSiteCode = currentSiteCode;
    }

    public List<Integer> getNextSiteCodes() {
        return nextSiteCodes;
    }

    public void setNextSiteCodes(List<Integer> nextSiteCodes) {
        this.nextSiteCodes = nextSiteCodes;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
