package com.jd.bluedragon.distribution.operateMonitor.service;

import com.jd.bluedragon.distribution.operateMonitor.domain.OperateMonitor;

import java.util.List;

/**
 * 分拣中心操作监控service
 */
public interface OperateMonitorService {

    /** 验货操作Monitor类型 */
    public static final Integer MONITOR_TYPE_INSPECTION = 1;

    /** 分拣操作Monitor类型 */
    public static final Integer MONITOR_TYPE_SORTING = 2;

    /** 发货操作Monitor类型 */
    public static final Integer MONITOR_TYPE_SEND = 3;
    /**
     * 根据包裹号查询包裹的相关数据
     * @param packageCode
     * @return
     */
    public List<OperateMonitor> queryOperateMonitorByPackageCode(String packageCode);

}
