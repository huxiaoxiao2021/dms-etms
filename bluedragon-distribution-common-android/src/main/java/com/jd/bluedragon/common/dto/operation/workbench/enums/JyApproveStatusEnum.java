package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 审批状态枚举
 *
 * @author hujiping
 * @date 2023/3/14 3:05 PM
 */
public enum JyApproveStatusEnum {

    UNKNOWN(0, "未知"),
    TODO(1, "待审批"),
    PASS(2, "审批通过"),
    REJECT(3, "审批驳回"),
    ;


    private final int code;

    private final String desc;

    JyApproveStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static JyApproveStatusEnum valueOf(Integer code){
        for (JyApproveStatusEnum e:JyApproveStatusEnum.values()){
            if (code.equals(e.getCode())){
                return e;
            }
        }
        return null;
    }
}
