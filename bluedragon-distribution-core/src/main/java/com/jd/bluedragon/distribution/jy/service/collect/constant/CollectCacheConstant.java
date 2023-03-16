package com.jd.bluedragon.distribution.jy.service.collect.constant;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class CollectCacheConstant {

    /**
     * 空任务扫描运单，按运单批修改集齐状态
     * 防重
     * Value: StringUtils.EMPTY
     */
    public static final String CACHE_TASK_NULL_WAYBILL_UPDATE_COLLECT_STATUS = "cache:waybill:update:collect:status:";
    public static final int CACHE_TASK_NULL_WAYBILL_UPDATE_COLLECT_STATUS_TIMEOUT = 7;//单位：天
    //并发锁
    public static final String LOCK_TASK_NULL_WAYBILL_UPDATE_COLLECT_STATUS = "lock:waybill:update:collect:status:";
    public static final int LOCK_TASK_NULL_WAYBILL_UPDATE_COLLECT_STATUS_TIMEOUT = 30;//单位 min

    /**
     * 空任务扫描运单，按运单批修改集齐状态 运单拆分
     * 防重
     * Value: StringUtils.EMPTY
     */
    public static final String CACHE_TASK_NULL_WAYBILL_SPLIT_UPDATE_COLLECT_STATUS = "cache:waybill:split:update:collect:status:";
    public static final int CACHE_TASK_NULL_WAYBILL_SPLIT_UPDATE_COLLECT_STATUS_TIMEOUT = 7;//单位：天
    //并发锁
    public static final String LOCK_TASK_NULL_WAYBILL_SPLIT_UPDATE_COLLECT_STATUS = "lock:waybill:split:update:collect:status:";
    public static final int LOCK_TASK_NULL_WAYBILL_SPLIT_UPDATE_COLLECT_STATUS_TIMEOUT = 30;//单位 min


    /**
     * 封车节点集齐初始化前 拆分
     * 防重
     * Value: StringUtils.EMPTY
     */
    public static final String CACHE_SEAL_COLLECT_SPLIT_BEFORE_INIT = "cache:seal:collect:split:";
    public static final int CACHE_SEAL_COLLECT_SPLIT_BEFORE_INIT_TIMEOUT = 7;//单位 天

    /**
     * 封车节点集齐拆分之后 初始化
     * 防重
     * Value: StringUtils.EMPTY
     */
    public static final String CACHE_SEAL_COLLECT_INIT_AFTER_SPLIT = "cache:seal:collect:init:";
    public static final int CACHE_SEAL_COLLECT_INIT_AFTER_SPLIT_TIMEOUT = 7;//单位 天


    /**
     * 空任务扫描节点按运单 集齐初始化前 拆分
     * 防重
     * Value: StringUtils.EMPTY
     */
    public static final String CACHE_TASK_NULL_WAYBILL_SPLIT_BEFORE_INIT = "cache:scan:waybill:collect:split:";
    public static final int CACHE_TASK_NULL_WAYBILL_SPLIT_BEFORE_INIT_TIMEOUT = 7;//单位 天
    //并发锁
    public static final String LOCK_TASK_NULL_WAYBILL_SPLIT_BEFORE_INIT = "lock:scan:waybill:collect:split:";
    public static final int LOCK_TASK_NULL_WAYBILL_SPLIT_BEFORE_INIT_TIMEOUT = 10;//单位 min

    /**
     * 空任务扫描节点按运单 集齐拆分之后 初始化
     * 防重
     * Value: StringUtils.EMPTY
     */
    public static final String CACHE_TASK_NULL_WAYBILL_INIT_AFTER_SPLIT = "cache:scan:waybill:collect:init:";
    public static final int CACHE_TASK_NULL_WAYBILL_INIT_AFTER_SPLIT_TIMEOUT = 7;//单位 天
}
