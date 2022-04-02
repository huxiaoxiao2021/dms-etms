package com.jd.bluedragon.distribution.jy.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/2
 * @Description: 排序类型
 */
public enum JyBizTaskUnloadOrderTypeEnum {

    ORDER_TIME(1, "排序时间"),
    ORDER_TIME_ABNORMAL(2, "排序时间和异常状态"),
    SORT_INTEGRAL(3, "积分");

    private Integer code;
    private String name;
    private static Map<Integer, JyBizTaskUnloadOrderTypeEnum> codeMap;
    public static Map<Integer, String> enumMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, JyBizTaskUnloadOrderTypeEnum>();
        enumMap = new HashMap<Integer, String>();
        for (JyBizTaskUnloadOrderTypeEnum _enum : JyBizTaskUnloadOrderTypeEnum.values()) {
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
    public static JyBizTaskUnloadOrderTypeEnum getEnumByCode(Integer code) {
        return codeMap.get(code);
    }

    /**
     * 通过编码获取规则类型名称
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getNameByCode(Integer code) {
        JyBizTaskUnloadOrderTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
    }

    JyBizTaskUnloadOrderTypeEnum(Integer code, String name) {
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
