package com.jd.bluedragon.distribution.qualityControl;

/**
 * 异常菜单来源
 */
public enum AbnormalBizSourceEnum {

    ABNORMAL_HANDLE(1, "异常处理"),

    ABNORMAL_REPORT_H5(2, "异常提报(新)"),

    ABNORMAL_OUT_CALL(3, "异常外呼");

    private Integer type;

    private String name;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    AbnormalBizSourceEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static AbnormalBizSourceEnum getEnum(Integer type) {
        for (AbnormalBizSourceEnum status : AbnormalBizSourceEnum.values()) {
            if (status.getType().equals(type)) {
                return status;
            }
        }
        return null;
    }
}
