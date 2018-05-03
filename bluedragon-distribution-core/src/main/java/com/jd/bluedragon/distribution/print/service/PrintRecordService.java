package com.jd.bluedragon.distribution.print.service;

import java.util.Set;

public interface PrintRecordService {
	/**
	 * 保存补打操作记录
	 * @param packageCode
	 * @return
	 */
	boolean saveReprintRecord(String packageCode);
	/**
	 * 获取运单号已经补打的包裹信息
	 * @param waybillCode
	 * @return
	 */
	Set<String> getHasReprintPackageCodes(String waybillCode);
	/**
	 * 根据运单号删除补打记录
	 * @param waybillCode
	 * @return
	 */
	boolean deleteReprintRecordsByWaybillCode(String waybillCode);
}
