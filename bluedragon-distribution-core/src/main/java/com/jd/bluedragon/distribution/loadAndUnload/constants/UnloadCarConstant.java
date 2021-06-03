package com.jd.bluedragon.distribution.loadAndUnload.constants;

/**
 * 卸车相关常量
 */
public class UnloadCarConstant {

    //卸车协助人历史查询数量
    public static final Integer UNLOAD_HELPER_HISTORY_SIZE = 10;

    /**
     * 卸车时效， 默认120分钟
     */
    public static final Integer UNLOAD_CAR_DURATION_DEFAULT = 120;
    /**
     * 卸车时效类型   0-默认方式（120min）   1-调用路由时效时间
     */
    public static final Integer UNLOAD_CAR_DURATION_TYPE_DEFAULT = 0;
    public static final Integer UNLOAD_CAR_DURATION_TYPE_ROUTE = 1;

    /**
     * 卸车任务是否超时完成
     */
    public static final Integer UNLOAD_CAR_COMPLETE_TIMEOUT = 1;
    public static final Integer UNLOAD_CAR_COMPLETE_NORMAL = 0;

}
