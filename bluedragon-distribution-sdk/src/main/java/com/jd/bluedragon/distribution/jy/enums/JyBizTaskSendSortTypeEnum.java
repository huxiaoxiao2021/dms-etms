package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName JyBizTaskSendSortTypeEnum
 * @Description 发车任务排序规则
 * @Author wyh
 * @Date 2022/5/29 15:55
 **/
public enum JyBizTaskSendSortTypeEnum {

    PLAN_DEPART_TIME(1, "预计发车时间"),
    SEAL_CAR_TIME(2, "封车时间"),
    ;

    private Integer code;
    private String name;

    JyBizTaskSendSortTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
