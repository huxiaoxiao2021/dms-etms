package com.jd.bluedragon.distribution.station.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.WorkStationAttendPlanManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupService;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordFlowDao;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.enums.SignBIzSourceEnum;
import com.jd.bluedragon.distribution.station.enums.SignFlowStatusEnum;
import com.jd.bluedragon.distribution.station.enums.SignFlowTypeEnum;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordFlowService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.distribution.station.service.WorkStationAttendPlanService;
import com.jd.bluedragon.distribution.station.service.WorkStationGridService;
import com.jd.bluedragon.distribution.station.service.WorkStationService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.lsb.flow.domain.ApprovalResult;
import com.jd.lsb.flow.domain.HistoryApprove;

import lombok.extern.slf4j.Slf4j;

/**
 * 人员签到流程业务--Service接口实现
 * 
 * @author wuyoude
 * @date 2023年03月10日 14:30:43
 *
 */
@Slf4j
@Service("userSignRecordFlowService")
public class UserSignRecordFlowServiceImpl implements UserSignRecordFlowService {

	@Autowired
	@Qualifier("userSignRecordFlowDao")
	private UserSignRecordFlowDao userSignRecordFlowDao;
	
	@Autowired
	@Qualifier("userSignRecordService")
	private UserSignRecordService userSignRecordService;
	
	@Value("${beans.userSignRecordService.signDateRangeMaxDays:2}")
	private int signDateRangeMaxDays;
	
	@Value("${beans.userSignRecordService.queryByPositionRangeDays:2}")
	private int queryByPositionRangeDays;

	@Autowired
	@Qualifier("workStationService")
	WorkStationService workStationService;
	
	@Autowired
	@Qualifier("workStationGridService")
	WorkStationGridService workStationGridService;
	
	@Autowired
	@Qualifier("jyGroupMemberService")
	private JyGroupMemberService jyGroupMemberService;
	
	@Autowired
	@Qualifier("jyGroupService")
	private JyGroupService jyGroupService;
	
	/**
	 * 计提日-日期
	 */
	@Value("${beans.userSignRecordFlowService.accrualDay:21}")
	private int accrualDay;
	/**
	 * 计提时间-小时
	 */
	@Value("${beans.userSignRecordFlowService.accrualHour:7}")
	private int accrualHour;
	
	/**
	 * 签到日期-不能小于上个计提日期
	 * @param signInTime
	 * @param signInTimeNew
	 * @return
	 */
	public boolean checkSignInTime(Date signInTime,Date signInTimeNew) {
        Date lastAccrualDate = DateHelper.getLastAccrualDate(accrualDay,accrualHour,0);
		if(signInTime != null && !signInTime.after(lastAccrualDate)) {
			return false;
		}
		if(signInTimeNew != null && !signInTimeNew.after(lastAccrualDate)) {
			return false;
		}
		return true;
	}
	@Override
	public boolean addData(UserSignRecordFlow userSignRecordFlow) {
		return userSignRecordFlowDao.insert(userSignRecordFlow) == 1;
	}
	@Override
	public Integer queryFlowCount(UserSignRecordFlowQuery query) {
		return userSignRecordFlowDao.queryDataCount(query);
	}
	@Override
	public List<UserSignRecordFlow> queryFlowList(UserSignRecordFlowQuery query) {
		return userSignRecordFlowDao.queryDataList(query);
	}
	public void dealFlowPassResult(String processInstanceNo, Integer state, String flowUser, String comment) {
		UserSignRecordFlow flowData = userSignRecordFlowDao.queryByFlowBizCode(processInstanceNo);
		if(flowData == null) {
			log.warn("根据流程单号{}查询签到流程数据失败！",processInstanceNo);
			return;
		}
		if(log.isDebugEnabled()) {
			log.debug("开始处理流程单号-pass:{},流程数据{}",processInstanceNo,JsonHelper.toJson(flowData));
		}
		boolean checkSignInTime = this.checkSignInTime(flowData.getSignInTime(), flowData.getSignInTimeNew());
		Integer flowType = flowData.getFlowType();
		UserSignRecordFlow updateData = new UserSignRecordFlow();
		updateData.setId(flowData.getId());
		if(SignFlowTypeEnum.ADD.getCode().equals(flowType)) {
			updateData.setFlowStatus(SignFlowStatusEnum.ADD_COMPLETE.getCode());
		}else if(SignFlowTypeEnum.MODIFY.getCode().equals(flowType)) {
			updateData.setFlowStatus(SignFlowStatusEnum.MODIFY_COMPLETE.getCode());
		}else if(SignFlowTypeEnum.DELETE.getCode().equals(flowType)) {
			updateData.setFlowStatus(SignFlowStatusEnum.DELETE_COMPLETE.getCode());
		}
		//校验签到时间是否上个计提周期
		if(!checkSignInTime) {
			updateData.setFlowStatus(SignFlowStatusEnum.TIMEOUT_CANCEL.getCode());
		}
		updateData.setFlowUpdateUser(flowUser);
		updateData.setFlowUpdateTime(new Date());
		updateData.setFlowRemark(comment);
		int updateCount = userSignRecordFlowDao.updateFlowStatusById(updateData);
		if(log.isDebugEnabled()) {
			log.debug("更新流程状态：流程单号{},更新数据{},更新结果{}",processInstanceNo,JsonHelper.toJson(updateData),updateCount);
		}
		if(!checkSignInTime) {
			log.warn("审批超时，审批通过时间在当前计提周期({}号{}点)结束之前：流程单号{},更新数据{},更新结果{}",accrualDay,accrualHour,processInstanceNo,JsonHelper.toJson(updateData),updateCount);
			return;
		}		
		UserSignRecord signData = toUserSignRecord(flowData);
		if(SignFlowTypeEnum.ADD.getCode().equals(flowType)) {
			signData.setId(null);
			signData.setCreateTime(new Date());
			signData.setTs(null);
			signData.setBizSource(SignBIzSourceEnum.PC.getCode());
			userSignRecordService.insert(signData);
			if(log.isDebugEnabled()) {
				log.debug("新增签到数据：流程单号{},签到数据{}",processInstanceNo,JsonHelper.toJson(signData));
			}
		}else if(SignFlowTypeEnum.MODIFY.getCode().equals(flowType)) {
			deleteSignData(signData,flowData,flowUser);
			//插入新记录
			signData.setId(null);
			signData.setSignInTime(flowData.getSignInTimeNew());
			signData.setSignOutTime(flowData.getSignOutTimeNew());
			signData.setYn(Constants.YN_YES);
			signData.setBizSource(SignBIzSourceEnum.PC.getCode());
			userSignRecordService.insert(signData);
			if(log.isDebugEnabled()) {
				log.debug("新增签到数据：流程单号{},签到数据{}",processInstanceNo,JsonHelper.toJson(signData));
			}
		}else if(SignFlowTypeEnum.DELETE.getCode().equals(flowType)) {
			deleteSignData(signData,flowData,flowUser);
		}
	}
	private void deleteSignData(UserSignRecord signData,UserSignRecordFlow flowData, String flowUser) {
		Result<UserSignRecord> oldDataResult = userSignRecordService.queryById(flowData.getRefRecordId());
		UserSignRecord oldData = null;
		if(oldDataResult != null) {
			oldData = oldDataResult.getData();
		}
		if(oldData != null) {
			UserSignRecord deleteData = new UserSignRecord();
			deleteData.setId(flowData.getRefRecordId());
			signData.setUpdateUser(flowData.getFlowCreateUser());
			signData.setUpdateUserName(flowData.getFlowCreateUser());			
			signData.setUpdateTime(new Date());			
			userSignRecordService.deleteById(deleteData);
			if(log.isDebugEnabled()) {
				log.debug("作废签到数据-deleteById：流程单号{},签到数据{}",flowData.getRefFlowBizCode(),JsonHelper.toJson(oldData));
			}			
		}else {
			signData.setId(flowData.getRefRecordId());
			signData.setYn(Constants.YN_NO);
			signData.setUpdateUser(flowData.getFlowCreateUser());
			signData.setUpdateUserName(flowData.getFlowCreateUser());
			signData.setUpdateTime(new Date());
			userSignRecordService.insert(signData);
			if(log.isDebugEnabled()) {
				log.debug("作废签到数据-insert：流程单号{},签到数据{}",flowData.getRefFlowBizCode(),JsonHelper.toJson(signData));
			}			
		}
	}
	/**
	 * 对象转换
	 * @param flowData
	 * @return
	 */
	private UserSignRecord toUserSignRecord(UserSignRecordFlow flowData) {
		UserSignRecord signData = new UserSignRecord();
		BeanUtils.copyProperties(flowData, signData);
		return signData;
	}
	public void dealFlowUnPassResult(String processInstanceNo, Integer state, String flowUser, String comment) {
		UserSignRecordFlow flowData = userSignRecordFlowDao.queryByFlowBizCode(processInstanceNo);
		if(flowData == null) {
			log.warn("根据流程单号{}查询签到流程数据失败！",processInstanceNo);
			return;
		}
		if(log.isDebugEnabled()) {
			log.debug("开始处理流程单号-unpass:{},流程数据{}",processInstanceNo,JsonHelper.toJson(flowData));
		}		
		UserSignRecordFlow updateData = new UserSignRecordFlow();
		updateData.setId(flowData.getId());
		updateData.setFlowStatus(state);
		updateData.setUpdateUser(flowUser);
		updateData.setFlowUpdateTime(new Date());
		updateData.setFlowRemark(comment);
		int updateCount = userSignRecordFlowDao.updateFlowStatusById(updateData);
		if(log.isDebugEnabled()) {
			log.debug("更新流程状态：流程单号{},更新数据{},更新结果{}",processInstanceNo,JsonHelper.toJson(updateData),updateCount);
		}		
	}
	@Override
	public void dealFlowResult(HistoryApprove historyApprove) {
        if(!Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue())
        		&& !Objects.equals(historyApprove.getState(), ApprovalResult.COMPLETE_AGREE.getValue())){
            log.warn("申请人【{}】提交的签到修改审批流程未通过！审批结果【{}】、审批意见【{}】、审批工单号【{}】、审批节点编码【{}】",
                    historyApprove.getApplicant(), historyApprove.getState(), historyApprove.getComment(), historyApprove.getProcessInstanceNo(), historyApprove.getNodeName());
            dealFlowUnPassResult(historyApprove.getProcessInstanceNo(),historyApprove.getState(),"",historyApprove.getComment());
        }else {
        	dealFlowPassResult(historyApprove.getProcessInstanceNo(),historyApprove.getState(),historyApprove.getApprover(),historyApprove.getComment());
        }
	}
	@Override
	public boolean checkUnCompletedFlow(UserSignRecordFlowQuery checkFlowQuery) {
		return userSignRecordFlowDao.queryCountForCheckUnCompletedFlow(checkFlowQuery) == 0;
	}
}
