package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

public class CarryDeparturePrintRequest implements Serializable {

    /*
    车牌号
     */
    private String carrierCode;
    private String handoverCode;
    private Date begSendCarTime;
    private Date endSendCarTIme;
    private String carLicense;
    private String startSiteCode;

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getHandoverCode() {
        return handoverCode;
    }

    public void setHandoverCode(String handoverCode) {
        this.handoverCode = handoverCode;
    }

    public Date getBegSendCarTime() {

        return begSendCarTime;
    }

    public void setBegSendCarTime(Date begSendCarTime) {
        this.begSendCarTime = begSendCarTime;
    }

    public Date getEndSendCarTIme() {
        return endSendCarTIme;
    }

    public void setEndSendCarTIme(Date endSendCarTIme) {
        this.endSendCarTIme = endSendCarTIme;
    }

    public String getCarLicense() {
        return carLicense;
    }

    public void setCarLicense(String carLicense) {
        this.carLicense = carLicense;
    }

    public String getStartSiteCode() {
        return startSiteCode;
    }

    public void setStartSiteCode(String startSiteCode) {
        this.startSiteCode = startSiteCode;
    }
}
