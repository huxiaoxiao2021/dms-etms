package com.jd.bluedragon.common.dto.device.enums;

/**
 * <p>
 *    设备类型枚举类
 *    以后有扩展的话，可以加，目前只加了简单的几个枚举
 *    对应自动化的查询页面为：http://dmsauto.etms.jd.com/deviceConfig/typeConfig/getPage
 *
 * @author wuzuxiang
 * @since 2019/12/3
 **/
public enum DeviceTypeEnum {

    SORTING_MACHINE("SORTING-MACHINE","自动分拣机"),
    SORTING_CABINET("SORTING-CABINET","智能分拣柜"),
    SORING_AGV("SORING-AGV","分拣AGV机器人"),
    GANTRY("GANTRY","龙门架");


    /**
     * 设备类型编码
     */
    private String typeCode;

    /**
     * 设备类型名称
     */
    private String typeName;

    DeviceTypeEnum(String typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getTypeName() {
        return typeName;
    }
}
