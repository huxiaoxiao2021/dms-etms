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
	public static final String REDIS_KEY_DMS_WEIGHT_FLOW = "DmsWeightFlow";
}
