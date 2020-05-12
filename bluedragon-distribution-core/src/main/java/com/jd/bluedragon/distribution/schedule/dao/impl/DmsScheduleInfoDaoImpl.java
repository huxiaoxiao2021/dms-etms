package com.jd.bluedragon.distribution.schedule.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.schedule.dao.DmsScheduleInfoDao;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfoCondition;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnPickingVo;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

/**
 * @ClassName: DmsScheduleInfoDaoImpl
 * @Description: 分拣调度信息表，数据来源于运单和企配仓--Dao接口实现
 * @author wuyoude
 * @date 2020年04月30日 09:15:52
 *
 */
@Repository("dmsScheduleInfoDao")
public class DmsScheduleInfoDaoImpl extends BaseDao<DmsScheduleInfo> implements DmsScheduleInfoDao {

	@Override
	public DmsScheduleInfo queryByWaybillCode(String waybillCode) {
		return sqlSession.selectOne(this.nameSpace+".queryByWaybillCode", waybillCode);
	}

	@Override
	public boolean updateScheduleInfo(DmsScheduleInfo dmsScheduleInfo) {
		return sqlSession.update(this.nameSpace+".updateScheduleInfo", dmsScheduleInfo) == 1;
	}

	@Override
	public boolean updateEdnFahuoInfo(DmsScheduleInfo dmsScheduleInfo) {
		return sqlSession.update(this.nameSpace+".updateEdnFahuoInfo", dmsScheduleInfo) == 1;
	}

	@Override
	public PagerResult<DmsEdnPickingVo> queryEdnPickingListByPagerCondition(
			DmsScheduleInfoCondition dmsScheduleInfoCondition) {
		return super.queryByPagerCondition("queryEdnPickingListByPagerCondition", dmsScheduleInfoCondition);
	}

	@Override
	public DmsEdnPickingVo queryDmsEdnPickingVo(String scheduleBillCode) {
		return sqlSession.selectOne(this.nameSpace+".queryDmsEdnPickingVo", scheduleBillCode);
	}

	@Override
	public List<String> queryEdnBatchNumList(String scheduleBillCode) {
		return sqlSession.selectList(this.nameSpace+".queryEdnBatchNumList", scheduleBillCode);
	}
	
	@Override
	public List<DmsScheduleInfo> queryEdnDmsScheduleInfoList(String scheduleBillCode) {
		return sqlSession.selectList(this.nameSpace+".queryEdnDmsScheduleInfoList", scheduleBillCode);
	}
}
