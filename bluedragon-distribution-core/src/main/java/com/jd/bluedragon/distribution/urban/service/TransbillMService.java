package com.jd.bluedragon.distribution.urban.service;

import java.util.List;

import com.jd.bluedragon.distribution.urban.domain.TransbillM;


/**
 * 城配运单M表--Service接口
 * 
 * @ClassName: TransbillMService
 * @Description: TODO
 * @author wuyoude
 * @date 2017年04月28日 13:30:01
 *
 */
public interface TransbillMService {
	/**
	 * 保存一条记录，不出在执行insert，存在则update
	 *
	 * @param transbillM
	 * @return
	 * description：以后不再使用
	 */
	@Deprecated
	boolean saveOrUpdate(TransbillM transbillM);
	/**
	 * 根据运单号获取城配运单数据
	 * @param waybillCode
	 * @return
	 */
	TransbillM getByWaybillCode(String waybillCode);
	/**
	 * 根据派车单号获取有效的运单号列表
	 * 
	 * @param scheduleBillCode-派车单号
	 * @return
	 * description:关于派车单号和运单号集齐验证不再使用
	 */
	@Deprecated
	List<String> getEffectWaybillCodesByScheduleBillCode(String scheduleBillCode);
}
