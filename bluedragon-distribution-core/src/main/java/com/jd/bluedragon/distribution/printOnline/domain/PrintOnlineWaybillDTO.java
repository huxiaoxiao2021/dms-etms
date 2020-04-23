package com.jd.bluedragon.distribution.printOnline.domain;

import java.io.Serializable;
import java.util.List;

public class PrintOnlineWaybillDTO implements Serializable{

    private static final long serialVersionUID = 4629211400291140296L;

    private String waybillCode;	//运单号

    private Integer packageNum;	//包裹数


    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }
}
