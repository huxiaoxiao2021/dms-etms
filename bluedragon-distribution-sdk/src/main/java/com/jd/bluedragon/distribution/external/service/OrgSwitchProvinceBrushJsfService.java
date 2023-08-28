package com.jd.bluedragon.distribution.external.service;

/**
 * 大区切换省区刷数接口
 *
 * @author hujiping
 * @date 2023/8/28 10:30 AM
 */
public interface OrgSwitchProvinceBrushJsfService {

    /**
     * merchant_weight_white_list 表刷数省区字段
     */
    void merchantWeightWhiteBrush(Integer startId);

    /**
     * func_switch_config 表刷数省区字段
     */
    void funcSwitchConfigBrush(Integer startId);

    /**
     * express_bill_exception_report 表刷数省区字段
     */
    void expressBillExceptionReportBrush(Integer startId);

    /**
     * discarded_waybill_storage_temp 表刷数省区字段
     */
    void discardedWaybillStorageBrush(Integer startId);
    /**
     * discarded_package_storage_temp 表刷数省区字段
     */
    void discardedPackageStorageBrush(Integer startId);

    /**
     * collection_bag_exception_report 表刷数省区字段
     */
    void collectionBagExceptionBrush(Integer startId);

    /**
     * dock_base_info 表刷数省区字段
     */
    void dockBaseInfoBrush(Integer startId);
}
