package com.jd.bluedragon.distribution.jy.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/1
 * @Description: 到车卸车任务状态
 */
public enum JyBizTaskUnloadStatusEnum {

    INIT(0,"等待初始"),

    ON_WAY(1,"在途"),

    WAIT_UN_SEAL(2,"待解"),

    WAIT_UN_LOAD(3,"待卸"),

    UN_LOADING(4,"卸车"),

    UN_LOAD_DONE(5,"已完成"),

    CANCEL(6,"取消");

    private Integer code;
    private String name;
    private static Map<Integer, JyBizTaskUnloadStatusEnum> codeMap;
    public static Map<Integer, String> enumMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, JyBizTaskUnloadStatusEnum>();
        enumMap = new HashMap<Integer, String>();
        for (JyBizTaskUnloadStatusEnum _enum : JyBizTaskUnloadStatusEnum.values()) {
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
    public static JyBizTaskUnloadStatusEnum getEnumByCode(Integer code) {
        return codeMap.get(code);
    }

    /**
     * 通过编码获取规则类型名称
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getNameByCode(Integer code) {
        JyBizTaskUnloadStatusEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知";
    }

    JyBizTaskUnloadStatusEnum(Integer code, String name) {
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
