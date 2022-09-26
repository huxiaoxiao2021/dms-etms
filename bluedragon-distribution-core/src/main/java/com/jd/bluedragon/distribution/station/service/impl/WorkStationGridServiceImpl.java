package com.jd.bluedragon.distribution.station.service.impl;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.position.domain.PositionRecord;
import com.jd.bluedragon.distribution.position.service.PositionRecordService;
import com.jd.bluedragon.distribution.station.dao.WorkStationGridDao;
import com.jd.bluedragon.distribution.station.domain.DeleteRequest;
import com.jd.bluedragon.distribution.station.domain.WorkStation;
import com.jd.bluedragon.distribution.station.domain.WorkStationGrid;
import com.jd.bluedragon.distribution.station.domain.WorkStationGridCountVo;
import com.jd.bluedragon.distribution.station.query.WorkStationGridQuery;
import com.jd.bluedragon.distribution.station.service.WorkStationGridService;
import com.jd.bluedragon.distribution.station.service.WorkStationService;
import com.jd.bluedragon.distribution.utils.CheckHelper;
import com.jd.bluedragon.dms.utils.AreaEnum;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 工序岗位网格表--Service接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Slf4j
@Service("workStationGridService")
public class WorkStationGridServiceImpl implements WorkStationGridService {

	// 工序删除无限制人ERP
	@Value("${workStation.delete.noLimit.erps:}")
	private String noLimitErps;

	@Autowired
	@Qualifier("workStationGridDao")
	private WorkStationGridDao workStationGridDao;
	
	@Autowired
	@Qualifier("workStationService")
	WorkStationService workStationService;
	
	@Autowired
	private BaseMajorManager baseMajorManager;
	@Autowired
	private IGenerateObjectId genObjectId;

	@Autowired
	private PositionRecordService positionRecordService;
	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	@Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result<Boolean> insert(WorkStationGrid insertData){
		Result<Boolean> result = checkAndFillBeforeAdd(insertData);
		if(!result.isSuccess()) {
			return result;
		}
		insertData.setBusinessKey(generalBusinessKey());
		result.setData(workStationGridDao.insert(insertData) == 1);
		// 添加岗位记录
		addPosition(insertData);
		return result;
	 }

	private String generalBusinessKey() {
		return DmsConstants.CODE_PREFIX_WORK_STATION_GRID.concat(StringHelper.padZero(this.genObjectId.getObjectId(WorkStationGrid.class.getName()),11));
	}

	private void addPosition(WorkStationGrid insertData) {
		PositionRecord record = new PositionRecord();
		record.setSiteCode(insertData.getSiteCode());
		record.setRefGridKey(insertData.getBusinessKey());
		record.setCreateUser(insertData.getCreateUser());
		record.setUpdateUser(insertData.getUpdateUser());
		Result<Integer> result = positionRecordService.insertPosition(record);
		if(result == null || !Objects.equals(result.getData(), Constants.YN_YES)){
			throw new RuntimeException("添加岗位数据失败，岗位对应的业务主键是:" + insertData.getBusinessKey());
	}
	}

	/**
	 * 校验并填充数据add
	 * @param insertData
	 * @return
	 */
	private Result<Boolean> checkAndFillBeforeAdd(WorkStationGrid insertData){
		Result<Boolean> result = checkAndFillNewData(insertData);
		if(!result.isSuccess()) {
			return result;
		}
		if(this.isExist(insertData)) {
			return result.toFail("该场地网格已存在，请修改！");
		}
		return result;
	}
	/**
	 * 校验并填充数据
	 * @param data
	 * @return
	 */
	private Result<Boolean> checkAndFillNewData(WorkStationGrid data){
		Result<Boolean> result = Result.success();
		Integer siteCode = data.getSiteCode();
		Integer floor = data.getFloor();
		String gridNo = data.getGridNo();
		String workCode = data.getWorkCode();
		String areaCode = data.getAreaCode();
		String ownerUserErp = data.getOwnerUserErp();
		Integer standardNum = data.getStandardNum();
		if(siteCode == null) {
			return result.toFail("场地ID为空！");
		}
		if(!CheckHelper.checkInteger("楼层", floor, 1,5, result).isSuccess()) {
			return result;
		}
		//校验gridNo
		if(StringHelper.isEmpty(gridNo)) {
			return result.toFail("网格号为空！");
		}
		if(gridNo.length()<2) {
			gridNo = "0".concat(gridNo);
			data.setGridNo(gridNo);
		}
		if(!CheckHelper.checkGridNo(gridNo, result).isSuccess()) {
			return result;
		}
		if(!CheckHelper.checkStr("作业区ID", areaCode, 50, result).isSuccess()) {
			return result;
		}		
		if(!CheckHelper.checkStr("工序ID", workCode, 50, result).isSuccess()) {
			return result;
		}
		if(!CheckHelper.checkInteger("编制人数", standardNum, 1,1000000, result).isSuccess()) {
			return result;
		}
		if(!CheckHelper.checkStr("负责人ERP", ownerUserErp, 50, result).isSuccess()) {
			return result;
		}
		BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(siteCode);
		if(siteInfo == null) {
			return result.toFail("青龙ID在基础资料中不存在！");
		}
		data.setOrgCode(siteInfo.getOrgId());
		String orgName = AreaEnum.getAreaNameByCode(siteInfo.getOrgId());
		if(orgName == null) {
			orgName = siteInfo.getOrgName();
		}
		data.setOrgName(orgName);
		data.setSiteName(siteInfo.getSiteName());
		
		WorkStation workStationCheckQuery = new WorkStation();
		workStationCheckQuery.setWorkCode(workCode);
		workStationCheckQuery.setAreaCode(areaCode);
		Result<WorkStation> workStationData = workStationService.queryByBusinessKey(workStationCheckQuery);
		if(workStationData == null
				|| workStationData.getData() == null) {
			return result.toFail("作业区工序信息不存在，请先维护作业区及工序信息！");
		}
		WorkStation workStation = workStationData.getData();
		data.setGridCode(workStation.getAreaCode().concat("-").concat(data.getGridNo()));
		data.setGridName(workStation.getAreaName().concat("-").concat(data.getGridNo()));
		data.setRefStationKey(workStation.getBusinessKey());
		return result;
	}
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public Result<Boolean> updateById(WorkStationGrid updateData){
		Result<Boolean> result = Result.success();
		String ownerUserErp = updateData.getOwnerUserErp();
		Integer standardNum = updateData.getStandardNum();
		if(!CheckHelper.checkInteger("编制人数", standardNum, 1,1000000, result).isSuccess()) {
			return result;
		}
		if(!CheckHelper.checkStr("负责人ERP", ownerUserErp, 50, result).isSuccess()) {
			return result;
		}
		WorkStationGrid oldData = workStationGridDao.queryById(updateData.getId());
		if(oldData == null) {
			return result.toFail("该网格数据已变更，请重新查询后修改！");
		}
		workStationGridDao.deleteById(updateData);
		updateData.setId(null);
		result.setData(workStationGridDao.insert(updateData) == 1);
		return result;
	 }
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	@Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result<Boolean> deleteById(WorkStationGrid deleteData){
		Result<Boolean> result = Result.success();
		Result<WorkStationGrid> queryResult = queryById(deleteData.getId());
		if(queryResult.getData() == null || StringUtils.isEmpty(queryResult.getData().getBusinessKey())){
			throw new RuntimeException("根据id:" + deleteData.getId() + "未查询到数据!");
		}
		result.setData(workStationGridDao.deleteById(deleteData) == 1);
		// 同步删除岗位记录
		String businessKey = queryResult.getData().getBusinessKey();
		PositionRecord positionRecord = new PositionRecord();
		positionRecord.setRefGridKey(businessKey);
		positionRecord.setUpdateUser(deleteData.getUpdateUser());
		Result<Boolean> deletePositionResult = positionRecordService.deleteByBusinessKey(positionRecord);
		if(Objects.equals(deletePositionResult.getData(), false)){
			throw new RuntimeException("根据businessKey:" + businessKey + "删除岗位数据失败!");
		}
		return result;
	 }
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Result<WorkStationGrid> queryById(Long id){
		Result<WorkStationGrid> result = Result.success();
		result.setData(workStationGridDao.queryById(id));
		return result;
	 }
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public Result<PageDto<WorkStationGrid>> queryPageList(WorkStationGridQuery query){
		Result<PageDto<WorkStationGrid>> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		PageDto<WorkStationGrid> pageData = new PageDto<>(query.getPageNumber(), query.getPageSize());
		Long totalCount = workStationGridDao.queryCount(query);
		if(totalCount != null && totalCount > 0){
			pageData.setResult(workStationGridDao.queryList(query));
			pageData.setTotalRow(totalCount.intValue());
		}else {
			pageData.setResult(new ArrayList<WorkStationGrid>());
			pageData.setTotalRow(0);
		}
		result.setData(pageData);
		return result;
	 }
	/**
	 * 查询参数校验
	 * @param query
	 * @return
	 */
	public Result<Boolean> checkParamForQueryPageList(WorkStationGridQuery query){
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

	@Override
	public Result<List<WorkStationGrid>> queryAllGridBySiteCode(WorkStationGridQuery query) {
		Result<List<WorkStationGrid>> result = Result.success();
		result.setData(workStationGridDao.queryAllGridBySiteCode(query));
		return result;
	}

	@Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public Result<Boolean> importDatas(List<WorkStationGrid> dataList) {
		Result<Boolean> result = checkAndFillImportDatas(dataList);
		if(!result.isSuccess()) {
			return result;
		}
		//先删除后插入新记录
		for(WorkStationGrid data : dataList) {
			WorkStationGrid oldData = workStationGridDao.queryByBusinessKey(data);
			if(oldData != null) {
				oldData.setUpdateUser(data.getCreateUser());
				oldData.setUpdateUserName(data.getCreateUserName());
				oldData.setUpdateTime(data.getCreateTime());
				if(!Objects.equals(workStationGridDao.deleteById(oldData), Constants.YN_YES)){
					throw  new RuntimeException("根据id:" + oldData.getId() + "删除数据失败!");
				}
				data.setBusinessKey(oldData.getBusinessKey());
			}else {
				data.setBusinessKey(generalBusinessKey());
			}			
			if(!Objects.equals(workStationGridDao.insert(data), Constants.YN_YES)){
				throw  new RuntimeException("新增businessKey为:" + data.getBusinessKey() + "的数据失败");
			}
			// 同步处理岗位
			syncDealPosition(oldData, data);
		}
		return result;
	}

	private void syncDealPosition(WorkStationGrid oldData, WorkStationGrid newData) {
		if(oldData == null){
		addPosition(newData);
	}
	}

	/**
	 * 校验并填充导入数据
	 * @param dataList
	 * @return
	 */
	private Result<Boolean> checkAndFillImportDatas(List<WorkStationGrid> dataList){
		Result<Boolean> result = Result.success();
		if(CollectionUtils.isEmpty(dataList)) {
			return result.toFail("导入数据为空！");
		}
		//逐条校验
		int rowNum = 1;
		Map<String,Integer> uniqueKeysRowNumMap = new HashMap<String,Integer>();
		for(WorkStationGrid data : dataList) {
			String rowKey = "第" + rowNum + "行";
			Result<Boolean> result0 = checkAndFillNewData(data);
			if(!result0.isSuccess()) {
				return result0.toFail(rowKey + result0.getMessage());
			}
			//导入数据防重校验
			String uniqueKeysStr = getUniqueKeysStr(data);
			if(uniqueKeysRowNumMap.containsKey(uniqueKeysStr)) {
				return result0.toFail(rowKey + "和第"+uniqueKeysRowNumMap.get(uniqueKeysStr)+"行数据重复！");
			}
			uniqueKeysRowNumMap.put(uniqueKeysStr, rowNum);
			rowNum ++;
		}
		return result;
	}
	private String getUniqueKeysStr(WorkStationGrid data) {
		if(data != null ) {
			return data.getSiteCode().toString()
					.concat(DmsConstants.KEYS_SPLIT)
					.concat(data.getFloor().toString())	
					.concat(DmsConstants.KEYS_SPLIT)
					.concat(data.getGridNo())		
					.concat(DmsConstants.KEYS_SPLIT)
					.concat(data.getRefStationKey());
		}
		return null;
	}
	@Override
	public Result<WorkStationGrid> queryByBusinessKey(WorkStationGrid data) {
		Result<WorkStationGrid> result = Result.success();
		result.setData(workStationGridDao.queryByBusinessKey(data));
		return result;
	}
	@Override
	public boolean isExist(WorkStationGrid data) {
		return workStationGridDao.queryByBusinessKey(data) != null;
	}
	@Override
	public Result<List<WorkStationGrid>> queryGridWorkDictList(WorkStationGridQuery query) {
		Result<List<WorkStationGrid>> result = Result.success();
		result.setData(workStationGridDao.queryGridWorkDictList(query));
		return result;
	}
	@Override
	public Result<List<WorkStationGrid>> queryGridAreaDictList(WorkStationGridQuery query) {
		Result<List<WorkStationGrid>> result = Result.success();
		result.setData(workStationGridDao.queryGridAreaDictList(query));
		return result;
	}
	@Override
	public Result<List<WorkStationGrid>> queryGridNoDictList(WorkStationGridQuery query) {
		Result<List<WorkStationGrid>> result = Result.success();
		result.setData(workStationGridDao.queryGridNoDictList(query));
		return result;
	}
	@Override
	public Result<List<WorkStationGrid>> queryGridFloorDictList(WorkStationGridQuery query) {
		Result<List<WorkStationGrid>> result = Result.success();
		result.setData(workStationGridDao.queryGridFloorDictList(query));
		return result;
	}
	@Override
	public Result<WorkStationGridCountVo> queryPageCount(WorkStationGridQuery query) {
		Result<WorkStationGridCountVo> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		result.setData(workStationGridDao.queryPageCount(query));
		return result;
	}
	@Override
	public boolean hasGridData(String stationKey) {
		return workStationGridDao.queryCountByRefStationKey(stationKey) > 0;
	}
	@Override
	public Result<Boolean> deleteByIds(DeleteRequest<WorkStationGrid> deleteRequest) {
		Result<Boolean> result = Result.success();
		if(deleteRequest == null
				|| CollectionUtils.isEmpty(deleteRequest.getDataList())) {
			return result.toFail("参数错误，删除列表不能为空！");
		}
		List<WorkStationGrid> oldDataList = workStationGridDao.queryByIds(deleteRequest);
		if(CollectionUtils.isEmpty(oldDataList)
				|| oldDataList.size() < deleteRequest.getDataList().size()) {
			return result.toFail("参数错误，数据已变更请刷新列表后重新选择！");
		}
		for(WorkStationGrid oldData : oldDataList) {
			if(!ObjectUtils.equals(oldData.getSiteCode(), deleteRequest.getOperateSiteCode())
					&& !Arrays.asList(noLimitErps.split(Constants.SEPARATOR_COMMA)).contains(deleteRequest.getOperateUserCode())) {
				return result.toFail("网格【"+oldData.getGridName()+ "】非本人所在场地数据，无法删除！");
			}
		}
		result.setData(workStationGridDao.deleteByIds(deleteRequest) > 0);
		for(WorkStationGrid oldData : oldDataList) {
			// 同步删除岗位记录
			String businessKey = oldData.getBusinessKey();
			PositionRecord positionRecord = new PositionRecord();
			positionRecord.setRefGridKey(businessKey);
			positionRecord.setUpdateUser(deleteRequest.getOperateUserCode());
			Result<Boolean> deletePositionResult = positionRecordService.deleteByBusinessKey(positionRecord);
			if(Objects.equals(deletePositionResult.getData(), false)){
				throw new RuntimeException("根据businessKey:" + businessKey + "删除岗位数据失败!");
			}
		}
		return result;
	}
	@Override
	public Result<Long> queryCount(WorkStationGridQuery query) {
		Result<Long> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		result.setData(workStationGridDao.queryCount(query));
		return result;
	}
	@Override
	public Result<List<WorkStationGrid>> queryListForExport(WorkStationGridQuery query) {
		Result<List<WorkStationGrid>> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		result.setData(workStationGridDao.queryListForExport(query));
		return result;
	}
	@Override
	public List<WorkStationGrid> querySiteListByOrgCode(WorkStationGridQuery query) {
		return workStationGridDao.querySiteListByOrgCode(query);
	}
	@Override
	public List<String> queryOwnerUserErpListBySiteCode(WorkStationGridQuery query) {
		return workStationGridDao.queryOwnerUserErpListBySiteCode(query);
	}

	@Override
	public Result<WorkStationGrid> queryByGridKey(WorkStationGridQuery workStationGridQuery) {
		Result<WorkStationGrid> result = Result.success();
		result.setData(workStationGridDao.queryByGridKey(workStationGridQuery));
		return result;
	}
}
