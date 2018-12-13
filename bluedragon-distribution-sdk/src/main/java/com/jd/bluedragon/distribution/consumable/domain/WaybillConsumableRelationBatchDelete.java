package com.jd.bluedragon.distribution.consumable.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hanjiaxing1 on 2018/10/11.
 */
public class WaybillConsumableRelationBatchDelete implements Serializable{

    private static final long serialVersionUID = 1L;

    /*
    * 批量删除的id
    * */
    private List<Long> ids;

    /*
    * 数据属的运单号
    * */
    private String waybillCode;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
