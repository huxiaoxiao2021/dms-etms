package com.jd.bluedragon.distribution.businessIntercept.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拦截处理节点枚举
 *
 * @author fanggang7
 * @time 2020-12-11 10:50:24 周五
 */
public enum BusinessInterceptDisposeNodeEnum {

    /**
     * 撤销 取消拦截
     */
    CANCEL_RECALL(-10001, "取消拦截"),
    /**
     * 撤销 0重量拦截
     */
    ZERO_WEIGHT_RECALL(-10002, "0重量拦截"),
    /**
     * 撤销 白条强制拦截 I owe you
     */
    PAY_BY_IOU_FORCE_RECALL(-10003, "白条强制拦截"),
    /**
     * 撤销 恶意订单
     */
    MALICIOUS_ORDER_RECALL(-10004, "恶意订单"),
    /**
     * 撤销 拒收订单
     */
    REJECT_RECEIVE_RECALL(-10005, "拒收订单"),
    /**
     * 撤销 病单
     */
    SICK_WAYBILL_RECALL(-10006, "病单"),
    /**
     * 撤销 改址
     */
    CHANGE_ADDRESS_RECALL(-10007, "改址"),
    /**
     * 撤销 京喜整包未称
     */
    JINGXI_ALL_PACK_NO_WEIGHT_RECALL(-10008, "京喜整包未称"),

    /**
     * 撤销 京喜空包拦截
     */
    JINGXI_EMPTY_BOX_RECALL(-10009, "京喜空包拦截"),

    /**
     * 换单打印
     */
    EXCHANGE_WAYBILL(10001, "换单打印"),
    /**
     * 补称重
     */
    ZERO_WEIGHT(10002, "补称重"),
    /**
     * 补打
     */
    PAY_BY_IOU_FORCE(10003, "补打"),
    /**
     * 拆包
     */
    MALICIOUS_ORDER(10004, "拆包"),
    /**
     * 逆向发货
     */
    REJECT_RECEIVE(10005, "逆向发货"),
    /**
     * 补打新单号
     */
    REPRINT_NEW_WAYBILL(10006, "补打新单号"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        // 将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (BusinessInterceptDisposeNodeEnum enumItem : BusinessInterceptDisposeNodeEnum.values()) {
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

    BusinessInterceptDisposeNodeEnum(Integer code, String name) {
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
