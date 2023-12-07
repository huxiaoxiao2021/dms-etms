package com.jd.bluedragon.distribution.jy.constants;

import java.util.HashMap;
import java.util.Map;

public enum PickingGoodTaskDetailInitServiceEnum {

    DMS_SEND_DMS_PICKING(64, 101, "提货时上游发货场地是分拣中心发货，待提明细初始化数据依赖分拣发货数据"),
    OTHER_SEND_DMS_PICKING(0, 102, "提货时上游发货场地非分拣中心发货，待提明细初始化数据依赖运输封车数据"),
    ;

    /**
     * 来源
     */
    private int source;
    /**
     * 初始化服务类code值
     */
    private int targetCode;
    private String msg;

    PickingGoodTaskDetailInitServiceEnum(int source, int targetCode, String msg) {
        this.source = source;
        this.targetCode = targetCode;
        this.msg = msg;
    }

    private static final Map<Integer, PickingGoodTaskDetailInitServiceEnum> map;

    static {
        map = new HashMap<Integer, PickingGoodTaskDetailInitServiceEnum>();
        for (PickingGoodTaskDetailInitServiceEnum _enum : PickingGoodTaskDetailInitServiceEnum.values()) {
            map.put(_enum.getSource(), _enum);
        }
    }

    public static PickingGoodTaskDetailInitServiceEnum getEnumBySource(Integer source) {
        return map.get(source);
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(int targetCode) {
        this.targetCode = targetCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
