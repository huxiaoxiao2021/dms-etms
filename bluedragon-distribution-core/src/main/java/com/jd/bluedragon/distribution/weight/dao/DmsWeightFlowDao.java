package com.jd.bluedragon.distribution.weight.dao;

import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlow;
import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlowCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: DmsWeightFlowDao
 * @Description: 分拣中心称重操作流水--Dao接口
 * @author wuyoude
 * @date 2018年03月09日 16:02:53
 *
 */
public interface DmsWeightFlowDao extends Dao<DmsWeightFlow> {
	/**
	 * 根据条件查询数据条数
	 * @param dmsWeightFlowCondition
	 * @return
	 */
	int queryNumByCondition(DmsWeightFlowCondition dmsWeightFlowCondition);

}
