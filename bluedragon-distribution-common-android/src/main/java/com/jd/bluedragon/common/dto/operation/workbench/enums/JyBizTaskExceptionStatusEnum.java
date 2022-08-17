package com.jd.bluedragon.common.dto.operation.workbench.enums;
/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum JyBizTaskExceptionStatusEnum {

    PENDING(0, "待取件"),
    WAITING_PROCESS(1, "待处理"),
    WAITING_PRINT(2, "待打印"),
    DONE(3, "已完成"),
    ;

    private Integer code;
    private String name;


    JyBizTaskExceptionStatusEnum(Integer code, String name) {
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
