package com.jd.bluedragon.distribution.station.jsf.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.api.WorkStationAttendPlanJsfService;
import com.jd.bluedragon.distribution.station.domain.WorkStationAttendPlan;
import com.jd.bluedragon.distribution.station.query.WorkStationAttendPlanQuery;
import com.jd.bluedragon.distribution.station.service.WorkStationAttendPlanService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 岗位人员出勤计划表--JsfService接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Slf4j
@Service("workStationAttendPlanJsfService")
public class WorkStationAttendPlanJsfServiceImpl implements WorkStationAttendPlanJsfService {

	@Autowired
	@Qualifier("workStationAttendPlanService")
	private WorkStationAttendPlanService workStationAttendPlanService;

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(WorkStationAttendPlan insertData){
		return workStationAttendPlanService.insert(insertData);
	 }
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public Result<Boolean> updateById(WorkStationAttendPlan updateData){
		return workStationAttendPlanService.updateById(updateData);
	 }
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	public Result<Boolean> deleteById(WorkStationAttendPlan deleteData){
		return workStationAttendPlanService.deleteById(deleteData);
	 }
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Result<WorkStationAttendPlan> queryById(Long id){
		return workStationAttendPlanService.queryById(id);
	 }
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public Result<PageDto<WorkStationAttendPlan>> queryPageList(WorkStationAttendPlanQuery query){
		return workStationAttendPlanService.queryPageList(query);
	 }
	@Override
	public Result<Boolean> importDatas(List<WorkStationAttendPlan> dataList) {
		return workStationAttendPlanService.importDatas(dataList);
	}
	@Override
	public Result<List<WorkStationAttendPlan>> queryWaveDictList(WorkStationAttendPlanQuery query) {
		return workStationAttendPlanService.queryWaveDictList(query);
	}

}
