package com.jd.bluedragon.distribution.station.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.station.enums.BusinessLineTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.dao.WorkStationDao;
import com.jd.bluedragon.distribution.station.domain.DeleteRequest;
import com.jd.bluedragon.distribution.station.domain.WorkStation;
import com.jd.bluedragon.distribution.station.domain.WorkStationAttendPlan;
import com.jd.bluedragon.distribution.station.domain.WorkStationCountVo;
import com.jd.bluedragon.distribution.station.query.WorkStationQuery;
import com.jd.bluedragon.distribution.station.service.WorkStationGridService;
import com.jd.bluedragon.distribution.station.service.WorkStationService;
import com.jd.bluedragon.distribution.utils.CheckHelper;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * @ClassName: WorkStationServiceImpl
 * @Description: 网格工序信息表--Service接口实现
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
@Service("workStationService")
public class WorkStationServiceImpl implements WorkStationService {

	private static final Logger logger = LoggerFactory.getLogger(WorkStationServiceImpl.class);

	@Autowired
	@Qualifier("workStationDao")
	private WorkStationDao workStationDao;
	
	@Autowired
	private IGenerateObjectId genObjectId;
	@Autowired
	@Qualifier("workStationGridService")
	private WorkStationGridService workStationGridService;

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(WorkStation insertData){
		Result<Boolean> result = checkAndFillBeforeAdd(insertData);
		if(!result.isSuccess()) {
			return result;
		}
		generateAndSetBusinessKey(insertData);
		result.setData(workStationDao.insert(insertData) == 1);
		return result;
	 }
	@Override
	public Result<Boolean> importDatas(List<WorkStation> dataList) {
		Result<Boolean> result = checkImportDatas(dataList);
		if(!result.isSuccess()) {
			return result;
		}		
		//先删除后插入新记录
		for(WorkStation data : dataList) {
			WorkStation oldData = workStationDao.queryByBusinessKey(data);
			if(oldData != null) {
				oldData.setUpdateUser(data.getCreateUser());
				oldData.setUpdateUserName(data.getCreateUserName());
				oldData.setUpdateTime(data.getCreateTime());
				workStationDao.deleteById(oldData);
				data.setBusinessKey(oldData.getBusinessKey());
			}else {
				generateAndSetBusinessKey(data);
			}
			data.setBusinessLineName(BusinessLineTypeEnum.getNameByCode(data.getBusinessLineCode()));
			workStationDao.insert(data);
		}
		return result;
	}
	/**
	 * 校验并填充数据add
	 * @param insertData
	 * @return
	 */
	private Result<Boolean> checkAndFillBeforeAdd(WorkStation insertData){
		Result<Boolean> result = checkAndFillNewData(insertData);
		if(!result.isSuccess()) {
			return result;
		}
		if(this.isExist(insertData)) {
			return result.toFail("该作业区、工序已存在，请修改！");
		}
		return result;
	}	
	/**
	 * 校验并填充导入数据
	 * @param dataList
	 * @return
	 */
	private Result<Boolean> checkImportDatas(List<WorkStation> dataList){
		Result<Boolean> result = Result.success();
		if(CollectionUtils.isEmpty(dataList)) {
			return result.toFail("导入数据为空！");
		}
		//逐条校验
		int rowNum = 1;
		Map<String,Integer> uniqueKeysRowNumMap = new HashMap<String,Integer>();
		for(WorkStation data : dataList) {
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
			if(BusinessLineTypeEnum.getEnum(data.getBusinessLineCode()) == null){
				return result0.toFail(rowKey + "的【业务条线ID】不符合要求！");
			}
			uniqueKeysRowNumMap.put(uniqueKeysStr, rowNum);
			rowNum ++;
		}
		return result;
	}
	private String getUniqueKeysStr(WorkStation data) {
		if(data != null ) {
			return data.getAreaCode()
					.concat(DmsConstants.KEYS_SPLIT)
					.concat(data.getWorkCode());
		}
		return null;
	}
	/**
	 * 校验并填充数据
	 * @param data
	 * @return
	 */
	private Result<Boolean> checkAndFillNewData(WorkStation data){
		Result<Boolean> result = Result.success();
		String workCode = data.getWorkCode();
		String workName = data.getWorkName();
		String areaCode = data.getAreaCode();
		String areaName = data.getAreaName();
		data.setBusinessLineName(BusinessLineTypeEnum.getNameByCode(data.getBusinessLineCode()));
		
		if(!CheckHelper.checkStr("作业区ID", areaCode, 50, result).isSuccess()) {
			return result;
		}		
		if(!CheckHelper.checkStr("作业区名称", areaName, 100, result).isSuccess()) {
			return result;
		}
		if(!CheckHelper.checkStr("工序ID", workCode, 50, result).isSuccess()) {
			return result;
		}
		if(!CheckHelper.checkStr("工序名称", workName, 100, result).isSuccess()) {
			return result;
		}
		return result;
	}
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public Result<Boolean> updateById(WorkStation updateData){
		Result<Boolean> result = checkAndFillNewData(updateData);
		if(!result.isSuccess()) {
			return result;
		}
		WorkStation oldData = workStationDao.queryById(updateData.getId());
		if(oldData == null) {
			return result.toFail("该工序数据已变更，请重新查询后修改！");
		}
		workStationDao.deleteById(updateData);
		updateData.setId(null);
		updateData.setBusinessLineName(BusinessLineTypeEnum.getNameByCode(updateData.getBusinessLineCode()));
		result.setData(workStationDao.insert(updateData) == 1);
		return result;
	 }
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	public Result<Boolean> deleteById(WorkStation deleteData){
		Result<Boolean> result = Result.success();
		if(deleteData == null
				|| deleteData.getId() == null) {
			return result.toFail("参数错误，id不能为空！");
		}
		WorkStation oldData = workStationDao.queryById(deleteData.getId());
		if(oldData == null) {
			return result.toFail("参数错误，无效的数据id！");
		}
		if(workStationGridService.hasGridData(oldData.getBusinessKey())) {
			return result.toFail("该工序存在场地网格记录，无法删除！");
		}
		result.setData(workStationDao.deleteById(deleteData) == 1);
		return result;
	 }
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Result<WorkStation> queryById(Long id){
		Result<WorkStation> result = Result.success();
		result.setData(workStationDao.queryById(id));
		return result;
	 }
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public Result<PageDto<WorkStation>> queryPageList(WorkStationQuery query){
		Result<PageDto<WorkStation>> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		PageDto<WorkStation> pageData = new PageDto<>(query.getPageNumber(), query.getPageSize());
		Long totalCount = workStationDao.queryCount(query);
		if(totalCount != null && totalCount > 0){
			pageData.setResult(workStationDao.queryList(query));
			pageData.setTotalRow(totalCount.intValue());
		}else {
			pageData.setResult(new ArrayList<WorkStation>());
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
	public Result<Boolean> checkParamForQueryPageList(WorkStationQuery query){
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
	public Result<List<WorkStation>> queryWorkDictList(WorkStationQuery query) {
		Result<List<WorkStation>> result = Result.success();
		result.setData(workStationDao.queryWorkDictList(query));
		return result;
	}
	@Override
	public Result<List<WorkStation>> queryAreaDictList(WorkStationQuery query) {
		Result<List<WorkStation>> result = Result.success();
		result.setData(workStationDao.queryAreaDictList(query));
		return result;
	}
	@Override
	public Result<WorkStation> queryByBusinessKey(WorkStation data) {
		Result<WorkStation> result = Result.success();
		result.setData(workStationDao.queryByBusinessKey(data));
		return result;		
	}

	@Override
	public Result<WorkStation> queryByRealBusinessKey(String businessKey) {
		Result<WorkStation> result = Result.success();
		if(StringUtils.isEmpty(businessKey)){
			result.toFail("业务主键不能为空!");
			return result;
		}
		result.setData(workStationDao.queryByRealBusinessKey(businessKey));
		return result;
	}

	@Override
	public boolean isExist(WorkStation data) {
		return workStationDao.queryByBusinessKey(data) != null;
	}
	@Override
	public Result<WorkStationCountVo> queryPageCount(WorkStationQuery query) {
		Result<WorkStationCountVo> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		result.setData(workStationDao.queryPageCount(query));
		return result;
	}
	/**
	 * 生成并设置businessKey
	 * @param data
	 */
	private void generateAndSetBusinessKey(WorkStation data) {
		if(data != null) {
			data.setBusinessKey(DmsConstants.CODE_PREFIX_WORK_STATION.concat(StringHelper.padZero(this.genObjectId.getObjectId(WorkStation.class.getName()),11)));
		}
	}
	@Override
	public Result<Boolean> deleteByIds(DeleteRequest<WorkStation> deleteRequest) {
		Result<Boolean> result = Result.success();
		if(deleteRequest == null
				|| CollectionUtils.isEmpty(deleteRequest.getDataList())) {
			return result.toFail("参数错误，删除列表不能为空！");
		}
		List<WorkStation> oldDataList = workStationDao.queryByIds(deleteRequest);
		if(CollectionUtils.isEmpty(oldDataList)
				|| oldDataList.size() < deleteRequest.getDataList().size()) {
			return result.toFail("参数错误，数据已变更请刷新列表后重新选择！");
		}
		for(WorkStation oldData : oldDataList) {
			if(workStationGridService.hasGridData(oldData.getBusinessKey())) {
				return result.toFail("工序【"+oldData.getWorkName()+ "】存在场地网格记录，无法删除！");
			}
		}
		result.setData(workStationDao.deleteByIds(deleteRequest) > 0);
		return result;
	}
	@Override
	public Result<Long> queryCount(WorkStationQuery query) {
		Result<Long> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		result.setData(workStationDao.queryCount(query));
		return result;
	}
	@Override
	public Result<List<WorkStation>> queryListForExport(WorkStationQuery query) {
		Result<List<WorkStation>> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		result.setData(workStationDao.queryListForExport(query));
		return result;
	}
}
