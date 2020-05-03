package com.jd.bluedragon.distribution.schedule.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfoCondition;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnPickingVo;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * @ClassName: DmsScheduleInfoDao
 * @Description: 分拣调度信息表，数据来源于运单和企配仓--Dao接口
 * @author wuyoude
 * @date 2020年04月30日 09:15:52
 *
 */
@Repository("dmsScheduleInfoDao")
public interface DmsScheduleInfoDao extends Dao<DmsScheduleInfo> {
	/**
	 * 根据运单号查询对象
	 * @param waybillCode
	 * @return
	 */
	DmsScheduleInfo queryByWaybillCode(String waybillCode);
	/**
	 * 更新调度信息
	 * @param dmsScheduleInfo
	 * @return
	 */
	boolean updateScheduleInfo(DmsScheduleInfo dmsScheduleInfo);
	/**
	 * 更新企配仓业务信息
	 * @param dmsScheduleInfo
	 * @return
	 */
	boolean updateEdnFahuoInfo(DmsScheduleInfo dmsScheduleInfo);
	/**
	 * 分页查询数据,返回当前实体的分页对象
	 * @param dmsScheduleInfoCondition
	 * @return
	 */
	PagerResult<DmsEdnPickingVo> queryEdnPickingListByPagerCondition(DmsScheduleInfoCondition dmsScheduleInfoCondition);
	/**
	 * 查询调度单号对应的匹配仓批次列表
	 * @param scheduleBillCode
	 * @return
	 */
	DmsEdnPickingVo queryDmsEdnPickingVo(String scheduleBillCode);
	/**
	 * 查询调度单号对应的匹配仓批次列表
	 * @param scheduleBillCode
	 * @return
	 */
	List<String> queryEdnBatchNumList(String scheduleBillCode);
	/**
	 * 查询企配仓调度明细
	 * @param scheduleBillCode
	 * @return
	 */
	List<DmsScheduleInfo> queryEdnDmsScheduleInfoList(String scheduleBillCode);
}
