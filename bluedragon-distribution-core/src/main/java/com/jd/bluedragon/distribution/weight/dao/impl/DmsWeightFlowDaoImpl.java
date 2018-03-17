package com.jd.bluedragon.distribution.weight.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlow;
import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlowCondition;
import com.jd.bluedragon.distribution.weight.dao.DmsWeightFlowDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

/**
 *
 * @ClassName: DmsWeightFlowDaoImpl
 * @Description: 分拣中心称重操作流水--Dao接口实现
 * @author wuyoude
 * @date 2018年03月09日 16:02:53
 *
 */
@Repository("dmsWeightFlowDao")
public class DmsWeightFlowDaoImpl extends BaseDao<DmsWeightFlow> implements DmsWeightFlowDao {

	@Override
	public int queryNumByCondition(DmsWeightFlowCondition dmsWeightFlowCondition) {
		Integer num = this.getSqlSession().selectOne(getNameSpace()+".pageNum_queryByPagerCondition", dmsWeightFlowCondition);
		return num.intValue();
	}

}
