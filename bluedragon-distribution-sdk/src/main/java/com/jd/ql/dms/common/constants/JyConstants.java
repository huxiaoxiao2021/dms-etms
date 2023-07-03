package com.jd.ql.dms.common.constants;

import java.io.Serializable;

/**
 * 拣运常量
 *
 * @author hujiping
 * @date 2022/10/18 10:09 AM
 */
public class JyConstants implements Serializable {

    /** jy降级配置常量  */
    // jy flink发货降级开关
    public static String JY_FLINK_SEND_IS_DEMOTION = "jyFlinkSendIsDemotion";
    // jy flink卸车降级开关
    public static String JY_FLINK_UNLOAD_IS_DEMOTION = "jyFlinkUnloadIsDemotion";
    // jy封车降级开关
    public static String JY_SEAL_CAR_MONITOR_IS_DEMOTION = "jySealCarMonitorIsDemotion";
    // jy卸车降级开关
    public static String JY_VEHICLE_TASK_UNLOAD_DETAIL_IS_DEMOTION = "jyVehicleTaskUnloadDetailIsDemotion";
    // jy发货降级开关
    public static String JY_VEHICLE_SEND_DETAIL_IS_DEMOTION = "jyVehicleSendDetailIsDemotion";

    /**
     * 接货仓验货拦截件提示码
     */
    public static final int WAREHOUSE_INSPECTION_INTERCEPT_CODE = 5001;

    public static final int JY_DEPPON_OPERATOR_ID = -1;
    public static final String JY_DEPPON_OPERATOR_ERP = "-1";

}
