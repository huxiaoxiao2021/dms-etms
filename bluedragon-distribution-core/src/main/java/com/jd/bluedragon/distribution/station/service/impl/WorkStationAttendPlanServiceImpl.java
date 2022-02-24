package com.jd.bluedragon.distribution.station.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.dao.WorkStationAttendPlanDao;
import com.jd.bluedragon.distribution.station.domain.WorkStation;
import com.jd.bluedragon.distribution.station.domain.WorkStationAttendPlan;
import com.jd.bluedragon.distribution.station.domain.WorkStationGrid;
import com.jd.bluedragon.distribution.station.query.WorkStationAttendPlanQuery;
import com.jd.bluedragon.distribution.station.service.WorkStationAttendPlanService;
import com.jd.bluedragon.distribution.station.service.WorkStationGridService;
import com.jd.bluedragon.distribution.station.service.WorkStationService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.wb.sdk.enums.station.WaveTypeEnum;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 岗位人员出勤计划表--Service接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Slf4j
@Service("workStationAttendPlanService")
public class WorkStationAttendPlanServiceImpl implements WorkStationAttendPlanService {

	@Autowired
	@Qualifier("workStationAttendPlanDao")
	private WorkStationAttendPlanDao workStationAttendPlanDao;
	
	@Autowired
	@Qualifier("workStationService")
	WorkStationService workStationService;
	
	@Autowired
	@Qualifier("workStationGridService")
	WorkStationGridService workStationGridService;	
	
	@Autowired
	private BaseMajorManager baseMajorManager;

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(WorkStationAttendPlan insertData){
		Result<Boolean> result = checkAndFillBeforeAdd(insertData);
		if(!result.isSuccess()) {
			return result;
		}
		result.setData(workStationAttendPlanDao.insert(insertData) == 1);
		return result;
	 }
	/**
	 * 校验并填充数据add
	 * @param insertData
	 * @return
	 */
	private Result<Boolean> checkAndFillBeforeAdd(WorkStationAttendPlan insertData){
		Result<Boolean> result = checkAndFillNewData(insertData);
		if(!result.isSuccess()) {
			return result;
		}
		if(this.isExist(insertData)) {
			return result.toFail("该场地网格人员计划已存在，请修改！");
		}
		return result;
	}	
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public Result<Boolean> updateById(WorkStationAttendPlan updateData){
		Result<Boolean> result = Result.success();
		workStationAttendPlanDao.deleteById(updateData);
		updateData.setId(null);		
		result.setData(workStationAttendPlanDao.insert(updateData) == 1);
		return result;
	 }
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	public Result<Boolean> deleteById(WorkStationAttendPlan deleteData){
		Result<Boolean> result = Result.success();
		result.setData(workStationAttendPlanDao.deleteById(deleteData) == 1);
		return result;
	 }
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Result<WorkStationAttendPlan> queryById(Long id){
		Result<WorkStationAttendPlan> result = Result.success();
		result.setData(this.fillOtherInfo(workStationAttendPlanDao.queryById(id)));
		return result;
	 }
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public Result<PageDto<WorkStationAttendPlan>> queryPageList(WorkStationAttendPlanQuery query){
		Result<PageDto<WorkStationAttendPlan>> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		PageDto<WorkStationAttendPlan> pageData = new PageDto<>(query.getPageNumber(), query.getPageSize());
		Long totalCount = workStationAttendPlanDao.queryCount(query);
		if(totalCount != null && totalCount > 0){
			List<WorkStationAttendPlan> dataList = workStationAttendPlanDao.queryList(query);
			if(CollectionUtils.isNotEmpty(dataList)) {
				for(WorkStationAttendPlan tmp: dataList) {
					this.fillOtherInfo(tmp);
				}
			}
			pageData.setResult(dataList);
			pageData.setTotalRow(totalCount.intValue());
		}else {
			pageData.setResult(new ArrayList<WorkStationAttendPlan>());
			pageData.setTotalRow(0);
		}
		
		result.setData(pageData);
		return result;
	 }
	/**
	 * 补充其他信息
	 * @param vo
	 */
	private WorkStationAttendPlan fillOtherInfo(WorkStationAttendPlan data) {
		if(data != null) {
			data.setWaveName(WaveTypeEnum.getNameByCode(data.getWaveCode()));
		}
		return data;
	}
	/**
	 * 查询参数校验
	 * @param query
	 * @return
	 */
	public Result<Boolean> checkParamForQueryPageList(WorkStationAttendPlanQuery query){
		Result<Boolean> result = Result.success();
		if(query.getPageSize() == null
				|| query.getPageSize() <= 0) {
			query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT);
		}
		query.setOffset(0);
		query.setLimit(query.getPageSize());
		if(query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
		}
		
		return result;
	 }
	
	private boolean isExist(WorkStationAttendPlan data) {
		return workStationAttendPlanDao.queryByBusinessKey(data) != null;
	}
	@Override
	public Result<Boolean> importDatas(List<WorkStationAttendPlan> dataList) {
		Result<Boolean> result = checkAndFillImportDatas(dataList);
		if(!result.isSuccess()) {
			return result;
		}
		//先删除后插入新记录
		for(WorkStationAttendPlan data : dataList) {
			workStationAttendPlanDao.deleteByBusinessKey(data);
			workStationAttendPlanDao.insert(data);
		}
		return result;
	}
	/**
	 * 校验并填充导入数据
	 * @param dataList
	 * @return
	 */
	private Result<Boolean> checkAndFillImportDatas(List<WorkStationAttendPlan> dataList){
		Result<Boolean> result = Result.success();
		if(CollectionUtils.isEmpty(dataList)) {
			return result.toFail("导入数据为空！");
		}
		//逐条校验
		int rowNum = 1;
		for(WorkStationAttendPlan data : dataList) {
			String rowKey = "第" + rowNum + "行";
			Result<Boolean> result0 = checkAndFillNewData(data);
			if(!result0.isSuccess()) {
				return result0.toFail(rowKey + result0.getMessage());
			}
			rowNum ++;
		}
		return result;
	}
	/**
	 * 校验并填充导入数据
	 * @param dataList
	 * @return
	 */
	private Result<Boolean> checkAndFillNewData(WorkStationAttendPlan data){
		Result<Boolean> result = Result.success();
		Integer siteCode = data.getSiteCode();
		String gridNo = data.getGridNo();
		String workCode = data.getWorkCode();
		Integer waveCode = data.getWaveCode();
		if(siteCode == null
				|| siteCode == 0) {
			return result.toFail("青龙ID为空！");
		}
		if(StringHelper.isEmpty(gridNo)) {
			return result.toFail("网格ID为空！");
		}
		if(StringHelper.isEmpty(workCode)) {
			return result.toFail("工序编码为空！");
		}
		if(WaveTypeEnum.getEnum(waveCode) == null) {
			return result.toFail("班次类型只能录入【"+WaveTypeEnum.getAllNames()+"】！");
		}
		BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(siteCode);
		if(siteInfo == null) {
			return result.toFail("青龙ID在基础资料中不存在！");
		}
		data.setOrgCode(siteInfo.getOrgId());
		
		WorkStation workStationCheckQuery = new WorkStation();
		workStationCheckQuery.setWorkCode(workCode);
		if(!workStationService.isExist(workStationCheckQuery)) {
			return result.toFail("工序岗位信息不存在，请先维护岗位信息！");
		}
		WorkStationGrid workStationGridCheckQuery = new WorkStationGrid();
		workStationGridCheckQuery.setSiteCode(siteCode);
		workStationGridCheckQuery.setGridNo(gridNo);
		workStationGridCheckQuery.setWorkCode(workCode);
		if(!workStationGridService.isExist(workStationGridCheckQuery)) {
			return result.toFail("网格信息不存在，请先维护场地网格信息！");
		}
		return result;
	}
	@Override
	public Result<List<WorkStationAttendPlan>> queryWaveDictList(WorkStationAttendPlanQuery query) {
		Result<List<WorkStationAttendPlan>> result = Result.success();
		result.setData(workStationAttendPlanDao.queryWaveDictList(query));
		return result;
	}
}
