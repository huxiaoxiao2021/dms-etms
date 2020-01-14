package com.jd.bluedragon.distribution.weightvolume;

/**
 * <p>
 *     对接分拣web的系统上游数据来源的系统编码枚举
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
public enum FromSourceEnum {

    /**
     * 平台打印
     */
    DMS_CLIENT_PLATE_PRINT,

    /**
     * 站点平台打印
     */
    DMS_CLIENT_SITE_PLATE_PRINT,

    /**
     * 换单打印
     */
    DMS_CLIENT_SWITCH_BILL_PRINT,

    /**
     * 包裹称重
     */
    DMS_CLIENT_PACKAGE_WEIGH_PRINT,

    /**
     * 驻场打印
     */
    DMS_CLIENT_FIELD_PRINT,

    /**
     * 批量分拣称重
     */
    DMS_CLIENT_BATCH_SORT_WEIGH_PRINT,

    /**
     * 快运称重打印
     */
    DMS_CLIENT_FAST_TRANSPORT_PRINT,

    /**
     * 分拣自动化测量
     */
    DMS_AUTOMATIC_MEASURE,

    /**
     * 分拣页面快运称重
     */
    DMS_WEB_FAST_TRANSPORT,

    /**
     * 分拣内部拆分
     */
    DMS_INNER_SPLIT;
}
