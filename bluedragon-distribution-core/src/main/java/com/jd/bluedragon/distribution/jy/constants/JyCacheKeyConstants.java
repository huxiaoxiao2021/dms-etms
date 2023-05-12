package com.jd.bluedragon.distribution.jy.constants;

import java.util.concurrent.TimeUnit;

/**
 * @author fanggang7
 * @time 2022-11-22 16:55:17 周二
 */
public class JyCacheKeyConstants {

    /*
     * 接货仓验货岗任务创建缓存锁标识
     */
    public static final String JY_WAREHOUSE_INSPECTION_CREATE_LOCK = "JY:WARE:INSPECTION:TASK:CREATE:LOCK:GP_CODE:%s";
    public static final Integer JY_WAREHOUSE_INSPECTION_CREATE_LOCK_EXPIRED = 10;
    public static final TimeUnit JY_WAREHOUSE_INSPECTION_CREATE_LOCK_EXPIRED_UNIT = TimeUnit.SECONDS;

    public static final String JY_TRANSPORT_SEND_VEHICLE_VALIDATE_LAST_GENERATE_STR = "jy:send:vehicle:dockValidate:lastGenerate:%s:%s";
    public static final Integer JY_TRANSPORT_SEND_VEHICLE_VALIDATE_LAST_GENERATE_EXPIRED = 50;
    public static final String JY_TRANSPORT_SEND_VEHICLE_VALIDATE_STR = "jy:send:vehicle:dockValidate:%s:%s:%s";
    public static final Integer JY_TRANSPORT_SEND_VEHICLE_VALIDATE_STR_EXPIRED = 10;
    public static final TimeUnit JY_TRANSPORT_SEND_VEHICLE_VALIDATE_STR_TIME_UINT = TimeUnit.MINUTES;

}
