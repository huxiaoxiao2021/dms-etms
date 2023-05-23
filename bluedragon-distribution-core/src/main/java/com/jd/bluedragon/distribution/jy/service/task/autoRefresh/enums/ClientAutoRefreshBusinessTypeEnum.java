package com.jd.bluedragon.distribution.jy.service.task.autoRefresh.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * APP自动刷新业务场景枚举
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-14 19:11:32 周日
 */
public enum ClientAutoRefreshBusinessTypeEnum {

    /**
     * 解封车任务列表
     */
    UNSEAL_TASK_LIST(1, "解封车任务列表"),

    /**
     * 卸车任务列表
     */
    UNLOAD_TASK_LIST(2, "卸车任务列表"),

    /**
     * 卸车扫描进度
     */
    UNLOAD_PROGRESS(3, "卸车扫描进度"),

    /**
     * 发货任务列表
     */
    SEND_TASK_LIST(4, "发货任务列表"),

    /**
     * 发货扫描进度
     */
    SEND_PROGRESS(5, "发货扫描进度"),

    /**
     * 转运卸车任务列表
     */
    TYS_UNLOAD_TASK_LIST(6, "卸车任务列表"),

    /**
     * 转运卸车扫描进度
     */
    TYS_UNLOAD_PROGRESS(7, "卸车扫描进度"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<>();
        ENUM_LIST = new ArrayList<>();
        for (ClientAutoRefreshBusinessTypeEnum enumItem : ClientAutoRefreshBusinessTypeEnum.values()) {
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

    ClientAutoRefreshBusinessTypeEnum(Integer code, String name) {
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
