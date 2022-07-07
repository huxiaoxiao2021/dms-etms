package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 卸车单号扫描类型
 * @author fanggang7
 * @time 2022-07-06 15:12:06 周三
 **/
public enum UnloadScanTypeEnum {

    SCAN_ONE(0, "按件发货", "支持扫描包裹号或箱号"),
    SCAN_WAYBILL(2, "按单发货", "扫描包裹号转成运单号，或扫描运单号"),
    ;

    private Integer code;

    private String name;

    private String desc;

    UnloadScanTypeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static String getNameByCode(Integer code) {
        for (UnloadScanTypeEnum scanTypeEnum : UnloadScanTypeEnum.values()) {
            if (scanTypeEnum.code.equals(code)) {
                return scanTypeEnum.name;
            }
        }
        return "";
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
