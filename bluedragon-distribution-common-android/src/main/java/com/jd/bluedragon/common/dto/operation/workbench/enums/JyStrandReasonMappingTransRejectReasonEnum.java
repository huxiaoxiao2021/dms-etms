package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 拣运滞留原因匹配运输驳回原因枚举
 *  @see 运输新增驳回原因需在此枚举中添加(code编码以2XX开头)
 *
 * @author hujiping
 * @date 2023/3/28 11:07 AM
 */
public enum JyStrandReasonMappingTransRejectReasonEnum {

    UNKNOWN(-1, "未知", -1),
    TRANS_ABOLISH(201, "作废", 100),
    TRANS_STORE(202, "囤货", 200),
    TRANS_REJECT(203, "驳回", 300),
    TRANS_REPORT_UNTIMELY(204, "提报不及时", 9000),
    TRANS_OVER_TIME_UN_DISPATCH(205, "超时未调度", 10000),
    TRANS_OVER_TIME_UN_SEND_CAR(206, "超时未派车", 20000),
    ;

    private final int code;

    private final String desc;

    private final int transportRejectCode;

    JyStrandReasonMappingTransRejectReasonEnum(int code, String desc, int transportRejectCode) {
        this.code = code;
        this.desc = desc;
        this.transportRejectCode = transportRejectCode;
    }
    
    public static JyStrandReasonMappingTransRejectReasonEnum queryEnumByRejectCode(Integer transportRejectCode) {
        for (JyStrandReasonMappingTransRejectReasonEnum value : JyStrandReasonMappingTransRejectReasonEnum.values()) {
            if(value.getTransportRejectCode() == transportRejectCode){
                return value;
            }
        }
        return UNKNOWN;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public int getTransportRejectCode() {
        return transportRejectCode;
    }
}
