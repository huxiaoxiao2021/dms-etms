package com.jd.bluedragon.distribution.urban.service;

import java.util.List;

import com.jd.bluedragon.distribution.urban.domain.UrbanWaybill;


/**
 * 城配运单同步表--Service接口
 * 
 * @ClassName: UrbanWaybillService
 * @Description: TODO
 * @author wuyoude
 * @date 2017年04月17日 16:55:58
 *
 */
public interface UrbanWaybillService {
	/**
	 * 保存一条记录，不出在执行insert，存在则update
	 * @param urbanWaybill
	 * @return
	 */
	boolean save(UrbanWaybill urbanWaybill);
	/**
	 * 根据运单号获取城配运单数据
	 * @param waybillCode
	 * @return
	 */
	UrbanWaybill getByWaybillCode(String waybillCode);
	/**
	 * 根据派车单号获取城配运单列表
	 * 
	 * @param scheduleBillCode-派车单号
	 * @return
	 */
	List<UrbanWaybill> getListByScheduleBillCode(String scheduleBillCode);
}
