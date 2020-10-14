package com.jd.bluedragon.enums;

/**
 * 导出记录状态
 */
public enum ExportLogStateEnum {
    INIT(0,"初始化"),
    DOING(1,"执行中"),
    SUCCESS(2,"执行成功"),
    FAIL(3,"执行失败");
    /**
     * 值
     */
    private Integer value;

    /**
     * 名称
     */
    private String name;

    ExportLogStateEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
