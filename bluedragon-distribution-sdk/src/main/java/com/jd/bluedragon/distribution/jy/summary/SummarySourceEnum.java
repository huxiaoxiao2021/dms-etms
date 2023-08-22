package com.jd.bluedragon.distribution.jy.summary;

public enum SummarySourceEnum {
    /*todo 自行统计统计维度*/
    SEAL("seal", "封车场景"),
    ;

    private String code;

    private String desc;

    SummarySourceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
