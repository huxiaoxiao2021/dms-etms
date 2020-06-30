package com.jd.bluedragon.common.utils;

public class CacheKeyConstants {
	
	public static final String POP_PRINT_BACKUP_KEY = "popprint.backup.list";

	/**
	 * 判断是否发货redis key 前缀 + 批次号
	 * */
	public static final String REDIS_KEY_IS_DELIVERY = "sortingService.getSendMSelective.key";

	/**
	 * 板号绑定的包裹号/箱号个数 key=前缀-板号
	 */
	public static final String REDIS_PREFIX_BOARD_BINDINGS_COUNT = "board.combination.bindings.count";
	/**
	 * 称重流水
	 */
	public static final String CACHE_KEY_DMS_WEIGHT_INFO = "DmsWeightInfo";
    /**
     * 提示语缓存key-用户创建的提示（userHintMsg）
     */
    public static final String CACHE_KEY_USER_HINT_MSG ="userHintMsg";
    /**
     * 提示语缓存key-系统提示（sysHintMsg）
     */
    public static final String CACHE_KEY_SYS_HINT_MSG ="sysHintMsg";
    /**
     * 缓存key-包裹补打记录（reprintRecords）
     */
    public static final String CACHE_KEY_REPRINT_RECORDS ="reprintRecords";
	/**
	 * 箱号状态缓存redis的key
	 */
	public static final String CACHE_KEY_BOX_STATUS = "BoxStatus";
    /**
     * 卸车任务
     *  板号绑定的包裹号 key=前缀-板号
     */
    public static final String REDIS_PREFIX_UNLOAD_BOARD_BINDINGS_COUNT = "unload.board.bindings.count";
    /**
     * 卸车任务
     *  封车编码已扫的包裹号 key=前缀-封车编码
     */
    public static final String REDIS_PREFIX_UNLOAD_SEAL_PACKAGE_COUNT = "unload.board.seal.package.count";
    /**
     * 卸车任务
     *  封车编码已扫的多货包裹号 key=前缀-封车编码
     */
    public static final String REDIS_PREFIX_UNLOAD_SEAL_SURPLUS_PACKAGE_COUNT = "unload.board.seal.surplusPackage.count";
}
