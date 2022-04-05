package com.jd.bluedragon.distribution.jy.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/1
 * @Description: 车辆类型
 */
public enum JyLineTypeEnum {

    OTHER(0, "其他"),

    TRUNK_LINE(1, "干线"),

    BRANCH_LINE(2, "支线"),

    TRANSFER(3, "传"),

    SHUTTLE(4, "摆")
    ;

    private Integer code;
    private String name;
    private static Map<Integer, JyLineTypeEnum> codeMap;
    public static Map<Integer, String> enumMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, JyLineTypeEnum>();
        enumMap = new HashMap<Integer, String>();
        for (JyLineTypeEnum _enum : JyLineTypeEnum.values()) {
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
    public static JyLineTypeEnum getEnumByCode(Integer code) {
        return codeMap.get(code);
    }

    /**
     * 通过编码获取规则类型名称
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getNameByCode(Integer code) {
        JyLineTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
    }

    JyLineTypeEnum(Integer code, String name) {
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
