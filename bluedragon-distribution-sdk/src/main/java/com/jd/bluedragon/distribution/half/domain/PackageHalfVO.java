package com.jd.bluedragon.distribution.half.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PackageHalfVO implements Serializable{

    private static final long serialVersionUID = 1L;

    private String waybillCode ;

    private Integer waybillOpeType;

    private Integer rejectPackageCount; //拒收包裹数量

    private List<PackageHalfDetailVO> packageList;

    private String halfType; //包裹半收类型  /** 半收类型（1-包裹半收，2-明细半收） */

    private String operateTime;

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
        Date date = new Date();
        SimpleDateFormat sdm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdm.parse(operateTime);
        } catch (ParseException e) {

        }
        return date;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getRejectPackageCount() {
        return rejectPackageCount==null?Integer.valueOf(0):rejectPackageCount;
    }

    public void setRejectPackageCount(Integer rejectPackageCount) {
        this.rejectPackageCount = rejectPackageCount;
    }
}
