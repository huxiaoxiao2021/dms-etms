package com.jd.bluedragon.distribution.goodsLoadScan;

public class GoodsLoadScanConstants {
    //扫描动作 1-扫描装车  0-取消扫描
    public static final Integer GOODS_SCAN_REMOVE = 0;
    public static final Integer GOODS_SCAN_LOAD = 1;

    //运单颜色状态--0无特殊颜色：已装=0, 1绿色：已扫完（未装=0）,2橙色：不齐强发, 3黄色：多扫, 4红色：不齐
    public static final Integer GOODS_SCAN_LOAD_BLANK = 0;
    public static final Integer GOODS_SCAN_LOAD_GREEN = 1;
    public static final Integer GOODS_SCAN_LOAD_ORANGE = 2;
    public static final Integer GOODS_SCAN_LOAD_YELLOW = 3;
    public static final Integer GOODS_SCAN_LOAD_RED = 4;

    //任务状态 未开始-0， 已开始-1， 已完成-2
    public static final Integer GOODS_LOAD_TASK_STATUS_BLANK = 0;
    public static final Integer GOODS_LOAD_TASK_STATUS_BEGIN = 1;
    public static final Integer GOODS_LOAD_TASK_STATUS_END = 2;

    //yn
    public static final Integer YN_Y = 1;
    public static final Integer YN_N = 0;

    //记录多扫标记 多扫-1 非多扫-0
    public static final Integer GOODS_LOAD_SCAN_FOLW_DISACCORD_Y = 1;
    public static final Integer GOODS_LOAD_SCAN_FOLW_DISACCORD_N = 0;

    //记录强发标记  强发-1， 非强发-0
    public static final Integer GOODS_LOAD_SCAN_FORCE_STATUS_Y = 1;
    public static final Integer GOODS_LOAD_SCAN_FORCE_STATUS_N = 0;

    //装车发货cache key 任务维度、运单维度、包裹维度
    public static final String CACHE_KEY_TASK = "Bnet_LoadScan_TaskId_";
    public static final String CACHE_KEY_WAYBILL = "Bnet_LoadScan_Waybill_";
    public static final String CACHE_KEY_PACKAGE = "Bnet_LoadScan_Package_";

    // 装车发货加锁标识
    public static final String LOCK_KEY = "Bnet_LoadScan_Lock_";

    //每页数量
    public static final int PAGE_SIZE = 500;
    //循环次数限制
    public static final int GOODS_LOAD_CYCLE_COUNT = 50;

    //一条任务对应最大包裹数
    //public static final int GOODS_LOAD_MAX_SIZE = 20000;

    //删除任务同步删除包裹
    public static final String LOAD_TASK_DELETE = "LOAD_TASK_DELETE_";

}
