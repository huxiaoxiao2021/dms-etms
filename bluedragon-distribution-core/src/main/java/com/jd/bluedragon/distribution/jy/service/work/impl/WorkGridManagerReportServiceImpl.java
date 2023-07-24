package com.jd.bluedragon.distribution.jy.service.work.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.dao.work.JyBizTaskWorkGridManagerDao;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManager;
import com.jd.bluedragon.distribution.jy.service.work.WorkGridManagerReportService;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskStatusEnum;
import com.jd.bluedragon.distribution.work.domain.WorkGridManagerReportQuery;
import com.jd.bluedragon.distribution.work.domain.WorkGridManagerReportVo;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * @ClassName: JyBizTaskWorkGridManagerServiceImpl
 * @Description: 巡检任务表--Service接口实现
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
@Service("workGridManagerReportService")
public class WorkGridManagerReportServiceImpl implements WorkGridManagerReportService {

	private static final Logger logger = LoggerFactory.getLogger(WorkGridManagerReportServiceImpl.class);

	@Autowired
	@Qualifier("jyBizTaskWorkGridManagerDao")
	private JyBizTaskWorkGridManagerDao jyBizTaskWorkGridManagerDao;
	
	@Value("${beans.workGridManagerReportService.queryRangeDays:6}")
	private int queryRangeDays;

	private Result<Boolean> checkAndInitQuery(WorkGridManagerReportQuery query) {
		Result<Boolean> result = Result.success();
		if(query == null) {
    		return result.toFail("查询参数不能为空！");
    	}
		if(query.getPageSize() == null || query.getPageSize() <= 0) {
			query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT);
		};
		query.setOffset(0);
		query.setLimit(query.getPageSize());
		if(query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
		};
		//设置时间范围
		Date startTime = null;
		Date endTime = 	null;	
		if(StringUtils.isNotBlank(query.getTaskDateStartStr())) {
			startTime = DateHelper.parseDate(query.getTaskDateStartStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
		}
		if(StringHelper.isNotEmpty(query.getTaskDateEndStr())) {
			endTime = DateHelper.parseDate(query.getTaskDateEndStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
		}
		//有日期查询范围，校验是否超过7天
		if(startTime != null && endTime != null) {
			Date checkDate = DateHelper.addDate(startTime,queryRangeDays);
			if(endTime.after(checkDate)) {
				return result.toFail("查询日期范围不能超过" + (queryRangeDays + 1) + "天");
			}

		}else {
			endTime = new Date();
			startTime = DateHelper.addDate(endTime,-queryRangeDays);
		}
		query.setTaskDateStart(startTime);
		query.setTaskDateEnd(endTime);
		return result;
	}
	@Override
	public Result<Integer> queryCountForExport(WorkGridManagerReportQuery query) {
		Result<Integer> result = Result.success(); 
		result.setData(0);
		Result<Boolean> checkResult = this.checkAndInitQuery(query);
		if(!checkResult.isSuccess()){
		    return result.toFail(checkResult.getMessage());
		}
		Integer countNum  = jyBizTaskWorkGridManagerDao.queryCountForReport(query);
		if(countNum != null) {
			result.setData(countNum);
			return result;
		}
		return result;
	}
	@Override
	public Result<List<WorkGridManagerReportVo>> queryListForExport(
			WorkGridManagerReportQuery query) {
		Result<List<WorkGridManagerReportVo>> result = Result.success(); 
		List<WorkGridManagerReportVo> dataList = new ArrayList<>();
		result.setData(dataList);
		Result<Boolean> checkResult = this.checkAndInitQuery(query);
		if(!checkResult.isSuccess()){
		    return result.toFail(checkResult.getMessage());
		}
		List<JyBizTaskWorkGridManager> jyDataList  = jyBizTaskWorkGridManagerDao.queryListForReport(query);
		if(CollectionUtils.isEmpty(jyDataList)) {
			return result;
		}
		for(JyBizTaskWorkGridManager jyTaskData: jyDataList) {
			dataList.add(toWorkGridManagerReportVo(jyTaskData));
		}
		return result;
	}
	private WorkGridManagerReportVo toWorkGridManagerReportVo(JyBizTaskWorkGridManager jyTaskData) {
		WorkGridManagerReportVo taskData  = new WorkGridManagerReportVo();
		BeanUtils.copyProperties(jyTaskData, taskData);
		taskData.setStatusName(WorkTaskStatusEnum.getNameByCode(taskData.getStatus()));
		return taskData;
	}	
	@Override
	public Result<PageDto<WorkGridManagerReportVo>> queryPageList(WorkGridManagerReportQuery query) {
		Result<PageDto<WorkGridManagerReportVo>> result = Result.success();
		Result<Boolean> checkResult = this.checkAndInitQuery(query);
		if(!checkResult.isSuccess()){
		    return result.toFail(checkResult.getMessage());
		}
		PageDto<WorkGridManagerReportVo> pageData = new PageDto<>(query.getPageNumber(), query.getPageSize());
		Integer totalCount = jyBizTaskWorkGridManagerDao.queryCountForReport(query);
		List<WorkGridManagerReportVo> voList = new ArrayList<WorkGridManagerReportVo>();
		if(totalCount != null && totalCount > 0){
			List<JyBizTaskWorkGridManager> dataList = jyBizTaskWorkGridManagerDao.queryListForReport(query);
			if(!CollectionUtils.isEmpty(dataList)) {
				for(JyBizTaskWorkGridManager tmp: dataList) {
					voList.add(toWorkGridManagerReportVo(tmp));
				}
			}
			pageData.setResult(voList);
			pageData.setTotalRow(totalCount.intValue());
		}else {
			pageData.setResult(voList);
			pageData.setTotalRow(0);
		}
		
		result.setData(pageData);
		return result;
	}
}
