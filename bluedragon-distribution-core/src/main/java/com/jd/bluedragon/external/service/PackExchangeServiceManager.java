package com.jd.bluedragon.external.service;

import com.jd.wms.packExchange.Result;

/**
 * 
 * @ClassName: PackExchangeServiceManager
 * @Description: TODO
 * @author: wuyoude
 * @date: 2018年5月9日 下午3:36:54
 *
 */
public interface PackExchangeServiceManager {
	/**
	 * 亚一通过包裹获取出库明细
	 * @param arg0 token值
	 * @param arg1 报文
	 * @return
	 */
	Result queryWs(String arg0,String arg1);
}
