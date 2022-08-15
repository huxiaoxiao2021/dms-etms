package com.jd.bluedragon.distribution.jy.enums;
/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum JyBizTaskExceptionTypeEnum {

    SANWU(0, "三无"),
    DAMAGED(1, "破损"),
    SCRAPPED(2, "报废"),
    ;

    private Integer code;
    private String name;


    JyBizTaskExceptionTypeEnum(Integer code, String name) {
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
