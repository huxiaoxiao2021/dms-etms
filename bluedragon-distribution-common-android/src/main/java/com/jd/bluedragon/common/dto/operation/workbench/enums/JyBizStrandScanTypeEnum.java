package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 拣运app-滞留上报扫货方式
 *
 * @author hujiping
 * @date 2023/3/28 11:07 AM
 */
public enum JyBizStrandScanTypeEnum {

    UNKNOWN(-1, "未知"),
    PACK(0, "按包裹"),
    WAYBILL(1, "按运单"),
    BOX(2, "按箱"),
    BOARD(3, "按板"),
    BATCH(4, "按批次")
    ;


    private final int code;

    private final String desc;

    JyBizStrandScanTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static JyBizStrandScanTypeEnum queryEnumByCode(Integer code) {
        for (JyBizStrandScanTypeEnum value : JyBizStrandScanTypeEnum.values()) {
            if(value.getCode() == code){
                return value;
            }
        }
        return UNKNOWN;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return desc;
    }
    
}
