package com.jd.bluedragon.distribution.jy.service.task.autoclose.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业工作台任务自动关闭业务类型枚举
 *
 * @author fanggang7
 * @time 2023-01-31 20:38:37 周二
 */
public enum JyAutoCloseTaskBusinessTypeEnum {

    /**
     * 解封车后自动关闭卸车任务
     */
    WAIT_UNLOAD_NOT_FINISH(1, "待卸状态中未卸车完成"),

    /**
     * 卸车开始后一直不再卸车扫描，也未操作卸车完成，自动关闭卸车任务
     */
    UNLOADING_NOT_FINISH(2, "卸车后未卸车完成"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<>();
        ENUM_LIST = new ArrayList<>();
        for (JyAutoCloseTaskBusinessTypeEnum enumItem : JyAutoCloseTaskBusinessTypeEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
    }

    /**
     * 通过code获取name
     *
     * @param code 编码
     * @return string
     */
    public static String getEnumNameByCode(Integer code) {
        return ENUM_MAP.get(code);
    }

    JyAutoCloseTaskBusinessTypeEnum(Integer code, String name) {
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
