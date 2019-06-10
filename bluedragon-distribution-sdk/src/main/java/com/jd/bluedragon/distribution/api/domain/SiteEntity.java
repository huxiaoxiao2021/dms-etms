package com.jd.bluedragon.distribution.api.domain;

public class SiteEntity {
    private String name;

    private Integer type;

    private Integer code;

    private Integer subType;

    public SiteEntity(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public SiteEntity(){

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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }
}
