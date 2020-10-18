package com.jd.bluedragon.distribution.goodsLoadScan;

public class GoodsLoadScanConstants {
    //扫描动作 1-扫描装车  0-取消扫描
    public static final Integer GOODS_SCAN_REMOVE = 0;
    public static final Integer GOODS_SCAN_LOAD = 1;

    //运单颜色状态--0无特殊颜色,1绿色,2橙色,3黄色,4红色
    public static final Integer GOODS_SCAN_LOAD_BLANK = 0;
    public static final Integer GOODS_SCAN_LOAD_GREEN = 1;
    public static final Integer GOODS_SCAN_LOAD_ORANGE = 2;
    public static final Integer GOODS_SCAN_LOAD_YELLOW = 3;
    public static final Integer GOODS_SCAN_LOAD_RED = 4;

    //任务状态
    public static final Integer GOODS_LOAD_TASK_STATUS_BLANK = 0;
    public static final Integer GOODS_LOAD_TASK_STATUS_BEGIN = 1;
    public static final Integer GOODS_LOAD_TASK_STATUS_END = 2;

    //yn
    public static final Integer YN_Y = 1;
    public static final Integer YN_N = 0;

    //记录多扫标记
    public static final Integer GOODS_LOAD_SCAN_FOLW_DISACCORD_Y = 1;
    public static final Integer GOODS_LOAD_SCAN_FOLW_DISACCORD_N = 1;

    //记录强发标记
    public static final Integer GOODS_LOAD_SCAN_FORCE_STATUS_Y = 1;
    public static final Integer GOODS_LOAD_SCAN_FORCE_STATUS_N = 0;

}
