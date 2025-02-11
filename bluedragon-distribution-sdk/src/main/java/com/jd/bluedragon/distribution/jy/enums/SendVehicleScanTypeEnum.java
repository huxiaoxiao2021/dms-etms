package com.jd.bluedragon.distribution.jy.enums;

/**
 * @program: ql-dms-distribution
 * @description:
 * @author: caozhixing3
 * @date: 2024-04-10 20:37
 **/
public enum SendVehicleScanTypeEnum {
    /**
     * 按件扫描
     */
    SCAN_ONE(1, "按件扫描", "支持扫描包裹号或箱号"),
    /**
     * 按单扫描
     */
    SCAN_WAYBILL(2, "按单扫描", "扫描包裹号转成运单号，或扫描运单号"),
    /**
     * 扫描板号
     */
    SCAN_BOARD(4, "板号", "扫描包裹号转成板号，或扫描板号"),
    ;
    /**
     * 编码
     */
    private Integer code;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String desc;

    /**
     * 构造函数用于初始化SendVehicleScanTypeEnum对象
     * @param code 代码值
     * @param name 名称
     * @param desc 描述
     */
    SendVehicleScanTypeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }


    /**
     * 根据代码获取枚举类型
     * @param code 代码值
     * @return 对应的枚举类型，如果未找到则返回null
     */
    public static SendVehicleScanTypeEnum getEnumByCode(Integer code) {
        for (SendVehicleScanTypeEnum scanTypeEnum : SendVehicleScanTypeEnum.values()) {
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
