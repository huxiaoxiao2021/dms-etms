package com.jd.bluedragon.core.security.log.enums;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log.domain
 * @ClassName: SecurityAccountEnums
 * @Description:
 * 账号类型；必填；
 *
 * 1：erp
 *
 * 2：passport（JDpin）
 *
 * 3：selfCreated（自建）
 *
 * 4: JSF调用者
 *
 * 5: 定时任务
 *
 * 6: 商家账号
 *
 * @Author： wuzuxiang
 * @CreateDate 2022/9/6 18:17
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum SecurityAccountEnums {

    account_type_1(1, "erp"),
    account_type_2(2, "passport（JDpin）"),
    account_type_3(3, "selfCreated（自建）"),
    account_type_4(4, "JSF调用者"),
    account_type_5(5, "定时任务"),
    account_type_6(6, "商家账号");

    private Integer type;

    private String mark;


    SecurityAccountEnums(Integer type, String mark) {
        this.type = type;
        this.mark = mark;
    }

    public Integer getType() {
        return type;
    }
}
