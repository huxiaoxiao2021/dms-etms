package com.jd.bluedragon.core.security.log.enums;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log.domain
 * @ClassName: SecurityLogRespInfoStatusEnums
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/7 14:54
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum SecurityLogRespInfoStatusEnums {

    /**
     * 成功，出参有返回记录 ，uniqueIdentifier有记录上报
     */
    SUCCESS(0),

    /**
     * 成功，uniqueIdentifier不上报（出参返回记录为0或批量查询或批量导出时适用）
     */
    SUCCESS_NO_DATE(1),

    /**
     * 失败
     */
    FAIL(-1);

    private final int code;

    SecurityLogRespInfoStatusEnums(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
