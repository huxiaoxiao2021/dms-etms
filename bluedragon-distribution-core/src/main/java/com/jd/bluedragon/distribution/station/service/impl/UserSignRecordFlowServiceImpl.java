package com.jd.bluedragon.distribution.station.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.WorkStationAttendPlanManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupService;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordFlowDao;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.enums.SignBIzSourceEnum;
import com.jd.bluedragon.distribution.station.enums.SignFlowTypeEnum;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordFlowService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.distribution.station.service.WorkStationAttendPlanService;
import com.jd.bluedragon.distribution.station.service.WorkStationGridService;
import com.jd.bluedragon.distribution.station.service.WorkStationService;
import com.jd.bluedragon.dms.utils.DmsConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 人员签到流程业务--Service接口实现
 * 
 * @author wuyoude
 * @date 2023年03月10日 14:30:43
 *
 */
@Slf4j
@Service("userSignRecordService")
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
	private WorkStationAttendPlanManager workStationAttendPlanManager;

	@Autowired
	@Qualifier("workStationAttendPlanService")
	WorkStationAttendPlanService workStationAttendPlanService;
	
	@Autowired
	@Qualifier("jyGroupMemberService")
	private JyGroupMemberService jyGroupMemberService;
	
	@Autowired
	@Qualifier("jyGroupService")
	private JyGroupService jyGroupService;
	
	/**
	 * 签到作废-小时数限制
	 */
	@Value("${beans.userSignRecordService.deleteCheckHours:4}")
	private double deleteCheckHours;
	
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
	@Override
	public void dealFlowPassResult(String processInstanceNo, Integer state, String flowUser, String comment) {
		UserSignRecordFlow flowData = userSignRecordFlowDao.queryByFlowBizCode(processInstanceNo);
		if(flowData == null) {
			log.warn("根据流程单号{}查询签到流程数据失败！",processInstanceNo);
			return;
		}
		UserSignRecordFlow updateData = new UserSignRecordFlow();
		updateData.setId(flowData.getId());
		updateData.setFlowStatus(state);
		updateData.setFlowUpdateUser(flowUser);
		updateData.setFlowUpdateTime(new Date());
		updateData.setFlowRemark(comment);
		userSignRecordFlowDao.updateFlowStatusById(updateData);
		
		UserSignRecord signData = toUserSignRecord(flowData);
		Integer flowType = flowData.getFlowType();
		if(SignFlowTypeEnum.ADD.getCode().equals(flowType)) {
			signData.setId(null);
			signData.setSignInTime(flowData.getSignInTimeNew());
			signData.setSignOutTime(flowData.getSignOutTimeNew());
			signData.setBizSource(SignBIzSourceEnum.PC.getCode());
			userSignRecordService.insert(signData);
		}else if(SignFlowTypeEnum.MODIFY.getCode().equals(flowType)) {
			deleteSignData(signData,flowData,flowUser);
			//插入新记录
			signData.setId(null);
			signData.setSignInTime(flowData.getSignInTimeNew());
			signData.setSignOutTime(flowData.getSignOutTimeNew());
			signData.setYn(Constants.YN_YES);
			signData.setBizSource(SignBIzSourceEnum.PC.getCode());
			userSignRecordService.insert(signData);
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
		}else {
			signData.setId(flowData.getRefRecordId());
			signData.setYn(Constants.YN_NO);
			signData.setUpdateUser(flowData.getFlowCreateUser());
			signData.setUpdateUserName(flowData.getFlowCreateUser());
			signData.setUpdateTime(new Date());
			userSignRecordService.insert(signData);
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
	@Override
	public void dealFlowUnPassResult(String processInstanceNo, Integer state, String flowUser, String comment) {
		UserSignRecordFlow flowData = userSignRecordFlowDao.queryByFlowBizCode(processInstanceNo);
		if(flowData == null) {
			log.warn("根据流程单号{}查询签到流程数据失败！",processInstanceNo);
			return;
		}
		UserSignRecordFlow updateData = new UserSignRecordFlow();
		updateData.setId(flowData.getId());
		updateData.setFlowStatus(state);
		updateData.setUpdateUser(flowUser);
		updateData.setFlowUpdateTime(new Date());
		updateData.setFlowRemark(comment);
		userSignRecordFlowDao.updateFlowStatusById(updateData);
	}
}
