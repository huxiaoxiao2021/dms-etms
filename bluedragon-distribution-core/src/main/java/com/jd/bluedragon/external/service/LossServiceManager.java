package com.jd.bluedragon.external.service;

import java.util.List;

import com.jd.loss.client.LossProduct;

/**
 * 
 * @ClassName: LossServiceManager
 * @Description: 报丢系统服务管理
 * @author: wuyoude
 * @date: 2018年5月9日 下午2:43:22
 *
 */
public interface LossServiceManager {
	/**
	 * 根据订单号获取报损数量
	 * @param orderId
	 * @return
	 */
    int getLossProductCountOrderId(String orderId);
	/**
	 * 根据订单号获取报损明细
	 * @param orderId
	 * @return
	 */
    List<LossProduct> getLossProductByOrderId(String orderId);
}
