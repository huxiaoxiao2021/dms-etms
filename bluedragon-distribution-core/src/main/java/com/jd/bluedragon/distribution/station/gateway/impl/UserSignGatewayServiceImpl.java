package com.jd.bluedragon.distribution.station.gateway.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.PositionData;
import com.jd.bluedragon.common.dto.station.ScanUserData;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.position.service.PositionRecordService;
import com.jd.bluedragon.distribution.station.enums.JobTypeEnum;
import com.jd.bluedragon.distribution.station.gateway.UserSignGatewayService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
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
	public JdCResponse<UserSignRecordData> signInWithPosition(UserSignRequest signInRequest) {
		return userSignRecordService.signInWithPosition(signInRequest);
	}
	@Override
	public JdCResponse<UserSignRecordData> signOutWithPosition(UserSignRequest signOutRequest) {
		return userSignRecordService.signOutWithPosition(signOutRequest);
	}
	@Override
	public JdCResponse<UserSignRecordData> signAuto(UserSignRequest userSignRequest) {
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
	@Override
	public JdCResponse<ScanUserData> queryScanUserData(String scanUserCode) {
		JdCResponse<ScanUserData> result = new JdCResponse<ScanUserData>();
		result.toSucceed();
		
		if(!BusinessUtil.isScanUserCode(scanUserCode)) {
			result.toFail("请扫描正确的三定条码！");
			return result;
		}
		ScanUserData data = new ScanUserData();
		data.setJobCode(BusinessUtil.getJobCodeFromScanUserCode(scanUserCode));
		data.setUserCode(BusinessUtil.getUserCodeFromScanUserCode(scanUserCode));
		result.setData(data);
		return result;
	}
	@Override
	public JdCResponse<UserSignRecordData> queryLastUserSignRecordData(UserSignQueryRequest query) {
		return userSignRecordService.queryLastUserSignRecordData(query);
	}
	@Override
	public JdCResponse<ScanUserData> queryScanUserDataForLogin(String scanUserCode) {
		JdCResponse<ScanUserData> result = this.queryScanUserData(scanUserCode);
		if(!result.isSucceed()) {
			return result;
		}
		//校验是否能登录
		Integer jobCode = null;
		if(result.getData() != null) {
			jobCode = result.getData().getJobCode();
		}
		if(!JobTypeEnum.JOBTYPE1.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE2.getCode().equals(jobCode)) {
			result.toFail("请扫描正式工|派遣工三定条码");
			return result;
		}
		return result;
	}

}
