package com.jd.bluedragon.distribution.jy.enums;

/**
 * @program: ql-dms-distribution
 * @description:
 * @author: caozhixing3
 * @date: 2024-04-10 20:36
 **/
public enum UnloadScanTypeEnum {
    /**
     * 按件扫描
     */
    SCAN_ONE(0, "按件扫描", "支持扫描包裹号或箱号"),
    /**
     * 按单扫描
     */
    SCAN_WAYBILL(2, "按单扫描", "扫描包裹号转成运单号"),
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
     * 构造函数
     * @param code
     * @param name
     * @param desc
     */
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

    /**
     * 获取代码值
     * @return code 代码值
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取名字
     * @return 名字
     */
    public String getName() {
        return name;
    }

    /**
     * 获取描述信息
     * @return desc 返回描述信息
     */
    public String getDesc() {
        return desc;
    }
}
