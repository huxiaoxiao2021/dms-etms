package com.jd.bluedragon.distribution.waybill.domain;

public enum WaybillNoCollectionRangeEnum {

    ALL_RANGE(1, "查看全部运单"),
    B_RANGE(2, "只看B网面单");

    private Integer type;//操作类型
    private String name;//操作名称

    WaybillNoCollectionRangeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
