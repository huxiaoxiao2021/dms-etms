package com.jd.bluedragon.distribution.station.service;

import java.util.List;

import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;

/**
 * 人员签到表--Service接口
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public interface UserSignRecordHistoryService {
	
	Integer querySignCount(UserSignRecordFlowQuery query);

	List<UserSignRecordFlow> querySignList(UserSignRecordFlowQuery query);
}
