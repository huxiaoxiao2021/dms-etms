package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName TmsLineTypeEnum
 * @Description 运输线路类型
 * @Author wyh
 * @Date 2022/6/9 16:35
 **/
public enum TmsLineTypeEnum {

    TRUNK_LINE(1, "干线", JyLineTypeEnum.TRUNK_LINE),

    BRANCH_LINE(2, "支线", JyLineTypeEnum.BRANCH_LINE),

    TC_SHUTTLE(3, "TC摆渡", JyLineTypeEnum.OTHER),

    DOOR_TO_DOOR_DELIVERY(4, "上门提送", JyLineTypeEnum.OTHER),

    SHUTTLE(5, "摆渡", JyLineTypeEnum.SHUTTLE),

    RETURN_CITY_STATION(9, "市内传站返回", JyLineTypeEnum.TRANSFER),

    LONG_STATION(10, "长途传站", JyLineTypeEnum.TRANSFER),

    CITY_STATION(11, "市内传站", JyLineTypeEnum.TRANSFER),

    CITY_DELIVERY(17, "城配", JyLineTypeEnum.OTHER),

    BUSINESS_PICKUP(30, "商家取件", JyLineTypeEnum.OTHER),

    RETURN_WITH_GOODS(31, "带货返回", JyLineTypeEnum.OTHER),

    JINGZUNDA_DELIVERY(32, "京尊达配送", JyLineTypeEnum.OTHER),

    JISUDA_DELIVERY(33, "极速达配送", JyLineTypeEnum.OTHER),

    RETURN_LONG_STATION(34, "长途传站返回", JyLineTypeEnum.TRANSFER),

    SHORT_BARGE(36, "短驳", JyLineTypeEnum.OTHER);
    ;

    private Integer code;
    private String name;
    private JyLineTypeEnum type;
    TmsLineTypeEnum(Integer code, String name, JyLineTypeEnum type) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public static JyLineTypeEnum getLineType(Integer code) {
        for (TmsLineTypeEnum typeEnum : TmsLineTypeEnum.values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum.getType();
            }
        }
        return JyLineTypeEnum.OTHER;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public JyLineTypeEnum getType() {
        return type;
    }
}
