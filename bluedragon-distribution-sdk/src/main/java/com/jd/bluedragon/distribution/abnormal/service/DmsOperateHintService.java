package com.jd.bluedragon.distribution.abnormal.service;

import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
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
	/**
	 * 获取包裹需要补打的提示信息
	 * @param waybillCode
	 * @return
	 */
	DmsOperateHint getNeedReprintHint(String waybillCode);

	/**
	 * 根据查询条件分页获取提示信息
	 * @param dmsOperateHintCondition
	 * @return
     */
	PagerResult<DmsOperateHint> queryByPagerCondition(DmsOperateHintCondition dmsOperateHintCondition);

	/**
	 * 获取开启状态的提示信息配置
	 * @param dmsOperateHint
	 * @return
     */
	DmsOperateHint getEnabledOperateHint(DmsOperateHint dmsOperateHint);
}
