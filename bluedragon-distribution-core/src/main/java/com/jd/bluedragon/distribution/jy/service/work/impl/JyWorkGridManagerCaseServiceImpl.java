package com.jd.bluedragon.distribution.jy.service.work.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.common.dto.basedata.response.AttachmentDetailData;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCaseData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCaseItemData;
import com.jd.bluedragon.core.jsf.work.WorkGridManagerCaseJsfManager;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.dao.work.JyWorkGridManagerCaseDao;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCase;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCaseItem;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCaseQuery;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerCaseItemService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerCaseService;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskTypeEnum;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jdl.basic.api.domain.work.WorkGridManagerCaseItem;
import com.jdl.basic.api.domain.work.WorkGridManagerCaseWithItem;

/**
 * @ClassName: JyWorkGridManagerCaseServiceImpl
 * @Description: 巡检任务表-检查项--Service接口实现
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
@Service("jyWorkGridManagerCaseService")
public class JyWorkGridManagerCaseServiceImpl implements JyWorkGridManagerCaseService {

	private static final Logger logger = LoggerFactory.getLogger(JyWorkGridManagerCaseServiceImpl.class);

	@Autowired
	@Qualifier("jyWorkGridManagerCaseDao")
	private JyWorkGridManagerCaseDao jyWorkGridManagerCaseDao;
	
	@Autowired
	@Qualifier("workGridManagerCaseJsfManager")
	private WorkGridManagerCaseJsfManager workGridManagerCaseJsfManager;
	
	@Autowired
	@Qualifier("jyWorkGridManagerCaseItemService")
	private JyWorkGridManagerCaseItemService jyWorkGridManagerCaseItemService;
	
	@Autowired
	@Qualifier("jyAttachmentDetailService")
	private JyAttachmentDetailService jyAttachmentDetailService;
	
	@Override
	public List<JyWorkGridManagerCaseData> loadCaseListForTaskData(JyWorkGridManagerCaseQuery taskCaseQuery) {
		//查询任务实例-case列表
		List<JyWorkGridManagerCase> jyTaskCaseList = jyWorkGridManagerCaseDao.queryCaseListByBizId(taskCaseQuery.getBizId());
		//查询任务定义-case列表
		List<WorkGridManagerCaseWithItem> taskCaseList = workGridManagerCaseJsfManager.queryCaseWithItemListByTaskCode(taskCaseQuery.getTaskCode());
		return toCaseDataList(taskCaseQuery,taskCaseList,jyTaskCaseList);
	}
	private List<JyWorkGridManagerCaseData> toCaseDataList(JyWorkGridManagerCaseQuery taskCaseQuery,List<WorkGridManagerCaseWithItem> taskCaseList, List<JyWorkGridManagerCase> jyTaskCaseList){
		String bizId = taskCaseQuery.getBizId();
		Integer taskType = taskCaseQuery.getTaskType();
		List<JyWorkGridManagerCaseData> caseList = new ArrayList<>();
		//正常任务-case定义列表不会为空
		if(CollectionUtils.isEmpty(taskCaseList)) {
			return caseList;
		}
		//判断是否存在保存的数据
		boolean isSaved = !CollectionUtils.isEmpty(jyTaskCaseList);
		//会议：允许多个重复的case，以保存的数据列表为准
		if(WorkTaskTypeEnum.MEETING.getCode().equals(taskType)) {
			WorkGridManagerCaseWithItem caseModle = taskCaseList.get(0);
			if(isSaved) {
				for(JyWorkGridManagerCase jyTaskCase: jyTaskCaseList) {
					JyWorkGridManagerCaseData caseData = new JyWorkGridManagerCaseData();
					fillDataByTaskCase(caseData,caseModle);
					fillDataByJyTaskCase(caseData,jyTaskCase);
					caseList.add(caseData);
				}
			}else {
				JyWorkGridManagerCaseData caseData = new JyWorkGridManagerCaseData();
				fillDataByTaskCase(caseData,caseModle);
				caseList.add(caseData);
			}
		} else {
			//其他场景：以caseCode为准，设置数据相关状态
			Map<String,JyWorkGridManagerCase> caseMap = new HashMap<>();
			//caseCode-附件列表
			Map<String,List<AttachmentDetailData>> attachmentMap = new HashMap<>();
			//caseCode-item数据map
			Map<String,Map<String,JyWorkGridManagerCaseItem>> jyCaseItemMap = new HashMap<>();
			if(isSaved) {
				for(JyWorkGridManagerCase jyTaskCase: jyTaskCaseList) {
					caseMap.put(jyTaskCase.getCaseCode(), jyTaskCase);
				}
				//查询附件
				JyAttachmentDetailQuery query = new JyAttachmentDetailQuery();
				query.setSiteCode(taskCaseQuery.getSiteCode());
				query.setBizType(JyAttachmentBizTypeEnum.TASK_WORK_GRID_MANAGER.getCode());
				query.setBizId(bizId);
				query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT_ATTACHMENT_QUERY);
				List<JyAttachmentDetailEntity> attachmentList = jyAttachmentDetailService.queryDataListByCondition(query);
				if(!CollectionUtils.isEmpty(attachmentList)) {
					for(JyAttachmentDetailEntity attachmentDb : attachmentList) {
						String caseCode = attachmentDb.getBizSubType();
						if(!attachmentMap.containsKey(caseCode)) {
							attachmentMap.put(caseCode, new ArrayList<>());
						}
						AttachmentDetailData attachmentData = new AttachmentDetailData();
						BeanUtils.copyProperties(attachmentDb, attachmentData);
						attachmentMap.get(caseCode).add(attachmentData);
					}
				}
				
				//查询item数据
				List<JyWorkGridManagerCaseItem> jyItemList = jyWorkGridManagerCaseItemService.queryItemListByBizId(bizId);
				if(!CollectionUtils.isEmpty(jyItemList)) {
					for(JyWorkGridManagerCaseItem jyItem: jyItemList) {
						String caseCode = jyItem.getCaseCode();
						String caseItemCode = jyItem.getCaseItemCode();
						if(!jyCaseItemMap.containsKey(caseCode)) {
							jyCaseItemMap.put(caseCode, new HashMap<String,JyWorkGridManagerCaseItem>());
						}
						jyCaseItemMap.get(caseCode).put(caseItemCode, jyItem);
					}
				}
			}
			
			for(WorkGridManagerCaseWithItem caseModle: taskCaseList) {
				String caseCode = caseModle.getCaseCode();
				JyWorkGridManagerCaseData caseData = new JyWorkGridManagerCaseData();
				fillDataByTaskCase(caseData,caseModle);
				fillDataByJyTaskCase(caseData,caseMap.get(caseCode));
				fillItemData(caseData,caseModle,jyCaseItemMap.get(caseCode));
				if(attachmentMap.containsKey(caseCode)) {
					caseData.setAttachmentList(attachmentMap.get(caseCode));
				}
				caseList.add(caseData);
			}
		}
		return caseList;
	}
	private void fillItemData(JyWorkGridManagerCaseData caseData, WorkGridManagerCaseWithItem caseModle,
			Map<String, JyWorkGridManagerCaseItem> jyCaseItemMap) {
		if(CollectionUtils.isEmpty(caseModle.getCaseItemList())) {
			return;
		}
		List<JyWorkGridManagerCaseItemData> itemList = new ArrayList<>();
		caseData.setItemList(itemList);
		for(WorkGridManagerCaseItem caseItemModle : caseModle.getCaseItemList()) {
			JyWorkGridManagerCaseItemData itemData = new JyWorkGridManagerCaseItemData();
			itemData.setCaseCode(caseItemModle.getCaseCode());
			itemData.setCaseItemCode(caseItemModle.getCaseItemCode());
			itemData.setCaseItemText(caseItemModle.getCaseItemText());
			itemData.setCanSelect(caseItemModle.getCanSelect());
			itemData.setSelectFlag(caseItemModle.getSelectFlag());
			itemData.setOrderNum(caseItemModle.getOrderNum());
			//设置选中状态
			if(jyCaseItemMap != null 
					&& jyCaseItemMap.containsKey(caseItemModle.getCaseItemCode())) {
				itemData.setSelectFlag(jyCaseItemMap.get(caseItemModle.getCaseItemCode()).getSelectFlag());
			}
			itemList.add(itemData);
		}
	}
	private void fillDataByTaskCase(JyWorkGridManagerCaseData caseData,WorkGridManagerCaseWithItem taskCase) {
		if(taskCase == null) {
			return;
		}
		caseData.setCaseCode(taskCase.getCaseCode());
		caseData.setCaseName(taskCase.getCaseName());
		caseData.setCaseTitle(taskCase.getCaseTitle());
		caseData.setCaseContent(taskCase.getCaseContent());
		caseData.setNeedUploadPhoto(taskCase.getNeedUploadPhoto());
		caseData.setCheckResult(taskCase.getCheckResult());
	}
	private void fillDataByJyTaskCase(JyWorkGridManagerCaseData caseData,JyWorkGridManagerCase jytaskCase) {
		if(jytaskCase == null) {
			return;
		}
		caseData.setCaseTitle(jytaskCase.getCaseTitle());
		caseData.setCaseContent(jytaskCase.getCaseContent());
		caseData.setEditStatus(jytaskCase.getEditStatus());
		caseData.setCheckResult(jytaskCase.getCheckResult());
	}
	@Override
	public int batchInsert(List<JyWorkGridManagerCase> addCase) {
		if(CollectionUtils.isEmpty(addCase)) {
			return 0;
		}
		return jyWorkGridManagerCaseDao.batchInsert(addCase);
	}
	@Override
	public int batchUpdate(List<JyWorkGridManagerCase> updateCase) {
		if(CollectionUtils.isEmpty(updateCase)) {
			return 0;
		}
		int updateNum = 0;
		for(JyWorkGridManagerCase updateData: updateCase) {
			updateNum += jyWorkGridManagerCaseDao.updateById(updateData);
		}
		return updateNum;
	}	
}
