package com.jd.bluedragon.distribution.station.jsf.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.api.WorkStationGridJsfService;
import com.jd.bluedragon.distribution.station.domain.WorkStationGrid;
import com.jd.bluedragon.distribution.station.query.WorkStationGridQuery;
import com.jd.bluedragon.distribution.station.service.WorkStationGridService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 工序岗位网格表--JsfService接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Slf4j
@Service("workStationGridJsfService")
public class WorkStationGridJsfServiceImpl implements WorkStationGridJsfService {

	@Autowired
	@Qualifier("workStationGridService")
	private WorkStationGridService workStationGridService;

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(WorkStationGrid insertData){
		return workStationGridService.insert(insertData);
	 }
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public Result<Boolean> updateById(WorkStationGrid updateData){
		return workStationGridService.updateById(updateData);
	 }
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	public Result<Boolean> deleteById(WorkStationGrid deleteData){
		return workStationGridService.deleteById(deleteData);
	 }
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Result<WorkStationGrid> queryById(Long id){
		return workStationGridService.queryById(id);
	 }
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public Result<PageDto<WorkStationGrid>> queryPageList(WorkStationGridQuery query){
		return workStationGridService.queryPageList(query);
	 }
	@Override
	public Result<List<WorkStationGrid>> queryAllGridBySiteCode(WorkStationGridQuery query) {
		return workStationGridService.queryAllGridBySiteCode(query);
	}
	@Override
	public Result<Boolean> importDatas(List<WorkStationGrid> dataList) {
		return workStationGridService.importDatas(dataList);
	}
	@Override
	public Result<List<WorkStationGrid>> queryGridWorkDictList(WorkStationGridQuery query) {
		return workStationGridService.queryGridWorkDictList(query);
	}
	@Override
	public Result<List<WorkStationGrid>> queryGridAreaDictList(WorkStationGridQuery query) {
		return workStationGridService.queryGridAreaDictList(query);
	}
	@Override
	public Result<List<WorkStationGrid>> queryGridNoDictList(WorkStationGridQuery query) {
		return workStationGridService.queryGridNoDictList(query);
	}
	@Override
	public Result<List<WorkStationGrid>> queryGridFloorDictList(WorkStationGridQuery query) {
		return workStationGridService.queryGridFloorDictList(query);
	}
}
