package com.jd.bluedragon.common.dto.operation.workbench.evaluate.response;

public enum JyEvaluateDimensionEnum {
    DIMENSION_100(100, "码货砌墙"),
    DIMENSION_200(200, "小件未集包"),
    DIMENSION_300(300, "生鲜/普货未分隔"),
    DIMENSION_400(400, "串点/经停包裹未隔离"),
    DIMENSION_500(500, "酒水置顶"),
    DIMENSION_600(600, "包裹凌乱倾倒"),
    DIMENSION_700(700, "集装容器使用不规范"),
    DIMENSION_800(800, "大压小/重压轻/木压纸"),
    DIMENSION_900(900, "特快件未码放车尾"),
    DIMENSION_1000(1000, "开门掉货"),
    DIMENSION_1100(1100, "其他"),
    ;
    private Integer code;
    private String name;
    JyEvaluateDimensionEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
