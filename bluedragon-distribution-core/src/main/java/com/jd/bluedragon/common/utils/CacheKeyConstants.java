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
    public static final String REDIS_PREFIX_UNLOAD_BOARD_PACKAGE_COUNT = "board.package.count-";
    /**
     * 卸车任务
     *  板号绑定的多货包裹号 key=前缀-板号
     */
    public static final String REDIS_PREFIX_UNLOAD_BOARD_SURPLUS_PACKAGE_COUNT = "board.surplusPackage.count-";
    /**
     * 卸车任务
     *  封车编码已扫的包裹号 key=前缀-封车编码
     */
    public static final String REDIS_PREFIX_UNLOAD_SEAL_PACKAGE_COUNT = "sealCar.package.count-";
    /**
     * 卸车任务
     *  封车编码已扫的多货包裹号 key=前缀-封车编码
     */
    public static final String REDIS_PREFIX_UNLOAD_SEAL_SURPLUS_PACKAGE_COUNT = "sealCar.surplusPackage.count-";
    /**
     * 卸车任务
     *  组板绑定的包裹 key=前缀-板号-包裹号
     */
    public static final String REDIS_PREFIX_BOARD_PACK = "unload.board.package-";
    /**
     * 卸车任务
     *  封车编码绑定的包裹 key=前缀-封车编码-包裹号
     */
    public static final String REDIS_PREFIX_SEALCAR_SURPLUS_PACK = "unload.sealCar.surplusPackage-";
	/**
	 * 包裹抽检记录的缓存
	 */
	public static final String CACHE_KEY_PACKAGE_OR_WAYBILL_CHECK_FLAG = "dmsWeb:packageOrWaybillCheckFlag:";
	/**
	 * 包裹发货状态缓存redis的key
	 */
	public static final String CACHE_KEY_WAYBILL_SEND_STATUS = "dmsWeb:waybillSendStatus:";
	/**
	 * 卸车任务已拦截包裹扫描
	 *  组板绑定的包裹 key=前缀-封车编码-包裹号
	 */
	public static final String REDIS_PREFIX_SEAL_PACK_INTERCEPT = "unload.seal.package.intercept-";
    /**
     * 缓存key-发货关系
     */
    public static final String CACHE_KEY_FORMAT_SAVE_SEND_RELATION ="dmsWeb:saveSendRelationKey:%s:%s";
}
