package com.jd.bluedragon.common.utils;

public class CacheKeyConstants {

    private static final String DMS_CACHE_PREFIX = "dms.etms.";

    private static final String JY_CACHE_PREFIX = "jy:";

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

    public static final String REDIS_PREFIX_STAGE_TASK_CREATE = "jy.unload.stage.task.create-";
    public static final int REDIS_PREFIX_STAGE_TASK_CREATE_TIMEOUT_SECONDS = 3;
    public static final long REDIS_PREFIX_STAGE_TASK_CREATE_WAIT_SPIN_TIMESTAMP = 100;

    public static final String REDIS_PREFIX_TASK_BOARD_CREATE = "jy.unload.task.board.relation.create-";
    public static final int REDIS_PREFIX_TASK_BOARD_CREATE_TIMEOUT_SECONDS = 3;
    public static final long REDIS_PREFIX_TASK_BOARD_CREATE_WAIT_SPIN_TIMESTAMP = 100;

    /***************************************** 抽检缓存start *******************************************/

    /**
     * 抽检校验缓存前缀
     */
    public static final String CACHE_SPOT_CHECK_CHECK = "dmsWeb:spotCheck:%S-%S";
    /**
     * 包裹抽检记录的缓存
     */
    public static final String CACHE_KEY_PACKAGE_OR_WAYBILL_CHECK_FLAG = "dmsWeb:packageOrWaybillCheckFlag:";
    /**
     * 运单已抽检缓存前缀
     */
    public static final String CACHE_SPOT_CHECK = "dmsWeb:waybillCheckFlag:%S";
    /**
     * 包裹抽检记录的缓存
     */
    public static final String CACHE_SPOT_CHECK_PACK_LIST = "dmsWeb:waybillSpotCheckList:%S-%S:";
    /**
     * 抽检包裹上传图片的缓存
     */
    public static final String CACHE_SPOT_CHECK_PICTURE = "spotCheck.pictureUrl-%s-%s";
    /**
     * 抽检下发fxm的缓存
     */
    public static final String CACHE_FXM_SEND_WAYBILL = "spotCheck.fxmSend-%s";
    /**
     * 抽检下发AI的缓存
     */
    public static final String CACHE_AI_SEND_WAYBILL = "spotCheck.aiSend-%s";

    /***************************************** 抽检缓存end *******************************************/

	/**
	 * 包裹发货状态缓存redis的key
	 */
	public static final String CACHE_KEY_WAYBILL_SEND_STATUS = "dmsWeb:waybillSendStatus:%s-%s";
    /**
     * 运单下已发货包裹发货状态缓存redis的key
     */
    public static final String CACHE_KEY_WAYBILL_SEND_PACKS_STATUS = "dmsWeb:waybillSendPacksStatus:%s-%s";
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
     * 缓存key-无滑道数据标识
     */
    public static final String CACHE_KEY_FORMAT_WAYBILL_HASNO_PRESITE_RECORD_FLG ="dmsWeb:waybillHasnoPresiteRecordFlg:%s";
    /**
     * 文件箱号绑定锁
     */
    public static final String BOX_BIND_NX_KEY = DMS_CACHE_PREFIX + "fileBox.bind:";

    /**
     * 箱号绑定物资关系锁
     */
    public static final String BOX_BIND_MATERIAL_KEY = DMS_CACHE_PREFIX + "box.bind.material:";
    public static final int BOX_BIND_MATERIAL_LOCK_TIME = 10;

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

    public static final String PACKAGE_SEND_BATCH_KEY = DMS_CACHE_PREFIX + "delivery.packageSend:%s";
    public static final String PACKAGE_SEND_COUNT_KEY = DMS_CACHE_PREFIX + "delivery.packageSend.count:%s";
    public static final String PACKAGE_SEND_LOCK_KEY = DMS_CACHE_PREFIX + "delivery.packageSend.lock:%s:%s";

    public static final String WAYBILL_SEND_BATCH_KEY = DMS_CACHE_PREFIX + "delivery.waybillSend:%s";
    public static final String WAYBILL_SEND_COUNT_KEY = DMS_CACHE_PREFIX + "delivery.waybillSend.count:%s";

    public static final String INITIAL_SEND_COUNT_KEY = DMS_CACHE_PREFIX + "delivery.initial.send.count:%s";
    public static final String COMPELETE_SEND_COUNT_KEY = DMS_CACHE_PREFIX + "delivery.compelete.send.count:%s";

    public static final String VIRTUAL_BOARD_CREATE_DESTINATION = "dmsWeb:virtualBoard:createDestination:%s_%s";
    public static final int VIRTUAL_BOARD_CREATE_DESTINATION_TIMEOUT = 60;

    public static final String VIRTUAL_BOARD_BIND = "dmsWeb:virtualBoard:bind:%s_%s";
    public static final int VIRTUAL_BOARD_BIND_TIMEOUT = 60;

    public static final String DISCARDED_STORAGE_OPERATE_SCAN = "dmsWeb:discardedStorage:scan:%s";
    public static final int DISCARDED_STORAGE_OPERATE_SCAN_TIMEOUT = 60;

    /**
     * 卸车扫描防重，每个单号只能扫描一次
     * 单号+场地+卸车任务
     */
    public static final String JY_UNLOAD_SCAN_KEY = JY_CACHE_PREFIX + "ul:scan:%s:%s:%s";

    /**
     * 卸车扫描-不更新卸车进度的缓存key
     *  单号+场地+卸车任务
     */
    public static final String JY_UNLOAD_SCAN_NOT_UPDATE_PROCESS_KEY = JY_CACHE_PREFIX + "ul:scan:nup:%s:%s:%s";

    /**
     * 拣运卸车任务主键
     * bizId
     */
    public static final String JY_UNLOAD_TASK_FIRST_SCAN_KEY = JY_CACHE_PREFIX + "ul:biz:first:%s";

    /**
     * 拣运卸车任务主键
     * bizId
     */
    public static final String JY_UNLOAD_TASK_LAST_SCAN_TIME_KEY = JY_CACHE_PREFIX + "ul:biz:lastScan:%s";

    /**
     * PDA扫描进度缓存
     * bizId
     */
    public static final String JY_UNLOAD_PDA_PROCESS_KEY = JY_CACHE_PREFIX + "ul:process:part:%s";

    /**
     * 拣运卸车任务扫描进度
     * bizId
     */
    public static final String JY_UNLOAD_PROCESS_KEY = JY_CACHE_PREFIX + "ul:process:%s";

    /**
     * 拣运卸车任务数据
     * sealCarCode
     */
    public static final String JY_UNLOAD_SEAL_CAR_MONITOR_SEAL_CAR_CODE = JY_CACHE_PREFIX + "sealCarMonitor:%s";

    /**
     * 派车单
     */
    public static final String JY_SEND_TRANS_WORK_KEY = JY_CACHE_PREFIX + "ss:init:%s";

    /**
     * 拣运发车任务主键
     * bizId
     */
    public static final String JY_SEND_TASK_FIRST_SCAN_KEY = JY_CACHE_PREFIX + "ss:biz:first:%s";

    /**
     * 拣运发车任务明细主键
     * bizId + createSite + receiveSite
     */
    public static final String JY_SEND_TASK_DETAIL_FIRST_SCAN_KEY = JY_CACHE_PREFIX + "sst:biz:first:%s:%s";

    /**
     * 工序操作-key
     */
    public static final String CACHE_KEY_WORK_STATION_EDIT = "k_work_station_edit";
    /**
     * 网格操作-key
     */
    public static final String CACHE_KEY_WORK_STATION_GRID_EDIT = "k_work_station_grid_edit";
    /**
     * 网格计划操作-key
     */
    public static final String CACHE_KEY_WORK_STATION_ATTEND_PLAN_EDIT = "k_work_station_attend_plan_edit";

    /**
     * 接货仓验货 任务缓存，按group_code唯一存在
     */
    public static final String JY_WAREHOUSE_INSPECTION_TASK_EXIST_KEY = JY_CACHE_PREFIX + "ware_inspec:group_code:%s";


    /**
     * 转运卸车任务人工模式组板  包裹板 或者箱号板
     */
    public static final String REDIS_PREFIX_BOARD_SCAN_TYPE = "tys.unload.board.scan.type:";
    public static final String BOARD_SCAN_TYPE_PACKAGE = "package";
    public static final String BOARD_SCAN_TYPE_BOX = "box";
    public static final String BOARD_SCAN_TYPE_ELSE = "else";

    /**
     * 滞留上报操作key，场地+箱号|批次|板号
     */
    public static final String CACHE_KEY_FORMAT_STRAND_REPORT = "dmsWeb:strandReport:%s:%s";

    /***************************************** 设备校准缓存start *******************************************/
    // 设备校准扫描-key
    public static final String CACHE_KEY_DWS_CALIBRATE_SCAN = "dwsCalibrate.scan:%s-%s";
    // 设备校准超时推送咚咚-key
    public static final String CACHE_KEY_DWS_CALIBRATE_PUSH_DD = "dwsCalibrate.pushDD:%s";
    // 设备校准抽检处理-key
    public static final String CACHE_KEY_DWS_CALIBRATE_SPOT_DEAL = "dwsCalibrate.spotDeal:%s";
    /***************************************** 设备校准缓存end *******************************************/

    /**
     * 拣运卸车统计
     * sealCarCode
     */
    public static final String JY_UNLOAD_AGG_KEY = JY_CACHE_PREFIX + "jyUnloadAgg:%s";


    /**
     * 拣运卸车统计
     * sealCarCode
     */
    public static final String JY_UNLOAD_AGG_LOCK_KEY = JY_CACHE_PREFIX + "jyUnloadAggLock:%s";

    /**
     * 拣运卸车统计(备库)
     * sealCarCode
     */
    public static final String JY_UNLOAD_AGG_BAK_KEY = JY_CACHE_PREFIX + "jyUnloadAggBak:%s";

    /**
     * 拣运卸车统计(备库)
     * sealCarCode
     */
    public static final String JY_UNLOAD_AGG_BAK_LOCK_KEY = JY_CACHE_PREFIX + "jyUnloadAggBakLock:%s";

    /**
     * 拣运发货统计
     * sealCarCode
     */
    public static final String JY_SEND_AGG_KEY = JY_CACHE_PREFIX + "jySendAgg:%s";

    /**
     * 发货锁前缀
     */
    public static final String JY_SEND_AGG_LOCK_KEY = JY_CACHE_PREFIX + "jySendAggLock:%s";

    /**
     * 拣运发货统计（备库）
     * sealCarCode
     */
    public static final String JY_SEND_AGG_BAK_KEY = JY_CACHE_PREFIX + "jySendAggBak:%s";


    /**
     * 发货锁前缀
     */
    public static final String JY_SEND_AGG_BAK_LOCK_KEY = JY_CACHE_PREFIX + "jySendAggBakLock:%s";

    /**
     * 拣运发货产品类型统计
     * sealCarCode
     */
    public static final String JY_SEND_PRODUCT_AGG_KEY = JY_CACHE_PREFIX + "jySendProductAgg:%s";

    /**
     * 拣运发货产品类型统计
     * sealCarCode
     */
    public static final String JY_SEND_PRODUCT_AGG_LOCK_KEY = JY_CACHE_PREFIX + "jySendProductAggLock:%s";

    /**
     * 拣运发货统计（备库）
     * sealCarCode
     */
    public static final String JY_SEND_PRODUCT_AGG_BAK_KEY = JY_CACHE_PREFIX + "jySendProductAggBak:%s";

    /**
     * 拣运发货统计（备库）
     * sealCarCode
     */
    public static final String JY_SEND_PRODUCT_AGG_BAK_LOCK_KEY = JY_CACHE_PREFIX + "jySendProductAggBakLock:%s";

    /**
     * 换单打印
     * sealCarCode
     */
    public static final String CACHE_KEY_CHANGE_ORDER_PRINT_KEY = JY_CACHE_PREFIX + "changeOrderPrintLock:%s";

    /**
     * 作业任务自动关闭
     * 任务类型，bizId
     */
    public static final String CACHE_KEY_JY_BIZ_TASK_AUTO_CLOSE = JY_CACHE_PREFIX + "jyBizTaskAutoClose:%s:%s";

    /**
     * 批量作废周转筐缓存key
     */
    public static final String CACHE_KEY_BATCH_ABOLISH_RECYCLE_BASKET = "abolishRecycleBasket:%s";

    /**
     * 特安件缓存Key
     */
    public static final String CACHE_KEY_JY_TEAN_WAYBILL = JY_CACHE_PREFIX + "jyTEANWaybill:%s:%s";


    /**
     * 拣运-滞留扫描
     */
    public static final String CACHE_KEY_JY_STRAND_SCAN = JY_CACHE_PREFIX + "strandScan:%s";


    /**
     * 拣运波次发货统计锁key
     */
    public static final String JY_SEND_PREDICT_AGG_LOCK_KEY = JY_CACHE_PREFIX + "jySendPredictAggLock:%s";

    /**
     * 亚运会安检运单缓存
     */
    public static final String CACHE_KEY_ASIA_SPORT_SECURITY_CHECK_WAYBILL = "AsiaSportSecurityCheckWaybill:%s";
    public static final int CACHE_KEY_ASIA_SPORT_SECURITY_CHECK_WAYBILL_TIMEOUT = 5;

    /**
     * 拣运波次发货统计锁key
     */
    public static final String JY_SEND_PREDICT_AGG_KEY = JY_CACHE_PREFIX + "jySendPredictAgg:%s";


    /**
     * 拣运-违禁品运单全程跟踪Key
     */
    public static final String CACHE_KEY_JY_CONTRABAND_BDTRANCE = JY_CACHE_PREFIX + "contrabandBDTrance:%s";

    /**
     * 违禁品上报并发key
     */
    public static final String CONTRABAND_LOCK_KEY = "DMS.EXCEPTION.CONTRABAND.UPLOAD:%s";

    /**
     * 空铁提货岗添加流向
     */
    public static final String CACHE_KEY_AIR_RAIL_ADD_SEND_FLOW = JY_CACHE_PREFIX + "AIR_RAIL_ADD_SEND_FLOW:%s";
}
