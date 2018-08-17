package com.jd.bluedragon.distribution.packingconsumable.domain;

import java.util.List;

/**
 * Created by hanjiaxing1 on 2018/8/10.
 */
public class DmsPackingConsumableInfo {

    /*快运或分拣中心id*/
    private Integer dmsId;

    /*是否支持包装耗材 0：不支持 1：支持*/
    private Integer supportStatus;

    /*包装耗材列表*/
    private List<PackingConsumableBaseInfo> PackingConsumableBaseInfoList;

    public Integer getDmsId() {
        return dmsId;
    }

    public void setDmsId(Integer dmsId) {
        this.dmsId = dmsId;
    }

    public Integer getSupportStatus() {
        return supportStatus;
    }

    public void setSupportStatus(Integer supportStatus) {
        this.supportStatus = supportStatus;
    }

    public List<PackingConsumableBaseInfo> getPackingConsumableBaseInfoList() {
        return PackingConsumableBaseInfoList;
    }

    public void setPackingConsumableBaseInfoList(List<PackingConsumableBaseInfo> packingConsumableBaseInfoList) {
        PackingConsumableBaseInfoList = packingConsumableBaseInfoList;
    }
}
