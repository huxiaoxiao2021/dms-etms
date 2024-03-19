package com.jd.bluedragon.distribution.jy.dto.material;

import java.util.Objects;

/**
 * 物资系统关键节点
 *
 */
public enum MaterialOperateNodeV2Enum {

    PURCHASE_STOCK_IN(10, "采购入库"),
    NODE_DELIVERY(20, "节点发出"),
    NODE_DELIVERY_CANCEL(22, "节点发出取消"),
    NODE_TRANSFER(25, "节点中转"),
    NODE_RECEIPT(30, "节点接收"),
    AUTO_SCAN(40, "自动扫描"),
    ALLOCATION_DELIVERY(50, "节点逆向发出"),
    ALLOCATION_RECEIPT(60, "节点逆向接收"),
    SCRAP(70, "报废"),
    CUSTOMER_RETENTION(80, "客户保留"),
    RETURN(90, "退货"),
    DAMAGED_REPLACE(100, "破损退货换新");

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    MaterialOperateNodeV2Enum(Integer code, String desc) {
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
        for (MaterialOperateNodeV2Enum item : MaterialOperateNodeV2Enum.values()) {
            if (Objects.equals(code, item.getCode())) {
                return item.desc;
            }
        }
        return null;
    }

    public static MaterialOperateNodeV2Enum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MaterialOperateNodeV2Enum item : MaterialOperateNodeV2Enum.values()) {
            if (Objects.equals(code, item.getCode())) {
                return item;
            }
        }
        return null;
    }
}
