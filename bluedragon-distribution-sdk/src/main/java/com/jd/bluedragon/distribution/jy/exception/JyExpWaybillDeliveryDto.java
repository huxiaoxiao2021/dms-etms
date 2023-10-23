package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/8/10 13:46
 * @Description: 运单妥投DTO
 */
public class JyExpWaybillDeliveryDto implements Serializable {

    private String waybillCode;

    private String  opeUserId;

    private String  opeUserErp;


    private String  opeSiteId;
    private String  opeSiteName;

    private String  opeTime;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getOpeUserId() {
        return opeUserId;
    }

    public void setOpeUserId(String opeUserId) {
        this.opeUserId = opeUserId;
    }

    public String getOpeUserErp() {
        return opeUserErp;
    }

    public void setOpeUserErp(String opeUserErp) {
        this.opeUserErp = opeUserErp;
    }

    public String getOpeSiteId() {
        return opeSiteId;
    }

    public void setOpeSiteId(String opeSiteId) {
        this.opeSiteId = opeSiteId;
    }

    public String getOpeSiteName() {
        return opeSiteName;
    }

    public void setOpeSiteName(String opeSiteName) {
        this.opeSiteName = opeSiteName;
    }

    public String getOpeTime() {
        return opeTime;
    }

    public void setOpeTime(String opeTime) {
        this.opeTime = opeTime;
    }
}
