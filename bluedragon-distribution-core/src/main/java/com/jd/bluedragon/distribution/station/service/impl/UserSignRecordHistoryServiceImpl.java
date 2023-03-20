package com.jd.bluedragon.distribution.station.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordFlowService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordHistoryService;
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
@Service("userSignRecordHistoryService")
public class UserSignRecordHistoryServiceImpl implements UserSignRecordHistoryService {

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
	
	@Override
	public Integer querySignCount(UserSignRecordFlowQuery query) {
		UserSignRecordQuery historyQuery = new UserSignRecordQuery();
		BeanUtils.copyProperties(query, historyQuery);
		return userSignRecordService.queryCountForFlow(historyQuery);
	}
	@Override
	public List<UserSignRecordFlow> querySignList(UserSignRecordFlowQuery query) {
		List<UserSignRecordFlow> flowList = new ArrayList<>();
		UserSignRecordQuery historyQuery = new UserSignRecordQuery();
		BeanUtils.copyProperties(query, historyQuery);
		List<UserSignRecord> historyList = userSignRecordService.queryDataListForFlow(historyQuery);
		if(!CollectionUtils.isEmpty(historyList)) {
			for(UserSignRecord signData : historyList) {
				flowList.add(toUserSignRecordFlow(signData));
			}
		}
		return flowList;
	}
	@Override
	public UserSignRecordFlow queryById(Long recordId) {
		Result<UserSignRecord> queryResult  = userSignRecordService.queryById(recordId);
		if(queryResult != null) {
			return toUserSignRecordFlow(queryResult.getData());
		}
		return null;
	}
	private UserSignRecordFlow toUserSignRecordFlow(UserSignRecord signData) {
		if(signData == null) {
			return null;
		}
		UserSignRecordFlow flowData = new UserSignRecordFlow();
		BeanUtils.copyProperties(signData, flowData);
		flowData.setId(null);
		flowData.setRefRecordId(signData.getId());
		return flowData;
	}
	
}
