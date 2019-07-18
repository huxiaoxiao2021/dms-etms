package com.jd.bluedragon.distribution.alliance;

import java.util.HashMap;
import java.util.Map;

public enum AllianceBusiOpeTypeEnum {
    SORTING_RECEIVE(1,"分拣揽收"),
    SITE_RECEIVE(2,"站点揽收"),
    SORTING_SEND(3,"分拣派送"),
    SITE_SEND(4,"站点派送");

    private int code;
    private String name;

    private static Map<Integer, AllianceBusiOpeTypeEnum> codeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, AllianceBusiOpeTypeEnum>();

        for (AllianceBusiOpeTypeEnum _enum : AllianceBusiOpeTypeEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static AllianceBusiOpeTypeEnum getEnumByKey(int code) {
        return codeMap.get(code);
    }

    AllianceBusiOpeTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
