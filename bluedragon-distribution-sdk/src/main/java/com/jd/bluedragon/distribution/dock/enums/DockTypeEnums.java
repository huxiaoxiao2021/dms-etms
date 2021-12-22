package com.jd.bluedragon.distribution.dock.enums;

import java.util.*;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.dock.enums
 * @ClassName: DockTypeEnums
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/11/26 15:18
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public enum DockTypeEnums {

    empty(0, ""),
    artery(1, "长途"),
    branch(2, "短途"),
    passOn(4, "传站"),
    ferry( 8,"摆渡");


    private final Integer type;

    private final String name;

    DockTypeEnums(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static List<Map<String,Object>> getAllTypesMap() {
        List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
        for (DockTypeEnums dockTypeEnums : DockTypeEnums.values()) {
            if (empty.equals(dockTypeEnums)) {
                continue;
            }
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("type", dockTypeEnums.getType());
            map.put("name", dockTypeEnums.getName());
            results.add(map);
        }
        return results;
    }

    public static DockTypeEnums getEnumsByType(Integer type) {
        for (DockTypeEnums dockTypeEnums : DockTypeEnums.values()) {
            if (dockTypeEnums.getType().equals(type)) {
                return dockTypeEnums;
            }
        }
        return DockTypeEnums.empty;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
