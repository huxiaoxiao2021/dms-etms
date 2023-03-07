package com.jd.bluedragon.distribution.weightvolume;

/**
 * <p>
 *     称重数据来源
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
     * 分拣称重量方
     */
    DMS_CLIENT_WEIGHT_VOLUME,

    /**
     * 分拣自动化测量
     */
    DMS_AUTOMATIC_MEASURE,

    /**
     * 分拣DWS测量
     */
    DMS_DWS_MEASURE,

    /**
     * 分拣页面快运称重
     */
    DMS_WEB_FAST_TRANSPORT,
    /***
     * 分拣页面快运称重-包裹导入
     */
    DMS_WEB_PACKAGE_FAST_TRANSPORT,
    /**
     * 分拣内部拆分
     */
    DMS_INNER_SPLIT,
    /**
     * 分拣内部拆分-经济网按箱拆分包裹称重
     */
    ENET_BOX_SPLIT_PACKAGE,
    /**
     * 云分拣-按箱号称重
     */
    CLPS_WEIGHT_BY_BOX,
    /**
     * 抽检
     */
    SPOT_CHECK,

    /**
     * 德邦融合-嘉峪关项目
     */
    DPRH,

    /**
     * PDA称重
     */
    DMS_PDA;
}
