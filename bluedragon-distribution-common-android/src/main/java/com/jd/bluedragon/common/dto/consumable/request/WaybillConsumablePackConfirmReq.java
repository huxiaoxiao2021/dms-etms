package com.jd.bluedragon.common.dto.consumable.request;

import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date 20210617
 **/
public class WaybillConsumablePackConfirmReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务单号： 运单号或包裹号
     */
    private String businessCode;

    private User user;

    private Integer dmsId;

    private List<WaybillConsumablePdaDto> waybillConsumablePdaDtoList;


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

    public List<WaybillConsumablePdaDto> getWaybillConsumableDtoList() {
        return waybillConsumablePdaDtoList;
    }

    public void setWaybillConsumableDtoList(List<WaybillConsumablePdaDto> waybillConsumablePdaDtoList) {
        this.waybillConsumablePdaDtoList = waybillConsumablePdaDtoList;
    }
}
