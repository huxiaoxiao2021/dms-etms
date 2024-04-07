package com.jd.bluedragon.common.dto.operation.workbench.enums;
/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 陆庆林（luqinglin3）
 * @Date: 2022/4/6
 * @Description:
 */
public enum JyBizTaskExceptionCycleTypeEnum {

    UPLOAD(0, "上报"),
    DISTRIBUTION(1, "分配"),
    RECEIVE(2, "领取"),
    PROCESS_SUBMIT(3, "处理-提交"),
    PROCESS_APPROVE(8,"处理-审批"),
    PROCESS_CUSTOMER(9,"处理-客服介入"),
    PROCESSED(4, "处理成功"),
    UP_SHELF(5, "暂存上架"),
    DOWN_SHELF(6, "暂存上架"),
    CLOSE(7, "完成"),
    DAMAGE_FIX_SEND_FINISH(10,"修复下传"),
    DAMAGE_INNER_DIRECT_SEND_FINISH(11,"直接下传(内破)"),
    DAMAGE_CHANGE_WRAP_FINISH(12,"更换包装下传"),
    DAMAGE_REVERSE_RETURN_FINISH(13,"逆向退回"),
    DAMAGE_OUTER_DIRECT_SEND_FINISH(14,"直接下传(外破)"),
    SCRAPPED_APPROVAL_PASSED_FINISH(15,"报废处理审批通过"),
    INTERCEPT_DISPOSE_RECALL(-1,"撤销拦截"),
    INTERCEPT_DISPOSE_REPRINT(16,"补打完成"),
    INTERCEPT_DISPOSE_EXCHANGE_PRINT(17,"换单打印完成"),
    INTERCEPT_DISPOSE_UPLOAD_WEIGHT_VOLUME(18,"称重量方"),
    INTERCEPT_DISPOSE_UNPACK(19,"拆包"),
    INTERCEPT_DISPOSE_REVERSE_SEND(20,"逆向发货"),
    INTERCEPT_DISPOSE_REPRINT_NEW_WAYBILL(21,"换单后补打新单"),
    ;

    private Integer code;
    private String name;


    JyBizTaskExceptionCycleTypeEnum(Integer code, String name) {
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
