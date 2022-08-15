package com.jd.bluedragon.distribution.jy.enums;
/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum JyBizTaskExceptionProcessStatusEnum {

    PENDING_ENTRY(0, "待录入"),
    WAITING_MATCH(1, "待匹配"),
    ON_SHELF(2, "上架"),
    DONE(3, "处理完成"),
    ;

    private Integer code;
    private String name;


    JyBizTaskExceptionProcessStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
