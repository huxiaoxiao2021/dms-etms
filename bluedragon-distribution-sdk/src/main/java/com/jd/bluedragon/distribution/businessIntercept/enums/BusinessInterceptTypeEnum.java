package com.jd.bluedragon.distribution.businessIntercept.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拦截类型枚举
 *
 * @author fanggang7
 * @time 2020-12-11 10:50:24 周五
 */
public enum BusinessInterceptTypeEnum {

    /**
     * 取消拦截
     */
    CANCEL(10001, "取消拦截"),
    /**
     * 0重量拦截
     */
    ZERO_Weight(10002, "0重量拦截"),
    /**
     * 白条强制拦截 I owe you
     */
    PAY_BY_IOU_FORCE(10003, "白条强制拦截"),
    /**
     * 恶意订单
     */
    MALICIOUS_ORDER(10004, "恶意订单"),
    /**
     * 拒收订单
     */
    REJECT_RECEIVE(10005, "拒收订单"),
    /**
     * 病单
     */
    SICK_WAYBILL(10006, "病单"),
    /**
     * 改址
     */
    CHANGE_ADDRESS(10007, "改址"),
    /**
     * 京喜整包未称
     */
    JINGXI_ALL_PACK_NO_WEIGHT(10008, "京喜整包未称"),

    /**
     * 京喜空包拦截
     */
    JINGXI_EMPTY_BOX(10009, "京喜空包拦截"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        // 将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (BusinessInterceptTypeEnum enumItem : BusinessInterceptTypeEnum.values()) {
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

    BusinessInterceptTypeEnum(Integer code, String name) {
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
