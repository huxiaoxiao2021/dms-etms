package com.jd.bluedragon.common.dto.operation.workbench.enums;
/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum JyBizTaskExceptionTypeEnum {

    SANWU(0, "三无"),
    SCRAPPED(1, "报废"),
    //DAMAGED(2, "破损"),
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

    public static JyBizTaskExceptionTypeEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (JyBizTaskExceptionTypeEnum value : JyBizTaskExceptionTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
