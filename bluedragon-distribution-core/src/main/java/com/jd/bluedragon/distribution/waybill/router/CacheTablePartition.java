package com.jd.bluedragon.distribution.waybill.router;


import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.ver.exception.IllegalWayBillCodeException;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.log4j.Logger;

/**
 * @author zhaohc
 *
 * 缓存运单路由规则类
 */
public class CacheTablePartition {

	private static final Logger logger = Logger.getLogger(CacheTablePartition.class);

	/** waybill 相关 */
	private static final String WAYBILL_TABLE  = "waybill";
	private static final String WAYBILL_PACKAGE_TABLE="waybill_package";
	private static final String PACKAGE_WEIGHTING_TABLE="package_weighting";
	private static final String DEFAULT_WAYBILL_TABLE  = "waybill1";
	private static int   MODE  = 128;
	private static boolean SWITCH_512 = false;

	/** 总部运单缓存库分库个数 bd_dms_cache*/
	private static int DMS_CACHE_DB_NUM = 8;

	/** 总部运单缓存库waybill表分表个数 128张表*/
	private static int DMS_CACHE_TABLE_WAYBILL_NUM = 128;

	static {
		MODE = Integer.parseInt(PropertiesHelper.newInstance().getValue("waybill.table.num"));
		SWITCH_512 = Boolean.parseBoolean(PropertiesHelper.newInstance().getValue("isWriteWaybill"));
		DMS_CACHE_DB_NUM = Integer.parseInt(PropertiesHelper.newInstance().getValue("dms.cache.db.num"));
		DMS_CACHE_TABLE_WAYBILL_NUM = Integer.parseInt(PropertiesHelper.newInstance().getValue("dms.cache.table.waybill.num"));
	}

	/**
	 * 读取waybill库的表名，如果切换成16张表的结构，按照新的哈希算法，否则按照原方法取表名
	 * @param waybillCode
	 * @return
     */
	public static String getWaybillTableName(String waybillCode){
		return getTableName(WAYBILL_TABLE,waybillCode);
	}
	/**
	 * 读取package_weighting库的表名，如果切换成16张表的结构，按照新的哈希算法，否则按照原方法取表名
	 * @param waybillCode
	 * @return
     */
	public static String getPackageWeightingTableName(String waybillCode){
		return getTableName(PACKAGE_WEIGHTING_TABLE,waybillCode);
	}

	/**
	 * 读取表名，如果切换成16张表的结构，按照新的哈希算法，否则按照原方法取表名
	 * @param waybillCode
	 * @return
	 */
	public static String getTableName(String tableName, String waybillCode){
		if(SWITCH_512) {
			long tableId = 0;
			//定位表
			long tableHashID = Math.abs(HashStrategy.BKDRHash(waybillCode)) % MODE;
			tableId = tableHashID + 1;

			return tableName + tableId;
		}else {
			String lastNumberString = waybillCode.replaceAll("[^0-9]", "");
			try{
				long lastNumber = Long.parseLong(trimZero(lastNumberString));
				long left = lastNumber % MODE;
				if (left == 0) {
					return tableName + MODE;
				} else {
					return tableName + left;
				}
			}catch (NumberFormatException e){
				logger.warn("运单号非法:" + waybillCode + ",异常原因：" + e.getMessage());
				throw new IllegalWayBillCodeException(JdResponse.MESSAGE_BILLCODE_EXCEPTION);
			}
		}
	}

	/**
	 * 读取waybill库的waybill_package表名，
	 * @param waybillCode
	 * @return
	 */
	public static String getWaybillPackageTableName(String waybillCode){
		long tableId = 0;
		//定位表
		long tableHashID = Math.abs(HashStrategy.BKDRHash(waybillCode)) % MODE;
		tableId = tableHashID + 1;

		return WAYBILL_PACKAGE_TABLE + tableId;
	}
	/**
	 * 将运单号左边除零
	 *
	 * @param lastNumberString
	 * @return
	 */
	private static String trimZero(String lastNumberString) {
		lastNumberString = lastNumberString.replaceAll("[A-Z]", "");
		while (lastNumberString.startsWith("0")
				&& lastNumberString.length() > 1) {
			lastNumberString = lastNumberString.substring(1,
					lastNumberString.length());
		}
		if(StringHelper.isEmpty(lastNumberString)){
			return "0";
		}
		return lastNumberString;
	}

	/**
	 * 根据运单号获取库名（运单缓存库8*128）
	 * @param waybillCode
	 * @return
	 */
	public static String getDmsCacheDb(String waybillCode) {
		//根据逻辑表编号，确定库
		long dbId = getLogicTableId(waybillCode,DMS_CACHE_DB_NUM,DMS_CACHE_TABLE_WAYBILL_NUM) / DMS_CACHE_TABLE_WAYBILL_NUM;
		dbId= dbId+1;
		return dbId+"";
	}
	/**
	 * 根据运单号获取总部运单缓存库的waybill表名（8*128)
	 * @param waybillCode
	 * @return
	 */
	public static String getDmsWaybillCacheTableName(String waybillCode){
		return getCacheTableName(WAYBILL_TABLE,waybillCode);
	}
	/**
	 * 根据运单号获取总部运单缓存库的waybill_package表名（8*128)
	 * @param waybillCode
	 * @return
	 */
	public static String getDmsPackageWeightingCacheTableName(String waybillCode){
		return getCacheTableName(PACKAGE_WEIGHTING_TABLE,waybillCode);
	}
	/**
	 * 根据运单号获取总部运单缓存库的表名（8*128)
	 * @param waybillCode
	 * @return
	 */
	public static String getCacheTableName(String tableName, String waybillCode){
		long tableId = (getLogicTableId(waybillCode,DMS_CACHE_DB_NUM,DMS_CACHE_TABLE_WAYBILL_NUM) % DMS_CACHE_TABLE_WAYBILL_NUM)+1;
		return tableName+tableId;
	}

	/**
	 * 获取逻辑表编号
	 * 如共8个库，每个库128张表，则共有1024张表，逻辑表为在1024张表中的定位。
	 * @param waybillCode 运单号
	 * @param dbCount 分库个数
	 * @param tableCount 每个库的分表个数
	 * @return
	 */
	private static long getLogicTableId (String waybillCode, int dbCount, int tableCount){
		String waybillCodeForTrans = waybillCode.replaceAll("[^0-9]", "");
		if(StringHelper.isEmpty(waybillCodeForTrans)){
			throw new IllegalArgumentException("[CacheTablePartition.getDmsCacheDb]waybillCode无效,waybillCode:"+waybillCode);
		}
		try{
			long waybillNum = Long.parseLong(waybillCodeForTrans);
			//分表总数
			int totalTableCount = dbCount*tableCount;

			//获取逻辑表编号
			return waybillNum%totalTableCount;
		}catch (NumberFormatException e){
			logger.warn("运单号非法:" + waybillCode+",异常原因：" + e.getMessage());
			throw new IllegalWayBillCodeException(JdResponse.MESSAGE_BILLCODE_EXCEPTION);
		}
	}
}