package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum JyBizTaskExceptionProcessStatusEnum {

    PENDING_ENTRY(0, "待录入"),
    WAITING_MATCH(1, "匹配中"),
    ON_SHELF(2, "上架"),
    DONE(3, "处理完成"),
    WAITING_PRINT(4, "待打印"),
    APPROVING(5, "审批中"),
    APPROVE_PASS(6, "审批通过"),
    APPROVE_REJECT(7, "审批驳回"),
    WAITER_INTERVENTION(8, "客服介入中")
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

    public static JyBizTaskExceptionProcessStatusEnum valueOf(int code){
        for (JyBizTaskExceptionProcessStatusEnum e:JyBizTaskExceptionProcessStatusEnum.values()){
            if (e.getCode() == code){
                return e;
            }
        }
        return null;
    }

}
