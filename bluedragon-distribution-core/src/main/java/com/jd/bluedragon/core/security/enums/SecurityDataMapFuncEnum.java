package com.jd.bluedragon.core.security.enums;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security
 * @ClassName: SecurityCheckEnums
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/8/17 14:15
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum SecurityDataMapFuncEnum {

    WAYBILL_PRINT(SecurityDataTypeEnum.WAYBILL, SecurityFuncEnum.FUNC_PRINT, "打印获取运单信息"),

    // 其他待补充
    ;

    SecurityDataMapFuncEnum(SecurityDataTypeEnum securityDataTypeEnum, SecurityFuncEnum securityFuncEnum, String desc){
        this.securityDataTypeEnum = securityDataTypeEnum;
        this.securityFuncEnum = securityFuncEnum;
        this.desc = desc;
    }

    // 数据类型
    private SecurityDataTypeEnum securityDataTypeEnum;
    // 功能类型
    private SecurityFuncEnum securityFuncEnum;
    // 描述
    private String desc;

    public SecurityDataTypeEnum getSecurityDataTypeEnum() {
        return securityDataTypeEnum;
    }

    public void setSecurityDataTypeEnum(SecurityDataTypeEnum securityDataTypeEnum) {
        this.securityDataTypeEnum = securityDataTypeEnum;
    }

    public SecurityFuncEnum getSecurityFuncEnum() {
        return securityFuncEnum;
    }

    public void setSecurityFuncEnum(SecurityFuncEnum securityFuncEnum) {
        this.securityFuncEnum = securityFuncEnum;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
