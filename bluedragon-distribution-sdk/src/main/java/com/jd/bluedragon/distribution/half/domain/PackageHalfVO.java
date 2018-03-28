package com.jd.bluedragon.distribution.half.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PackageHalfVO implements Serializable{

    private static final long serialVersionUID = 1L;

    private String waybillCode ;

    private Integer waybillOpeType;

    private List<PackageHalfDetailVO> packageList;

    private String halfType; //包裹半收类型  /** 半收类型（1-包裹半收，2-明细半收） */

    private Date operateTime;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public List<PackageHalfDetailVO> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<PackageHalfDetailVO> packageList) {
        this.packageList = packageList;
    }

    public String getHalfType() {
        return halfType;
    }

    public void setHalfType(String halfType) {
        this.halfType = halfType;
    }

    public Integer getWaybillOpeType() {
        return waybillOpeType;
    }

    public void setWaybillOpeType(Integer waybillOpeType) {
        this.waybillOpeType = waybillOpeType;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
