package com.jd.bluedragon.distribution.consumable.service;

/**
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2018年08月23日 20时:43分
 */
public interface WaybillConsumableExportCol {
    String FILENAME = "包装耗材导出";
    public static final String[] TITLES = {
            "waybillCode",
            "receiveTimeStr",
            "dmsName",
            "type",
            "name",
            "unit",
            "receiveQuantity",
            "confirmQuantity",
            "receiveUserErp",
            "confirmStatusStr",
            "confirmUserErp",
            "confirmTimeStr",
            "packUserErp"
    };
    public static final String[] PROPERTYS = {
            "运单号",
            "揽收完成时间",
            "始发转运中心",
            "耗材类型",
            "耗材名称",
            "单位",
            "揽收预计数量",
            "确认使用数量",
            "揽收人ERP",
            "确认状态",
            "确认人ERP",
            "确认时间",
            "打包人ERP"
    };
}
