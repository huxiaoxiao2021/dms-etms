package com.jd.bluedragon.core.security.dataam.enums;

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
public enum SecurityFuncEnum {

    FUNC_PRINT(1, "打印");

    SecurityFuncEnum(int code, String name){
        this.code = code;
        this.name = name;
    }

    private int code;
    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
