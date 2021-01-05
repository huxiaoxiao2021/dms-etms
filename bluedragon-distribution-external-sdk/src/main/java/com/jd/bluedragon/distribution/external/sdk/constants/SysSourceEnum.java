package com.jd.bluedragon.distribution.external.sdk.constants;

/**
 * @ClassName SysSourceEnum
 * @Description 系统请求来源枚举
 * @Author wyh
 * @Date 2021/1/4 16:49
 **/
public enum SysSourceEnum {

    SYS_REVERSE(1, "集约组（原退货组）")

    ;

    private Integer sysCode;

    private String sysName;

    public Integer getSysCode() {
        return sysCode;
    }

    public String getSysName() {
        return sysName;
    }

    SysSourceEnum(Integer sysCode, String sysName) {
        this.sysCode = sysCode;
        this.sysName = sysName;
    }

    @Override
    public String toString() {
        return "SysSourceEnum{" +
                "sysCode=" + sysCode +
                ", sysName='" + sysName + '\'' +
                '}';
    }

    public SysSourceEnum getEnum(Integer sysCode) {
        for (SysSourceEnum value : SysSourceEnum.values()) {
            if (value.sysCode.equals(sysCode)) {
                return value;
            }
        }
        return null;
    }

    public Boolean sysExist(Integer sysCode) {
        return null != getEnum(sysCode);
    }
}
