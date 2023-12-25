package com.jd.bluedragon.distribution.jy.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum PickingGoodTaskDetailInitServiceEnum {

    DMS_SEND_DMS_PICKING(64, 101, "提货时上游发货场地是分拣中心发货，待提明细初始化数据依赖分拣发货数据"),
    OTHER_SEND_DMS_PICKING(0, 102, "默认兜底：提货时上游发货场地非分拣中心发货，待提明细初始化数据依赖运输封车数据"),
    ;

    /**
     * 来源
     */
    private Integer source;
    /**
     * 初始化服务类code值
     */
    private Integer targetCode;
    private String desc;

    PickingGoodTaskDetailInitServiceEnum(Integer source, Integer targetCode, String desc) {
        this.source = source;
        this.targetCode = targetCode;
        this.desc = desc;
    }

    private static final Map<Integer, PickingGoodTaskDetailInitServiceEnum> map;

    static {
        map = new HashMap<Integer, PickingGoodTaskDetailInitServiceEnum>();
        for (PickingGoodTaskDetailInitServiceEnum _enum : PickingGoodTaskDetailInitServiceEnum.values()) {
            map.put(_enum.getSource(), _enum);
        }
    }

    public static PickingGoodTaskDetailInitServiceEnum getEnumBySource(Integer source) {
        PickingGoodTaskDetailInitServiceEnum en = map.get(source);
        if(Objects.isNull(en)) {
            return PickingGoodTaskDetailInitServiceEnum.OTHER_SEND_DMS_PICKING;
        }
        return en;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(Integer targetCode) {
        this.targetCode = targetCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
