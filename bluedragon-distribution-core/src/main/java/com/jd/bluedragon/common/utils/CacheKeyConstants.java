package com.jd.bluedragon.common.utils;

public class CacheKeyConstants {

    private static final String DMS_CACHE_PREFIX = "dms.etms.";

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
	 * 卸车任务已拦截包裹扫描
	 *  组板绑定的包裹 key=前缀-封车编码-包裹号
	 */
	public static final String REDIS_PREFIX_SEAL_WAYBILL = "unload.seal.waybill-";

	/**
	 * 运单下面的包裹集合
	 */
	public static final String CACHE_KEY_WAYBILL_PACKAGE_CODES = "dmsWeb:waybillPackageCodes:";

    /**
     * 缓存key-发货关系
     */
    public static final String CACHE_KEY_FORMAT_SAVE_SEND_RELATION ="dmsWeb:saveSendRelationKey:%s:%s";

    /**
     * 文件箱号绑定锁
     */
    public static final String BOX_BIND_NX_KEY = DMS_CACHE_PREFIX + "fileBox.bind:";

    // ------------------------ S 通知相关
    /**
     * 全局最新通知数据缓存
     */
    public static final String CACHE_KEY_FORMAT_CLIENT_GLOBAL_LAST_NEW_NOTICE = "dmsWeb:client:notice:lastNewNotice";

    /**
     * 用户最新通知数据缓存，形如 dmsWeb:client:notice:lastNewNotice:fanggang7 格式
     */
    public static final String CACHE_KEY_FORMAT_CLIENT_USER_LAST_NEW_NOTICE = "dmsWeb:client:notice:lastNewNotice:%s";

    /**
     * 全局通知更新记录
     */
    public static final String CACHE_KEY_FORMAT_CLIENT_NOTICE_GLOBAL_CHANGE_INFO = "dmsWeb:client:notice:global:changeInfo";

    /**
     * 用户总通知个数缓存
     */
    public static final String CACHE_KEY_FORMAT_CLIENT_NOTICE_USER_TOTAL_COUNT = "dmsWeb:client:notice:totalCount:%s";
    // 用户总通知数过期时间，单位小时
    public static final int CACHE_KEY_CLIENT_NOTICE_TOTAL_COUNT_TIME_EXPIRE = 24;

    /**
     * 用户已读通知个数缓存，形如 dmsWeb:client:notice:readCount:fanggang7 格式
     */
    public static final String CACHE_KEY_FORMAT_CLIENT_NOTICE_USER_READ_COUNT = "dmsWeb:client:notice:readCount:%s";
    public static final int CACHE_KEY_CLIENT_NOTICE_READ_COUNT_TIME_EXPIRE = 24;

    /**
     * 用户搜索时间缓存，防止刷搜索，形如 dmsWeb:client:notice:lastSearch:fanggang7 格式
     */
    public static final String CACHE_KEY_FORMAT_CLIENT_NOTICE_USER_LAST_SEARCH_TIME = "dmsWeb:client:notice:lastSearch:%s";
    public static final Integer CACHE_KEY_CLIENT_NOTICE_USER_LAST_SEARCH_TIME_EXPIRE = 60;

    // ------------------------ E 通知相关

    /**
     * 用户登录设备记录缓存key
     */
    public static final String CACHE_KEY_FORMAT_CLIENT_LOGIN_USER_DEVICE_ID = "dmsWeb:client:login:userDeviceId:%s:%s";
    /**
     * 用户登录设备记录缓存过期时间
     */
    public static final int CACHE_KEY_FORMAT_CLIENT_LOGIN_DEVICE_ID_EXPIRE_TIME = 24;

    /**
     * 卸车负责人Key
     */
    public static final String CACHE_KEY_UNLOAD_MAIN_ERP = "unload.main.erp-";
}
