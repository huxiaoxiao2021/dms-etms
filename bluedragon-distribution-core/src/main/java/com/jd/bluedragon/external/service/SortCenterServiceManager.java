package com.jd.bluedragon.external.service;

import com.jd.pop.sortcenter.ws.VenderOperInfoResult;
/**
 * 
 * @ClassName: SortCenterManager
 * @Description: 根据订单号获取POP相关信息
 * @author: wuyoude
 * @date: 2018年5月8日 下午6:42:57
 *
 */
public interface SortCenterServiceManager {
	/**
	 * 根据订单号获取POP相关信息
	 * @param arg0
	 * @return
	 */
    VenderOperInfoResult searchInfoForByOrderId(long arg0);
}
