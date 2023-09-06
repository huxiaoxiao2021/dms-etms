package com.jd.bluedragon.distribution.jy.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 发货车辆状态标签
 */
public enum JyBizDriverTagEnum {

    DRIVER_TIMEOUT(1, "driverTimeOut", 6),
    SCAN_DOCK(2, "scanDock", 5),
    LEAVE_TIMEOUT(3, "leaveTimeOut", 7),
    ;

    private Integer tag;
    private String driverNodeCode;
    private Integer vehicleLabelOption;

    private static Map<Integer, JyBizDriverTagEnum> tagToVehicleTagEnumMap;

    private static Map<String, JyBizDriverTagEnum> nodeCodeToVehicleTagEnumMap;


    static {
        tagToVehicleTagEnumMap = new HashMap<Integer, JyBizDriverTagEnum>();
        nodeCodeToVehicleTagEnumMap = new HashMap<String, JyBizDriverTagEnum>();
        for (JyBizDriverTagEnum tagEnum : JyBizDriverTagEnum.values()) {
            tagToVehicleTagEnumMap.put(tagEnum.tag, tagEnum);
            nodeCodeToVehicleTagEnumMap.put(tagEnum.driverNodeCode, tagEnum);
        }
    }

    JyBizDriverTagEnum(Integer tag, String driverNodeCode, Integer vehicleLabelOption) {
        this.tag = tag;
        this.driverNodeCode = driverNodeCode;
        this.vehicleLabelOption = vehicleLabelOption;
    }

    public Integer getTag() {
        return tag;
    }

    public String getDriverNodeCode() {
        return driverNodeCode;
    }

    public Integer getVehicleLabelOption() {
        return vehicleLabelOption;
    }

    public static JyBizDriverTagEnum getTagEnumByTag(Integer tag) {
        return tagToVehicleTagEnumMap.get(tag);
    }

    public static JyBizDriverTagEnum getTagEnumByNodeCode(String driverNodeCode) {
        return nodeCodeToVehicleTagEnumMap.get(driverNodeCode);
    }
}
