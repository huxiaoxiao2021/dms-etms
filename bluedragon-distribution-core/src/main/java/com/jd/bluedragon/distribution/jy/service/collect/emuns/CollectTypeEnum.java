package com.jd.bluedragon.distribution.jy.service.collect.emuns;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public enum CollectTypeEnum {

    WAYBILL_BUQI(101, "运单不齐"),//运单不齐或场地不齐
    //    TASK_BUQI(1011, "本车不齐"),
//    SITE_BUQI(1012, "在库不齐"),
    TASK_JIQI(201, "本车集齐"),
    SITE_JIQI(202, "在库集齐");

    private int code;
    private String desc;
    private static Map<Integer, CollectTypeEnum> codeMap;
    public static Map<Integer, String> enumMap;

    static {
        // 将所有枚举装载到map中
        codeMap = new HashMap<Integer, CollectTypeEnum>();
        enumMap = new HashMap<Integer, String>();
        for (CollectTypeEnum _enum : CollectTypeEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            enumMap.put(_enum.getCode(), _enum.getDesc());
        }
    }

    CollectTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        CollectTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getDesc();
        }
        return "未知";
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
