package com.jd.bluedragon.distribution.printOnline.domain;

import java.io.Serializable;
import java.util.Date;

public class PrintOnlineBoxDTO implements Serializable {

    private static final long serialVersionUID = 6086735593536156126L;

    private String boxCode;	//箱号
    private Integer waybillNum;	//订单数
    private Integer packageNum;	//实际包裹数
    private Double volume;	//体积
    private String sealCode1;	//封签号1
    private String sealCode2;	//封签号2
    private Date sealTime;	//锁时间

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getWaybillNum() {
        return waybillNum;
    }

    public void setWaybillNum(Integer waybillNum) {
        this.waybillNum = waybillNum;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getSealCode1() {
        return sealCode1;
    }

    public void setSealCode1(String sealCode1) {
        this.sealCode1 = sealCode1;
    }

    public String getSealCode2() {
        return sealCode2;
    }

    public void setSealCode2(String sealCode2) {
        this.sealCode2 = sealCode2;
    }

    public Date getSealTime() {
        return sealTime;
    }

    public void setSealTime(Date sealTime) {
        this.sealTime = sealTime;
    }
}
