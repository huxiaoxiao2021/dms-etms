package com.jd.bluedragon.distribution.jy.service.work.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.response.AttachmentDetailData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCaseData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCaseItemData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerData;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCaseQuery;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerCaseService;
import com.jd.bluedragon.distribution.jy.work.enums.WorkCheckResultEnum;
import com.jd.bluedragon.distribution.work.domain.*;
import com.jd.bluedragon.utils.BaseContants;
import com.jd.bluedragon.utils.BeanHelper;
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
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jdl.basic.api.enums.WorkGridManagerTaskBizType;

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

	@Autowired
	@Qualifier("jyBizTaskWorkGridManagerService")
	private JyBizTaskWorkGridManagerService jyBizTaskWorkGridManagerService;

	@Autowired
	@Qualifier("jyWorkGridManagerCaseService")
	private JyWorkGridManagerCaseService jyWorkGridManagerCaseService;

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
		if (WorkCheckResultEnum.UNDO.getCode().equals(taskData.getIsMatch())) {
			taskData.setIsMatchName(WorkCheckResultEnum.UNDO.getName());
		} else if (WorkCheckResultEnum.PASS.getCode().equals(taskData.getIsMatch())) {
			taskData.setIsMatchName(WorkCheckResultEnum.PASS.getName());
		} else if (WorkCheckResultEnum.UNPASS.getCode().equals(taskData.getIsMatch())) {
			taskData.setIsMatchName(WorkCheckResultEnum.UNPASS.getName());
		}
		//省区位物流总部的时候显示相应的枢纽
		if (BaseContants.LOGISTICS_HEADQUARTERS.equals(taskData.getProvinceAgencyCode())) {
			taskData.setProvinceAgencyName(taskData.getAreaHubName());
		}
		if(jyTaskData.getTaskBizType() != null){
			taskData.setTaskBizTypeName(WorkGridManagerTaskBizType.getNameByCode(jyTaskData.getTaskBizType()));
		}
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

	/**
	 * 根据bizId调后端获取任务详细信息
	 *
	 * @param query
	 * @return
	 */
	@Override
	public Result<WorkGridManagerReportVo> queryTaskDataByBizId(WorkGridMangerReportDetailQuery query) {
		Result<WorkGridManagerReportVo> result = new Result<WorkGridManagerReportVo>();
		result.toSuccess("查询成功！");
		JyWorkGridManagerData taskData = jyBizTaskWorkGridManagerService.queryTaskDataByBizId(query.getBizId());
		if (taskData == null) {
			result.toFail("任务数据不存在！");
			return result;
		}
		JyWorkGridManagerCaseQuery taskCaseQuery = new JyWorkGridManagerCaseQuery();
		taskCaseQuery.setBizId(taskData.getBizId());
		taskCaseQuery.setSiteCode(taskData.getSiteCode());
		taskCaseQuery.setTaskType(taskData.getTaskType());
		taskCaseQuery.setTaskCode(taskData.getTaskCode());
		List<JyWorkGridManagerCaseData> caseList = jyWorkGridManagerCaseService.loadCaseListForTaskData(taskCaseQuery);
		taskData.setCaseList(caseList);
		//数据转化 JyWorkGridManagerData 转化为 WorkGridManagerReportVo
		WorkGridManagerReportVo workGridManagerReportVo = covertWorkGridManagerReportVo(taskData);
		result.setData(workGridManagerReportVo);
		return result;

	}

	/**
	 * 数据转化
	 * JyWorkGridManagerData 转化为 WorkGridManagerReportVo
	 *
	 * @param taskData
	 * @return
	 */
	private WorkGridManagerReportVo covertWorkGridManagerReportVo(JyWorkGridManagerData taskData) {
		WorkGridManagerReportVo workGridManagerReportVo = new WorkGridManagerReportVo();
		BeanUtils.copyProperties(taskData, workGridManagerReportVo);
		//指标任务扩展信息
		if(taskData.getBusinessQuotaInfoData() != null){
			workGridManagerReportVo.setBusinessQuotaInfoData(new BusinessQuotaInfoData());
			BeanUtils.copyProperties(taskData.getBusinessQuotaInfoData(), workGridManagerReportVo.getBusinessQuotaInfoData());
		}
		//暴力分拣任务扩展信息
		if(taskData.getViolenceSortInfoData() != null){
			workGridManagerReportVo.setViolenceSortInfoData(new ViolenceSortInfoData());
			BeanUtils.copyProperties(taskData.getViolenceSortInfoData(), workGridManagerReportVo.getViolenceSortInfoData());
		}
		
		List<JyWorkGridManagerCaseDataVO> jyWorkGridManagerCaseDataVOList = new ArrayList<JyWorkGridManagerCaseDataVO>();
		if (!CollectionUtils.isEmpty(taskData.getCaseList())) {
			for (JyWorkGridManagerCaseData jyWorkGridManagerCaseData : taskData.getCaseList()) {
				JyWorkGridManagerCaseDataVO jyWorkGridManagerCaseDataVO = new JyWorkGridManagerCaseDataVO();
				List<AttachmentDetailDataVO> attachmentDetailDataVOList = new ArrayList<AttachmentDetailDataVO>();
				List<AttachmentDetailDataVO> improveAttachmentList = new ArrayList<AttachmentDetailDataVO>();
				List<JyWorkGridManagerCaseItemDataVO> jyWorkGridManagerCaseItemDataVOList = new ArrayList<JyWorkGridManagerCaseItemDataVO>();
				//AttachmentDetailData 转为 AttachmentDetailDataVO
				if (!CollectionUtils.isEmpty(jyWorkGridManagerCaseData.getAttachmentList())) {
					for (AttachmentDetailData detailData : jyWorkGridManagerCaseData.getAttachmentList()) {
						AttachmentDetailDataVO attachmentDetailDataVO = new AttachmentDetailDataVO();
						BeanUtils.copyProperties(detailData, attachmentDetailDataVO);
						attachmentDetailDataVOList.add(attachmentDetailDataVO);
					}
				}
				//AttachmentDetailData 转为 AttachmentDetailDataVO
				if (!CollectionUtils.isEmpty(jyWorkGridManagerCaseData.getImproveAttachmentList())) {
					for (AttachmentDetailData detailData : jyWorkGridManagerCaseData.getImproveAttachmentList()) {
						AttachmentDetailDataVO attachmentDetailDataVO = new AttachmentDetailDataVO();
						BeanUtils.copyProperties(detailData, attachmentDetailDataVO);
						improveAttachmentList.add(attachmentDetailDataVO);
					}
				}
				//JyWorkGridManagerCaseItemData 转为 JyWorkGridManagerCaseItemDataVO
				if (!CollectionUtils.isEmpty(jyWorkGridManagerCaseData.getItemList())) {
					for (JyWorkGridManagerCaseItemData jyWorkGridManagerCaseItemData : jyWorkGridManagerCaseData.getItemList()) {
						JyWorkGridManagerCaseItemDataVO jyWorkGridManagerCaseItemDataVO = new JyWorkGridManagerCaseItemDataVO();
						BeanUtils.copyProperties(jyWorkGridManagerCaseItemData, jyWorkGridManagerCaseItemDataVO);
						jyWorkGridManagerCaseItemDataVOList.add(jyWorkGridManagerCaseItemDataVO);
					}
				}
				//JyWorkGridManagerCaseData 转为 JyWorkGridManagerCaseDataVO
				BeanUtils.copyProperties(jyWorkGridManagerCaseData, jyWorkGridManagerCaseDataVO);
				jyWorkGridManagerCaseDataVO.setAttachmentList(attachmentDetailDataVOList);
				jyWorkGridManagerCaseDataVO.setImproveAttachmentList(improveAttachmentList);
				jyWorkGridManagerCaseDataVO.setItemList(jyWorkGridManagerCaseItemDataVOList);
				jyWorkGridManagerCaseDataVOList.add(jyWorkGridManagerCaseDataVO);
			}
		}
		workGridManagerReportVo.setCaseList(jyWorkGridManagerCaseDataVOList);
		return workGridManagerReportVo;
	}
}
