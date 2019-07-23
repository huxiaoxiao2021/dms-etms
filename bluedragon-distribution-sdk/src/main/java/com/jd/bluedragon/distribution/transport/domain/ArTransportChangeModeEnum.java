package com.jd.bluedragon.distribution.transport.domain;

/**
 * @author lixin39
 * @Description 空铁运输类型变更枚举值
 * @ClassName ArTransportChangeModeEnum
 * @date 2019/7/10
 */
public enum ArTransportChangeModeEnum {

    /**
     * 航空转陆运
     */
    AIR_TO_ROAD_CODE(10, "航空转陆运", 10001),

    /**
     * 航空转铁路
     */
    AIR_TO_RAILWAY_CODE(20, "航空转铁路"),

    /**
     * 铁路转航空
     */
    RAILWAY_TO_AIR_CODE(30, "铁路转航空"),

    /**
     * 航空转高铁
     */
    AIR_TO_HIGH_SPEED_TRAIN_CODE(40, "航空转高铁", 40001),

    /**
     * 航空转普列
     */
    AIR_TO_ORDINARY_TRAIN_CODE(50, "航空转普列");

    private Integer code;

    private String desc;

    private Integer id;

    ArTransportChangeModeEnum() {
    }

    ArTransportChangeModeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    ArTransportChangeModeEnum(Integer code, String desc, Integer id) {
        this.code = code;
        this.desc = desc;
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getId() {
        return id;
    }

    /**
     * 根据编码值获取枚举类型
     *
     * @param code
     * @return
     */
    public static ArTransportChangeModeEnum getEnum(Integer code) {
        for (ArTransportChangeModeEnum changeMode : ArTransportChangeModeEnum.values()) {
            if (changeMode.getCode().equals(code)) {
                return changeMode;
            }
        }
        return null;
    }

}
