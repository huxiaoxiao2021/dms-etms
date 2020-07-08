package com.jd.bluedragon.distribution.print.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 打印平台操作类型
 * Created by shipeilin on 2018/2/5.
 */
public enum WayBillFinishedEnum {
    DELIVERY(150, "妥投"),
    REFUSED(160, "拒收"),
    END_PICK_UP(-650, "终止揽收"),
    ORDER_CANCEL(-790, "下单取消"),
    INTEERCEPTED_SUCCESS(-860, "拦截成功");

    private Integer value;//值
    private String name;//操作名称

    WayBillFinishedEnum(Integer value, String name) {
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
        waybillStatusFinishedSet.add(INTEERCEPTED_SUCCESS.getValue());
    }

   static {
        for(WayBillFinishedEnum wayBillFinishedEnum :WayBillFinishedEnum.values()){
            WAYBILLSTATES+=wayBillFinishedEnum.value+",";
        }
       if(WAYBILLSTATES.endsWith(",")){
           WAYBILLSTATES=WAYBILLSTATES.substring(0,WAYBILLSTATES.length()-1);
       }
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
