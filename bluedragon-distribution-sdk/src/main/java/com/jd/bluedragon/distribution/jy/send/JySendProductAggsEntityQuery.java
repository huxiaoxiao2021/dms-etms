package com.jd.bluedragon.distribution.jy.send;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 发货数据统计表
 *
 * @author chenyaguo
 * @date 2022-11-02 15:26:08
 */
public class JySendProductAggsEntityQuery implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 任务主键
     */
    private String bizId;

    /**
     * 操作场地ID
     */
    private Long operateSiteId;

    /**
     * 产品类型
     */
    private String productType;

    /**
     *  开始时间
     */
    private Date startTime;

    /**
     *  结束时间
     */
    private Date endTime;

    /**
     * 目的场地ids
     */
    private List<Long> endSiteIds;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Long operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<Long> getEndSiteIds() {
        return endSiteIds;
    }

    public void setEndSiteIds(List<Long> endSiteIds) {
        this.endSiteIds = endSiteIds;
    }
}
