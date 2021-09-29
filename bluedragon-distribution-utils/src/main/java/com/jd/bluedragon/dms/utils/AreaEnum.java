package com.jd.bluedragon.dms.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 七大区域枚举类
 * Created by wuyoude on 2021/09/25
 */
public enum AreaEnum {

    //枚举值
    EAST_AREA(3, "华东区"),
    SOUTHWEST_AREA(4, "西南区"),
    NORTH_AREA(6, "华北区"),
    SOUTH_AREA(10, "华南区"),
    CENTRAL_AREA(600, "华中区"),
    NORTHEAST_AREA(611, "东北区"),
    NORTHWEST_AREA(645, "西北区");

    private Integer code;
    private String name;
    private static Map<Integer, AreaEnum> codeMap;
    private static List<AreaData> areaList;
    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, AreaEnum>();
        areaList = new ArrayList<AreaData>();
        for (AreaEnum areaEnum : AreaEnum.values()) {
            codeMap.put(areaEnum.getCode(), areaEnum);
            areaList.add(new AreaData(areaEnum.getCode(),areaEnum.getName()));
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 区域枚举
     */
    public static AreaEnum getAreaEnumByKey(Integer code) {
        return codeMap.get(code);
    }
    /**
     * 
     * @param code
     * @return
     */
    public static List<AreaData> toAreaDataList() {
        return areaList;
    }
    AreaEnum(Integer code, String name) {
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
