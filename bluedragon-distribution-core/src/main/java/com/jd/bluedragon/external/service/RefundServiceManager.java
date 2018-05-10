package com.jd.bluedragon.external.service;

import com.jd.fa.refundService.ValidRequest;
/**
 * 
 * @ClassName: RefundServiceManager
 * @Description: 财务相关接口管理
 * @author: wuyoude
 * @date: 2018年5月9日 下午2:18:00
 *
 */
public interface RefundServiceManager {
	/**
	 * 先货类型运单调用财务接口
	 * @param xmlMessage
	 * @return
	 */
	String sendXmlMessage(com.jd.fa.orderrefund.XmlMessage xmlMessage);
	/**
	 * 先款类型运单调用财务接口
	 * @param requestParam
	 * @param businessType
	 * @return
	 */
	ValidRequest innerSystemApplyForCheckWithType(
	        com.jd.fa.refundService.CustomerRequestNew requestParam,
	        int businessType);
}
