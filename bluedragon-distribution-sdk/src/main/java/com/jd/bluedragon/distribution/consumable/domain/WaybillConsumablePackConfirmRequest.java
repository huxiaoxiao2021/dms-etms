package com.jd.bluedragon.distribution.consumable.domain;


import com.jd.bluedragon.distribution.jy.dto.User;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date 20210617
 **/
public class WaybillConsumablePackConfirmRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务单号： 运单号或包裹号
     */
    private String businessCode;

    private User user;

    private Integer dmsId;

    private List<WaybillConsumablePdaRequest> waybillConsumablePdaRequestList;


    //---------------------get   set-------------------------------


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public Integer getDmsId() {
        return dmsId;
    }

    public void setDmsId(Integer dmsId) {
        this.dmsId = dmsId;
    }

    public List<WaybillConsumablePdaRequest> getWaybillConsumablePdaRequestList() {
        return waybillConsumablePdaRequestList;
    }

    public void setWaybillConsumablePdaRequestList(List<WaybillConsumablePdaRequest> waybillConsumablePdaRequestList) {
        this.waybillConsumablePdaRequestList = waybillConsumablePdaRequestList;
    }
}
