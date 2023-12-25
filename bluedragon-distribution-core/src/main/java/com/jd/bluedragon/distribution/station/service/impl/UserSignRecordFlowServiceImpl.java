package com.jd.bluedragon.distribution.station.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.group.GroupMemberData;
import com.jd.bluedragon.common.dto.group.GroupMemberRequest;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationAttendPlanManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberTypeEnum;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupService;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordFlowDao;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.enums.SignBizSourceEnum;
import com.jd.bluedragon.distribution.station.enums.SignFlowStatusEnum;
import com.jd.bluedragon.distribution.station.enums.SignFlowTypeEnum;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordFlowService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordHistoryService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.distribution.station.service.WorkStationAttendPlanService;
import com.jd.bluedragon.distribution.station.service.WorkStationGridService;
import com.jd.bluedragon.distribution.station.service.WorkStationService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.lsb.flow.domain.ApprovalResult;
import com.jd.lsb.flow.domain.HistoryApprove;
import com.jdl.basic.api.domain.position.PositionData;

import lombok.extern.slf4j.Slf4j;

import static com.jd.bluedragon.Constants.*;

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
	
	@Autowired
	@Qualifier("userSignRecordHistoryService")
	private UserSignRecordHistoryService userSignRecordHistoryService;
	
	
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
	
	@Autowired
	private PositionManager positionManager;

	/**
	 * 默认计提日-日期
	 */
	private static final int ACCRUAL_DAY = 21;
	/**
	 * 默认计提时间-小时
	 */
	private static final int ACCRUAL_HOUR = 0;

	/**
	 * 默认计提修改最大日期
	 */
	private static final int LAST_MODIFY_ACCRUAL_DAY = 21;
	/**
	 * 默认计提修改最大小时
	 */
	private static final int LAST_MODIFY_ACCRUAL_HOUR = 12;

	@Autowired
	private SysConfigService sysConfigService;

	/**
	 * 签到日期-不能小于上个计提日期
	 * @param signInTime
	 * @param signInTimeNew
	 * @return
	 */
	public boolean checkSignInTime(Date signInTime,Date signInTimeNew) {
		Date currentTime = new Date();

		// 根据当前时间获取计提周期时间
		int accrualDay = getAccrualDay();
		int accrualHour = getAccrualHour();
		Date lastAccrualDate = DateHelper.getLastAccrualDate(accrualDay,accrualHour,0);

		// 根据当前时间获取修改计提周期的最大时间
		int lastModifyAccrualDay = getLastModifyAccrualDay();
		int lastModifyAccrualHour = getLastModifyAccrualHour();
		Date lastModifyAccrualDate = DateHelper.getLastAccrualDate(lastModifyAccrualDay,lastModifyAccrualHour,0);
		if (currentTime.before(lastModifyAccrualDate) && lastModifyAccrualDate.before(lastAccrualDate)) {
			// 当前时间已经过了计提周期，但是还没有过计提修改的周期，根据最大修改时间来校验
			if(signInTime != null && !signInTime.after(lastModifyAccrualDate)) {
				return false;
			}
			if(signInTimeNew != null && !signInTimeNew.after(lastModifyAccrualDate)) {
				return false;
			}
			return true;
		}

		if(signInTime != null && !signInTime.after(lastAccrualDate)) {
			return false;
		}
		if(signInTimeNew != null && !signInTimeNew.after(lastAccrualDate)) {
			return false;
		}
		return true;
	}

	private int getLastModifyAccrualHour() {
		SysConfig hourConf = sysConfigService.findConfigContentByConfigName(USER_SIGN_RECORD_FLOW_LAST_MODIFY_ACCRUAL_HOUR);
		if (hourConf != null
				&& StringUtils.isNotEmpty(hourConf.getConfigContent())
				&& NumberUtils.isNumber(hourConf.getConfigContent())) {
			return Integer.parseInt(hourConf.getConfigContent());
		}
		return LAST_MODIFY_ACCRUAL_HOUR;
	}

	private int getLastModifyAccrualDay() {
		SysConfig dayConf = sysConfigService.findConfigContentByConfigName(USER_SIGN_RECORD_FLOW_LAST_MODIFY_ACCRUAL_DAY);
		if (dayConf != null
				&& StringUtils.isNotEmpty(dayConf.getConfigContent())
				&& NumberUtils.isNumber(dayConf.getConfigContent())) {
			return Integer.parseInt(dayConf.getConfigContent());
		}
		return LAST_MODIFY_ACCRUAL_DAY;
	}

	private int getAccrualHour() {
		SysConfig hourConf = sysConfigService.findConfigContentByConfigName(USER_SIGN_RECORD_FLOW_ACCRUAL_HOUR);
		if (hourConf != null
				&& StringUtils.isNotEmpty(hourConf.getConfigContent())
				&& NumberUtils.isNumber(hourConf.getConfigContent())) {
			 return Integer.parseInt(hourConf.getConfigContent());
		}
		return ACCRUAL_HOUR;
	}

	private int getAccrualDay() {
		SysConfig dayConf = sysConfigService.findConfigContentByConfigName(USER_SIGN_RECORD_FLOW_ACCRUAL_DAY);
		if (dayConf != null
				&& StringUtils.isNotEmpty(dayConf.getConfigContent())
				&& NumberUtils.isNumber(dayConf.getConfigContent())) {
			return Integer.parseInt(dayConf.getConfigContent());
		}
		return ACCRUAL_DAY;
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
		if(!SignFlowStatusEnum.PENDING_APPROVAL.getCode().equals(flowData.getFlowStatus())) {
			log.warn("流程单号{}流程状态为{},无需处理！",processInstanceNo,SignFlowStatusEnum.getNameByCode(flowData.getFlowStatus()));
			return;
		}
		if(log.isDebugEnabled()) {
			log.debug("开始处理流程单号-pass:{},流程数据{}",processInstanceNo,JsonHelper.toJson(flowData));
		}
		boolean checkSignInTime = this.checkSignInTime(flowData.getSignInTime(), flowData.getSignInTimeNew());
		boolean checkOldData = true;
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
		//校验原始数据状态
		if(SignFlowTypeEnum.DELETE.getCode().equals(flowType)
				|| SignFlowTypeEnum.MODIFY.getCode().equals(flowType)) {
			UserSignRecordFlow oldData = userSignRecordHistoryService.queryById(flowData.getRefRecordId());
			if(oldData == null
					|| Constants.YN_NO.equals(oldData.getYn())) {
				updateData.setFlowStatus(SignFlowStatusEnum.HAS_DELETED_CANCEL.getCode());
				checkOldData = false;
			}
		}
		updateData.setFlowUpdateUser(flowUser);
		updateData.setFlowUpdateTime(new Date());
		updateData.setFlowRemark(comment);
		

		int updateCount = userSignRecordFlowDao.updateFlowStatusById(updateData);
		if(log.isDebugEnabled()) {
			log.debug("更新流程状态：流程单号{},更新数据{},更新结果{}",processInstanceNo,JsonHelper.toJson(updateData),updateCount);
		}
		int accrualDay = getAccrualDay();
		int accrualHour = getAccrualHour();
		if(!checkSignInTime) {
			log.warn("审批超时，审批通过时间在当前计提周期({}号{}点)结束之前：流程单号{},更新数据{},更新结果{}",accrualDay,accrualHour,processInstanceNo,JsonHelper.toJson(updateData),updateCount);
			return;
		}
		if(!checkOldData) {
			log.warn("审批无效，原签到数据已作废：流程单号{},更新数据{},更新结果{}",processInstanceNo,JsonHelper.toJson(updateData),updateCount);
			return;
		}
		if(flowData.getPositionCode() == null) {
			//查询组员信息
			com.jdl.basic.common.utils.Result<PositionData>  positionResult= this.positionManager.queryPositionByGridKey(flowData.getRefGridKey());
			if(positionResult != null
					&& positionResult.getData() != null) {
				flowData.setPositionCode(positionResult.getData().getPositionCode());
			}
		}
		UserSignRecord signData = toUserSignRecord(flowData);
		if(SignFlowTypeEnum.ADD.getCode().equals(flowType)) {
			signData.setId(null);
			signData.setCreateTime(new Date());
			signData.setTs(null);
			signData.setBizSource(SignBizSourceEnum.PC.getCode());
			userSignRecordService.insert(signData);
			//新增一条组员数据
			if(log.isDebugEnabled()) {
				log.debug("新增签到数据：流程单号{},签到数据{}",processInstanceNo,JsonHelper.toJson(signData));
			}
			
			addGroupMember(signData,flowData);
		}else if(SignFlowTypeEnum.MODIFY.getCode().equals(flowType)) {
			deleteSignData(signData,flowData,flowUser);
			//插入新记录
			signData.setId(null);
			signData.setSignInTime(flowData.getSignInTimeNew());
			signData.setSignOutTime(flowData.getSignOutTimeNew());
			signData.setYn(Constants.YN_YES);
			signData.setUpdateUser(flowData.getFlowCreateUser());
			signData.setUpdateUserName(flowData.getFlowCreateUser());
			signData.setUpdateTime(new Date());			
			signData.setBizSource(SignBizSourceEnum.PC.getCode());
			userSignRecordService.insert(signData);
			if(log.isDebugEnabled()) {
				log.debug("新增签到数据：流程单号{},签到数据{}",processInstanceNo,JsonHelper.toJson(signData));
			}
			addGroupMember(signData,flowData);
		}else if(SignFlowTypeEnum.DELETE.getCode().equals(flowType)) {
			deleteSignData(signData,flowData,flowUser);
		}
	}
	private void addGroupMember(UserSignRecord signData, UserSignRecordFlow flowData) {
		GroupMemberRequest addMemberRequest = new GroupMemberRequest();
		addMemberRequest.setMemberType(JyGroupMemberTypeEnum.PERSON.getCode());
		addMemberRequest.setSignInTime(signData.getSignInTime());
		addMemberRequest.setSignOutTime(signData.getSignOutTime());
		addMemberRequest.setSignRecordId(signData.getId());
		addMemberRequest.setPositionCode(flowData.getPositionCode());
		addMemberRequest.setJobCode(signData.getJobCode());
		addMemberRequest.setUserCode(signData.getUserCode());
		addMemberRequest.setUserName(signData.getUserName());
		addMemberRequest.setOrgCode(signData.getOrgCode());
		addMemberRequest.setSiteCode(signData.getSiteCode());
		addMemberRequest.setOperateUserCode(flowData.getFlowCreateUser());
		addMemberRequest.setOperateUserName(flowData.getFlowCreateUser());
		jyGroupMemberService.addMemberForFlow(addMemberRequest);
	}
	private void deleteSignData(UserSignRecord signData,UserSignRecordFlow flowData, String flowUser) {
		UserSignRecord oldData = userSignRecordService.queryByIdForFlow(flowData.getRefRecordId());
		
		if(oldData != null) {
			UserSignRecord deleteData = new UserSignRecord();
			deleteData.setId(flowData.getRefRecordId());
			deleteData.setUpdateUser(flowData.getFlowCreateUser());
			deleteData.setUpdateUserName(flowData.getFlowCreateUser());			
			deleteData.setUpdateTime(new Date());		
			deleteData.setBizSource(SignBizSourceEnum.PC.getCode());
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
			signData.setBizSource(SignBizSourceEnum.PC.getCode());
			userSignRecordService.insert(signData);
			if(log.isDebugEnabled()) {
				log.debug("作废签到数据-insert：流程单号{},签到数据{}",flowData.getRefFlowBizCode(),JsonHelper.toJson(signData));
			}			
		}
		deleteGroupMember(signData,flowData);
	}
	private void deleteGroupMember(UserSignRecord signData, UserSignRecordFlow flowData) {
		GroupMemberRequest removeMemberRequest = new GroupMemberRequest();
		removeMemberRequest.setMemberType(JyGroupMemberTypeEnum.PERSON.getCode());
		removeMemberRequest.setSignRecordId(flowData.getRefRecordId());
		removeMemberRequest.setOperateUserCode(flowData.getFlowCreateUser());
		removeMemberRequest.setOperateUserName(flowData.getFlowCreateUser());
		jyGroupMemberService.deleteMemberForFlow(removeMemberRequest);
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
