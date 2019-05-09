package com.jd.bluedragon.distribution.waybill.domain;

import com.jd.bluedragon.distribution.storage.domain.StoragePackageMStatusEnum;

import java.util.HashMap;
import java.util.Map;

public enum WaybillNoCollectionQueryTypeEnum {

    SEND_CODE_QUERY_TYPE(1, "批次号查询差异"),
    BOARD_CODE_QUERY_TYPE(2, "板号查询差异"),
    BOX_CODE_QUERY_TYPE(3, "箱号查询差异");

    private Integer type;

    private String name;

    public static Map<Integer, String> waybillNoCollectionQueryTypeMap;

    static {
        //将所有枚举装载到map中
        waybillNoCollectionQueryTypeMap = new HashMap<>();
        for (WaybillNoCollectionQueryTypeEnum waybillNoCollectionQueryTypeEnum : WaybillNoCollectionQueryTypeEnum.values()) {
            waybillNoCollectionQueryTypeMap.put(waybillNoCollectionQueryTypeEnum.getType(), waybillNoCollectionQueryTypeEnum.getName());
        }
    }

    WaybillNoCollectionQueryTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 判断是否是正确的枚举值
     *
     */
    public static boolean isCorrectType(int type) {
        return waybillNoCollectionQueryTypeMap.containsKey(type);
    }
}
