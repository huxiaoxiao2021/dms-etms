package com.jd.bluedragon.distribution.receive.service;

import com.jd.bluedragon.distribution.receive.domain.ArReceive;
import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
*
* @ClassName: ArReceiveService
* @Description: 空铁提货表--Service接口
* @author wuyoude
* @date 2018年01月15日 22:51:31
*
*/
public interface ArReceiveService extends Service<ArReceive> {
	/**
	 * 根据包裹号/箱号获取最近的一次航空登记批次信息
	 * @return
	 */
	ArSendCode getLastArSendCodeByBarcode(String barcode);
}
