package com.jd.bluedragon.distribution.jy.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/1
 * @Description: 任务状态
 * 任务状态；0：初始化；1：已分配；2：已开始；3：已关闭
 */
public enum JyScheduleTaskStatusEnum {

    INIT(0,"等待初始"),

    DISTRIBUTED(1,"已分配"),

    STARTED(2,"已开始"),

    CLOSED(3,"已关闭");

    private Integer code;
    private String name;
    private static Map<Integer, JyScheduleTaskStatusEnum> codeMap;
    public static Map<Integer, String> enumMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, JyScheduleTaskStatusEnum>();
        enumMap = new HashMap<Integer, String>();
        for (JyScheduleTaskStatusEnum _enum : JyScheduleTaskStatusEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            enumMap.put(_enum.getCode(), _enum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static JyScheduleTaskStatusEnum getEnumByCode(Integer code) {
        return codeMap.get(code);
    }

    /**
     * 通过编码获取规则类型名称
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getNameByCode(Integer code) {
        JyScheduleTaskStatusEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
    }

    JyScheduleTaskStatusEnum(Integer code, String name) {
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
