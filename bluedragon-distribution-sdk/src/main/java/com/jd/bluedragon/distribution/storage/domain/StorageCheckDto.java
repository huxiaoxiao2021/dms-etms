package com.jd.bluedragon.distribution.storage.domain;

import java.io.Serializable;

/**
 * 暂存校验结果
 *
 * @author: hujiping
 * @date: 2020/5/7 16:09
 */
public class StorageCheckDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 暂存来源
     * @see com.jd.bluedragon.distribution.storage.domain.StorageSourceEnum
     * */
    private Integer storageSource;

    /**
     * 储位号
     * */
    private String storageCode;

    /**
     * 预计送达时间
     * */
    private String planDeliveryTime;

    public Integer getStorageSource() {
        return storageSource;
    }

    public void setStorageSource(Integer storageSource) {
        this.storageSource = storageSource;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }

    public String getPlanDeliveryTime() {
        return planDeliveryTime;
    }

    public void setPlanDeliveryTime(String planDeliveryTime) {
        this.planDeliveryTime = planDeliveryTime;
    }
}
