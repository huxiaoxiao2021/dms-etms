package com.jd.bluedragon.common.dto.seal.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import java.io.Serializable;

public class CheckTransportReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 7633234736944916205L;
    /**
     * 运力编码
     */
    private String transportCode;
    /**
     * 目的地id
     */
    private Integer endSiteId;
    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 中转属性开关
     * true: 开启中转属性确认校验
     * false: 不校验中转属性逻辑
     */
    private Boolean temporaryTransferSwitch;

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public Boolean getTemporaryTransferSwitch() {
        return temporaryTransferSwitch;
    }

    public void setTemporaryTransferSwitch(Boolean temporaryTransferSwitch) {
        this.temporaryTransferSwitch = temporaryTransferSwitch;
    }
}
