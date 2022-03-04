package com.jd.bluedragon.distribution.station.gateway.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.PositionData;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.position.service.PositionRecordService;
import com.jd.bluedragon.distribution.station.gateway.UserSignGatewayService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 人员签到表--JsfService接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Slf4j
@Service("userSignGatewayService")
public class UserSignGatewayServiceImpl implements UserSignGatewayService {

	@Autowired
	@Qualifier("userSignRecordService")
	private UserSignRecordService userSignRecordService;
	
	@Autowired
	private PositionRecordService positionRecordService;
	
	@Override
	public JdCResponse<Boolean> signInWithPosition(UserSignRequest signInRequest) {
		return userSignRecordService.signInWithPosition(signInRequest);
	}
	@Override
	public JdCResponse<Boolean> signOutWithPosition(UserSignRequest signOutRequest) {
		return userSignRecordService.signOutWithPosition(signOutRequest);
	}
	@Override
	public JdCResponse<Boolean> signAuto(UserSignRequest userSignRequest) {
		return userSignRecordService.signAuto(userSignRequest);
	}
	@Override
	public JdCResponse<PageDto<UserSignRecordData>> querySignListWithPosition(UserSignQueryRequest query) {
		return userSignRecordService.querySignListWithPosition(query);
	}
	@Override
	public JdCResponse<PositionData> queryPositionData(String positionCode) {
		return positionRecordService.queryPositionData(positionCode);
	}

}
