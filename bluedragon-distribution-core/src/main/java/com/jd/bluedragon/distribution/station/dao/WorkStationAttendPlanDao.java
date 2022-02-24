package com.jd.bluedragon.distribution.station.dao;

import java.util.List;

import com.jd.bluedragon.distribution.station.domain.WorkStationAttendPlan;
import com.jd.bluedragon.distribution.station.query.WorkStationAttendPlanQuery;


/**
 * 岗位人员出勤计划表--Dao接口
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public interface WorkStationAttendPlanDao {

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	int insert(WorkStationAttendPlan insertData);
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	int updateById(WorkStationAttendPlan updateData);
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	int deleteById(WorkStationAttendPlan deleteData);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	WorkStationAttendPlan queryById(Long id);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	List<WorkStationAttendPlan> queryList(WorkStationAttendPlanQuery query);
	/**
	 * 按条件查询数量
	 * @param query
	 * @return
	 */
	long queryCount(WorkStationAttendPlanQuery query);
	/**
	 * 根据业务主键删除
	 * @param data
	 */
	int deleteByBusinessKey(WorkStationAttendPlan data);
	/**
	 * 根据业务主键查询
	 * @param data
	 * @return
	 */
	WorkStationAttendPlan queryByBusinessKey(WorkStationAttendPlan data);
	/**
	 * 查询场地班次列表
	 * @param query
	 * @return
	 */
	List<WorkStationAttendPlan> queryWaveDictList(WorkStationAttendPlanQuery query);

}
