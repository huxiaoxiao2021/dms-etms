package com.jd.bluedragon.distribution.abnormal.service;

import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
 *
 * @ClassName: DmsOperateHintService
 * @Description: 运单操作提示--Service接口
 * @author wuyoude
 * @date 2018年03月19日 10:10:39
 *
 */
public interface DmsOperateHintService extends Service<DmsOperateHint> {
	/**
	 * 根据运单号获取验货提示信息
	 * @param waybillCode
	 * @return
	 */
	String getInspectHintMessageByWaybillCode(String waybillCode);
	/**
	 * 根据运单号获取发货提示信息
	 * @param waybillCode
	 * @return
	 */
	String getDeliveryHintMessageByWaybillCode(String waybillCode);
}
