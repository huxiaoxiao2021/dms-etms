package com.jd.bluedragon.common.dto.operation.workbench.enums;
/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum JyBizTaskExceptionSourceEnum {

    COMMON(0, "通用入口"),
    UN_LOAD(1, "卸车入口"),
    SEND(2, "发货入口")
    ;

    private Integer code;
    private String name;


    JyBizTaskExceptionSourceEnum(Integer code, String name) {
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
