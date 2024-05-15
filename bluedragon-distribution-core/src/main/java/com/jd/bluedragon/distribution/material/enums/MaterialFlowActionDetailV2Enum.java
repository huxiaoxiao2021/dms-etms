package com.jd.bluedragon.distribution.material.enums;

import java.util.Objects;

/**
 * 物资系统关键节点
 *
 * @author fanggang7
 * @time 2024-01-02 17:31:53 周二
 */
public enum MaterialFlowActionDetailV2Enum {

    /**
     * 采购入库
     */
    PURCHASE_STOCK_IN(10, "采购入库"),

    /**
     * 节点发出
     */
    NODE_DELIVERY(20, "节点发出"),

    /**
     * 节点发出取消
     */
    NODE_DELIVERY_CANCEL(22, "节点发出取消"),

    /**
     * 节点中转
     */
    NODE_TRANSFER(25, "节点中转"),

    /**
     * 节点接收
     */
    NODE_RECEIPT(30, "节点接收"),

    /**
     * 自动扫描
     */
    AUTO_SCAN(40, "自动扫描"),

    /**
     * 节点逆向发出
     */
    ALLOCATION_DELIVERY(50, "节点逆向发出"),

    /**
     * 节点逆向接收
     */
    ALLOCATION_RECEIPT(60, "节点逆向接收"),

    /**
     * 报废
     */
    SCRAP(70, "报废"),

    /**
     * 客户保留
     */
    CUSTOMER_RETENTION(80, "客户保留"),

    /**
     * 退货
     */
    RETURN(90, "退货"),

    /**
     * 破损退货换新
     */
    DAMAGED_REPLACE(100, "破损退货换新");

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    MaterialFlowActionDetailV2Enum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MaterialFlowActionDetailV2Enum item : MaterialFlowActionDetailV2Enum.values()) {
            if (Objects.equals(code, item.getCode())) {
                return item.desc;
            }
        }
        return null;
    }

    public static MaterialFlowActionDetailV2Enum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MaterialFlowActionDetailV2Enum item : MaterialFlowActionDetailV2Enum.values()) {
            if (Objects.equals(code, item.getCode())) {
                return item;
            }
        }
        return null;
    }
}
