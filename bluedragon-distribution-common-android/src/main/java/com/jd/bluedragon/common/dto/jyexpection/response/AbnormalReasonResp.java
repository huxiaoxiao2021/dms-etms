package com.jd.bluedragon.common.dto.jyexpection.response;

public class AbnormalReasonResp {
    private String code;

    private String name;

    private String description;

    private String upperCode;

    private String upperId;

    private String reasonLevel;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpperCode() {
        return upperCode;
    }

    public void setUpperCode(String upperCode) {
        this.upperCode = upperCode;
    }

    public String getUpperId() {
        return upperId;
    }

    public void setUpperId(String upperId) {
        this.upperId = upperId;
    }

    public String getReasonLevel() {
        return reasonLevel;
    }

    public void setReasonLevel(String reasonLevel) {
        this.reasonLevel = reasonLevel;
    }
}
