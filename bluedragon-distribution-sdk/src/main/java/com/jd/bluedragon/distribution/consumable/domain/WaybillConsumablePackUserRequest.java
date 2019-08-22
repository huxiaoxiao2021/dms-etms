package com.jd.bluedragon.distribution.consumable.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hanjiaxing1 on 2018/10/11.
 */
public class WaybillConsumablePackUserRequest implements Serializable{

    private static final long serialVersionUID = 1L;

    /*
     * 批量更新的id
     * */
    private List<Long> ids;

    /*
    * 批量更新运单号
    * */
    private List<String> waybillCodeList;

    /*
    * 更新人ERP
    * */
    private String packUserErp;

    public List<String> getWaybillCodeList() {
        return waybillCodeList;
    }

    public void setWaybillCodeList(List<String> waybillCodeList) {
        this.waybillCodeList = waybillCodeList;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getPackUserErp() {
        return packUserErp;
    }

    public void setPackUserErp(String packUserErp) {
        this.packUserErp = packUserErp;
    }
}
