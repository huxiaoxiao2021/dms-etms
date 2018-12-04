package com.jd.bluedragon.distribution.signAndReturn.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: MergedWaybill
 * @Description: 合单的旧单号信息
 * @author: hujiping
 * @date: 2018/11/29 17:43
 */
public class MergedWaybill implements Serializable {

    /**
     * 主键id
     * */
    private Long id;
    /**
     * 外键主表ID
     * */
    private Long signReturnPrintMId;
    /**
     * 旧运单号
     * */
    private String waybillCode;
    /**
     * 旧单号妥投时间
     * */
    private Date deliveredTime;
    /**
     * 是否删除 0-已删除，1-未删除
     * */
    private Integer isDelete;

    public Long getSignReturnPrintMId() {
        return signReturnPrintMId;
    }

    public void setSignReturnPrintMId(Long signReturnPrintMId) {
        this.signReturnPrintMId = signReturnPrintMId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Date getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(Date deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}
