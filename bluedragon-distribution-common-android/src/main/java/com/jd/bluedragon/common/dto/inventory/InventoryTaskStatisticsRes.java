package com.jd.bluedragon.common.dto.inventory;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;


/**
 * 找货任务
 */
public class InventoryTaskStatisticsRes implements Serializable {

    private static final long serialVersionUID = -6051001368608203945L;

    /**
     * 总任务数
     */
    private Integer totalTaskNum;

    /**
     * 总任务数
     */
    private Integer totalPackageNum;


    public Integer getTotalTaskNum() {
        return totalTaskNum;
    }

    public void setTotalTaskNum(Integer totalTaskNum) {
        this.totalTaskNum = totalTaskNum;
    }

    public Integer getTotalPackageNum() {
        return totalPackageNum;
    }

    public void setTotalPackageNum(Integer totalPackageNum) {
        this.totalPackageNum = totalPackageNum;
    }
}
