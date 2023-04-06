package com.jd.bluedragon.distribution.jy.service.collect.emuns;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhengchengfa
 * @Description //包裹集齐状态
 * @date
 **/
public enum CollectStatusEnum {

    SCAN_NULL(101, "未到"),//未在当前场地包裹（不含本车中）
    SCAN_WAIT(102, "待扫"),//当前任务未验
    SCAN_DO(103, "已扫"),//当前任务已验
    SCAN_END(104, "在库");//其他任务已验未发
    /**
     * JDA 10个包裹，当前场地其他任务验货2个，当前任务5个，已验4个，未验1个，还有3个在其他场地
     * 未到：3
     * 已扫：4
     * 待扫：1
     * 在库：2
     */

    private int code;
    private String desc;
    private static Map<Integer, CollectStatusEnum> codeMap;
    public static Map<Integer, String> enumMap;

    static {
        // 将所有枚举装载到map中
        codeMap = new HashMap<Integer, CollectStatusEnum>();
        enumMap = new HashMap<Integer, String>();
        for (CollectStatusEnum _enum : CollectStatusEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            enumMap.put(_enum.getCode(), _enum.getDesc());
        }
    }

    CollectStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        CollectStatusEnum _enum = codeMap.get(code);
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
