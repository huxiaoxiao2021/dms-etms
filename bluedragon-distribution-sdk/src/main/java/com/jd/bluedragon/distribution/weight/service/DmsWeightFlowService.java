package com.jd.bluedragon.distribution.weight.service;

import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlow;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
 *
 * @ClassName: DmsWeightFlowService
 * @Description: 分拣中心称重操作流水--Service接口
 * @author wuyoude
 * @date 2018年03月09日 16:02:53
 *
 */
public interface DmsWeightFlowService extends Service<DmsWeightFlow> {
	/**
	 * 校验是否包含运单称重
	 * @param waybillCode
	 * @return
	 */
	boolean checkTotalWeight(String waybillCode);
}
