package com.jd.bluedragon.distribution.jy.service.work.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.response.AttachmentDetailData;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCaseData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCaseItemData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerTaskEditRequest;
import com.jd.bluedragon.core.jsf.work.WorkGridManagerTaskJsfManager;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManager;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCase;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCaseItem;
import com.jd.bluedragon.distribution.jy.enums.EditTypeEnum;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerBusinessService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerCaseItemService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerCaseService;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskStatusEnum;
import com.jd.jsf.gd.util.StringUtils;

/**
 * @ClassName: JyBizTaskWorkGridManagerServiceImpl
 * @Description: 巡检任务表--Service接口实现
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
@Service("jyWorkGridManagerBusinessService")
public class JyWorkGridManagerBusinessServiceImpl implements JyWorkGridManagerBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(JyWorkGridManagerBusinessServiceImpl.class);

	@Autowired
	@Qualifier("jyBizTaskWorkGridManagerService")
	private JyBizTaskWorkGridManagerService jyBizTaskWorkGridManagerService;
	
	@Autowired
	@Qualifier("jyWorkGridManagerCaseService")
	private JyWorkGridManagerCaseService jyWorkGridManagerCaseService;
	
	@Autowired
	@Qualifier("jyWorkGridManagerCaseItemService")
	private JyWorkGridManagerCaseItemService jyWorkGridManagerCaseItemService;	
	
	@Autowired
	@Qualifier("workGridManagerTaskJsfManager")
	private WorkGridManagerTaskJsfManager workGridManagerTaskJsfManager;
	
	@Autowired
	@Qualifier("jyAttachmentDetailService")
	private JyAttachmentDetailService jyAttachmentDetailService;	

	@Override
	public JdCResponse<Boolean> submitData(JyWorkGridManagerTaskEditRequest request) {
		JdCResponse<Boolean> result = new JdCResponse<Boolean>();
		result.toSucceed("保存成功！");
		if(request == null
				|| request.getTaskData() == null) {
			result.toFail("请求参数不能为空！");
			return result;
		}
		if(request.getUser() == null 
				|| StringUtils.isBlank(request.getUser().getUserErp())) {
			result.toFail("操作人erp不能为空！");
			return result;
		}
		request.getCurrentOperate();
		JyWorkGridManagerData taskData = request.getTaskData();
		String bizId = taskData.getBizId();
		String userErp = request.getUser().getUserErp();
		Date currentTime = new Date();
		JyWorkGridManagerData oldData = jyBizTaskWorkGridManagerService.queryTaskDataByBizId(bizId);
		if(oldData == null) {
			result.toFail("任务不存在|已删除！");
			return result;
		}
		if(WorkTaskStatusEnum.COMPLETE.getCode().equals(oldData.getStatus())) {
			result.toFail("任务已完成，不能重复提交！");
			return result;
		}
		if(WorkTaskStatusEnum.OVER_TIME.getCode().equals(oldData.getStatus())
				|| (oldData.getPreFinishTime() != null && currentTime.after(oldData.getPreFinishTime()))) {
			result.toFail("任务已超时，不能提交！");
			return result;
		}
		JyBizTaskWorkGridManager updateTaskData = new JyBizTaskWorkGridManager();
		updateTaskData.setStatus(WorkTaskStatusEnum.COMPLETE.getCode());
		updateTaskData.setHandlerPositionCode("");
		updateTaskData.setUpdateUser(userErp);
		updateTaskData.setUpdateTime(currentTime);
		updateTaskData.setProcessEndTime(currentTime);
		updateTaskData.setId(oldData.getId());
		List<JyAttachmentDetailEntity> addAttachmentList = new ArrayList<>();
		List<JyWorkGridManagerCase> updateCase = new ArrayList<>();
		List<JyWorkGridManagerCase> addCase = new ArrayList<>();
		List<JyWorkGridManagerCaseItem> addCaseItem = new ArrayList<>(); 
		int caseIndex = 0;
		//保存case信息
		for(JyWorkGridManagerCaseData caseData : taskData.getCaseList()) {
			caseIndex ++;
			caseData.setBizId(bizId);
			caseData.setOrderNum(caseIndex);
			if(EditTypeEnum.ADD.getCode().equals(caseData.getEditType())
					|| caseData.getId() == null) {
				addCase.add(toJyWorkGridManagerCaseForAdd(userErp,currentTime,taskData,caseData));
			} else if(EditTypeEnum.MODIFY.getCode().equals(caseData.getEditType())) {
				updateCase.add(toJyWorkGridManagerCaseForUpdate(userErp,currentTime,taskData,caseData));
			}
			if(!CollectionUtils.isEmpty(caseData.getAttachmentList())) {
				for(AttachmentDetailData attachmentData : caseData.getAttachmentList()) {
					addAttachmentList.add(toJyAttachmentDetailEntity(userErp,currentTime,taskData,caseData,attachmentData));
				}
			}
			if(!CollectionUtils.isEmpty(caseData.getItemList())) {
				for(JyWorkGridManagerCaseItemData caseItem: caseData.getItemList()) {
					if(caseItem.checkIsSelected()) {
						addCaseItem.add(toJyWorkGridManagerCaseItem(userErp,currentTime,taskData,caseData,caseItem));
					}
				}
			}
		}
		
		jyWorkGridManagerCaseService.batchInsert(addCase);
		jyWorkGridManagerCaseService.batchUpdate(updateCase);
		jyWorkGridManagerCaseItemService.batchInsert(addCaseItem);
		jyAttachmentDetailService.batchInsert(addAttachmentList);
		jyBizTaskWorkGridManagerService.finishTask(updateTaskData);
		return result;
	}

	private JyWorkGridManagerCase toJyWorkGridManagerCaseForUpdate(String userErp, Date currentTime,
			JyWorkGridManagerData taskData, JyWorkGridManagerCaseData caseData) {
		JyWorkGridManagerCase caseEntity = new JyWorkGridManagerCase();
		caseEntity.setId(caseData.getId());
		caseEntity.setBizId(caseData.getBizId());
		caseEntity.setCaseCode(caseData.getCaseCode());
		caseEntity.setCheckResult(caseData.getCheckResult());
		caseEntity.setCaseTitle(caseData.getCaseTitle());
		caseEntity.setCaseContent(caseData.getCaseContent());
		caseEntity.setUpdateUser(userErp);
		caseEntity.setUpdateTime(currentTime);
		return caseEntity;
	}

	private JyWorkGridManagerCase toJyWorkGridManagerCaseForAdd(String userErp, Date currentTime,
			JyWorkGridManagerData taskData, JyWorkGridManagerCaseData caseData) {
		JyWorkGridManagerCase caseEntity = new JyWorkGridManagerCase();
		caseEntity.setBizId(caseData.getBizId());
		caseEntity.setCaseCode(caseData.getCaseCode());
		caseEntity.setCheckResult(caseData.getCheckResult());
		caseEntity.setCaseTitle(caseData.getCaseTitle());
		caseEntity.setCaseContent(caseData.getCaseContent());
		caseEntity.setCreateUser(userErp);
		caseEntity.setCreateTime(currentTime);
		return caseEntity;
	}

	private JyWorkGridManagerCaseItem toJyWorkGridManagerCaseItem(String userErp, Date currentTime, JyWorkGridManagerData taskData,
			JyWorkGridManagerCaseData caseData, JyWorkGridManagerCaseItemData caseItem) {
		JyWorkGridManagerCaseItem caseItemEntity = new JyWorkGridManagerCaseItem();
        caseItemEntity.setBizId(caseData.getBizId());
        caseItemEntity.setCaseCode(caseData.getCaseCode());
        caseItemEntity.setCaseItemCode(caseItem.getCaseItemCode());
        caseItemEntity.setSelectFlag(caseItem.getSelectFlag());
        caseItemEntity.setCreateUser(userErp);
        caseItemEntity.setCreateTime(currentTime);
		return caseItemEntity;
	}

	private JyAttachmentDetailEntity toJyAttachmentDetailEntity(String userErp, Date currentTime, JyWorkGridManagerData taskData,JyWorkGridManagerCaseData caseData,AttachmentDetailData attachmentData) {
		JyAttachmentDetailEntity attachmentEntity = new JyAttachmentDetailEntity();
        attachmentEntity.setBizId(caseData.getBizId());
        attachmentEntity.setSiteCode(taskData.getSiteCode());
        attachmentEntity.setBizType(JyAttachmentBizTypeEnum.TASK_WORK_GRID_MANAGER.getCode());
        attachmentEntity.setBizSubType(caseData.getCaseCode());
        attachmentEntity.setAttachmentType(JyAttachmentTypeEnum.PICTURE.getCode());
        attachmentEntity.setCreateUserErp(userErp);
        attachmentEntity.setCreateTime(currentTime);
        attachmentEntity.setUpdateUserErp(userErp);
        attachmentEntity.setAttachmentUrl(attachmentData.getAttachmentUrl());
		return attachmentEntity;
	}

	@Override
	public JdCResponse<Boolean> saveData(JyWorkGridManagerTaskEditRequest request) {
		JdCResponse<Boolean> result = new JdCResponse<Boolean>();
		result.toSucceed("保存成功！");
		return result;
	}
}
