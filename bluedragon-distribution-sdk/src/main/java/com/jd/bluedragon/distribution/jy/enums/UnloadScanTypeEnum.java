package com.jd.bluedragon.distribution.jy.enums;

/**
 * @program: ql-dms-distribution
 * @description:
 * @author: caozhixing3
 * @date: 2024-04-10 20:36
 **/
public enum UnloadScanTypeEnum {

    SCAN_ONE(0, "按件扫描", "支持扫描包裹号或箱号"),
    SCAN_WAYBILL(2, "按单扫描", "扫描包裹号转成运单号"),
    ;

    private Integer code;

    private String name;

    private String desc;

    UnloadScanTypeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    /**
     * 根据代码获取卸载扫描类型的名称
     * @param code 代码
     * @return 对应的卸载扫描类型名称
     * @throws NullPointerException 如果code为空时
     */
    public static UnloadScanTypeEnum getEnumByCode(Integer code) {
        for (UnloadScanTypeEnum scanTypeEnum : UnloadScanTypeEnum.values()) {
            if (scanTypeEnum.code.equals(code)) {
                return scanTypeEnum;
            }
        }
        return null;
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
