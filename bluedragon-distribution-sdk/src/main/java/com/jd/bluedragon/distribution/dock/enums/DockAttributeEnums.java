package com.jd.bluedragon.distribution.dock.enums;

import java.util.*;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.dock.enums
 * @ClassName: DockAttributeEnums
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/11/26 15:27
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum DockAttributeEnums {

    empty(0, ""),

    load(1, "装"),

    unload(2, "卸");


    private final Integer attr;

    private final String name;

    DockAttributeEnums(Integer attr, String name) {
        this.attr = attr;
        this.name = name;
    }

    public static List<Map<String,Object>> getAllAttributesMap() {
        List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
        for (DockAttributeEnums dockAttributeEnums : DockAttributeEnums.values()) {
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("attr", dockAttributeEnums.getAttr());
            map.put("name", dockAttributeEnums.getName());
            results.add(map);
        }
        return results;
    }

    public static DockAttributeEnums getEnumsByType(Integer type) {
        for (DockAttributeEnums dockAttributeEnums : DockAttributeEnums.values()) {
            if (dockAttributeEnums.getAttr().equals(type)) {
                return dockAttributeEnums;
            }
        }
        return DockAttributeEnums.empty;
    }

    public Integer getAttr() {
        return attr;
    }

    public String getName() {
        return name;
    }


}
