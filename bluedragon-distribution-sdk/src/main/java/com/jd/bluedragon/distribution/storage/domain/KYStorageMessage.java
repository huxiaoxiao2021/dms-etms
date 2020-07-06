package com.jd.bluedragon.distribution.storage.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 快运暂存对外MQ实体
 *
 * @author: hujiping
 * @date: 2020/5/7 23:07
 */
public class KYStorageMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     * */
    private String waybillCode;
    /**
     * 暂存状态 1、上架 2、下架
     * */
    private Integer storageStatus;
    /**
     * 操作人ERP
     * */
    private String operateErp;
    /**
     * 操作时间
     * */
    private Date operateTime;
    /**
     * 操作站点编码
     * */
    private Integer operateSiteCode;
    /**
     * 操作站点名称
     * */
    private String operateSiteName;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getStorageStatus() {
        return storageStatus;
    }

    public void setStorageStatus(Integer storageStatus) {
        this.storageStatus = storageStatus;
    }

    public String getOperateErp() {
        return operateErp;
    }

    public void setOperateErp(String operateErp) {
        this.operateErp = operateErp;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }
}
