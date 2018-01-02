package com.jd.ql.dms.common.domain;

/**
 * 运输的车型信息
 * Created by xumei3 on 2017/12/28.
 */
public class BusType {
    /** 车型id **/
    private int busTypeId;
    /** 车型名称 **/
    private String busTypeName;

    public BusType(int busTypeId, String busTypeName){
        this.busTypeId = busTypeId;
        this.busTypeName = busTypeName;
    }

    public int getBusTypeId() {
        return busTypeId;
    }

    public void setBusTypeId(int busTypeId) {
        this.busTypeId = busTypeId;
    }

    public String getBusTypeName() {
        return busTypeName;
    }

    public void setBusTypeName(String busTypeName) {
        this.busTypeName = busTypeName;
    }
}
