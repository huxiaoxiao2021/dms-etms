package com.jd.bluedragon.distribution.exceptionReport.billException.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * @author laoqingchang1
 * @description 运单完结枚举
 * @date 2022-10-17 14:52
 */
public enum WaybillFinishedEnum {
    DELIVERY(150, "妥投"),
    REFUSED(160, "拒收"),
    END_PICK_UP(-650, "终止揽收"),
    ORDER_CANCEL(-790, "下单取消"),
    RECEIVE_AND_RETURN_FINISHED(530, "上门接货退货完成");

    private Integer value;//值
    private String name;//操作名称

    WaybillFinishedEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
    public static Set<Integer> waybillStatusFinishedSet = new HashSet<Integer>();
    public static String WAYBILLSTATES="";

    /**
     * 初始化终结状态集合
     */
    static{

        waybillStatusFinishedSet.add(DELIVERY.getValue());
        waybillStatusFinishedSet.add(REFUSED.getValue());
        waybillStatusFinishedSet.add(END_PICK_UP.getValue());
        waybillStatusFinishedSet.add(ORDER_CANCEL.getValue());
        waybillStatusFinishedSet.add(RECEIVE_AND_RETURN_FINISHED.getValue());
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
