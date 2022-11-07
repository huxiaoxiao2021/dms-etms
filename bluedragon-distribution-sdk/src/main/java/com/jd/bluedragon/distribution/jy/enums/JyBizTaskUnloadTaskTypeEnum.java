package com.jd.bluedragon.distribution.jy.enums;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卸车任务类型
 *
 * @author fanggang7
 * @time 2022-11-07 10:51:27 周一
 */
public enum JyBizTaskUnloadTaskTypeEnum {

    UNLOAD_TASK_CATEGORY_DMS(1,"分拣卸车"),
    UNLOAD_TASK_CATEGORY_TYS(2,"转运卸车"),
    UNLOAD_TASK_CATEGORY_WAREHOUSE_RECEIVE(3,"接货仓验货"),
    ;

    private Integer code;
    private String name;
    public static Map<Integer, String> ENUM_MAP;
    public static List<Integer> ENUM_LIST;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        for (JyBizTaskUnloadTaskTypeEnum _enum : JyBizTaskUnloadTaskTypeEnum.values()) {
            ENUM_MAP.put(_enum.getCode(), _enum.getName());
            ENUM_LIST.add(_enum.getCode());
        }

    }

    JyBizTaskUnloadTaskTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
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

}
