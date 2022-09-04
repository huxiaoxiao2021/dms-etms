package com.jd.bluedragon.distribution.station.gateway.impl;


import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jdl.basic.api.response.JDResponse;
import com.jdl.basic.common.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.PositionData;
import com.jd.bluedragon.common.dto.station.ScanUserData;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.distribution.position.service.PositionRecordService;
import com.jd.bluedragon.distribution.station.enums.JobTypeEnum;
import com.jd.bluedragon.distribution.station.gateway.UserSignGatewayService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

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

	@Autowired
	private PositionManager positionManager;

	@Autowired
	private UccPropertyConfiguration uccPropertyConfiguration;
	
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
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.querySignListWithPosition",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<PageDto<UserSignRecordData>> querySignListWithPosition(UserSignQueryRequest query) {
		return userSignRecordService.querySignListWithPosition(query);
	}
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.querySignListByOperateUser",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<PageDto<UserSignRecordData>> querySignListByOperateUser(UserSignQueryRequest query) {
		return userSignRecordService.querySignListByOperateUser(query);
	}	
	@Override
	public JdCResponse<PositionData> queryPositionData(String positionCode) {
		if(uccPropertyConfiguration.isJyBasicServerSwitch()){
			log.info("queryPositionData - 获取基础服务数据");
			JdCResponse<PositionData> response = new JdCResponse<>();
			response.toFail();
			try{
				log.info("UserSignGatewayServiceImpl.queryPositionData 入参-{}",positionCode);
				Result<com.jdl.basic.api.domain.position.PositionData> result = positionManager.queryPositionWithIsMatchAppFunc(positionCode);
				if(result == null){
					response.setMessage("查询岗位码失败！");
					return response;
				}
				if(result.getData() == null){
					response.setMessage(result.getMessage());
					return response;
				}
				PositionData positionData = new PositionData();
				BeanUtils.copyProperties(result.getData(),positionData);
				response.setData(positionData);
				if(result.isSuccess()){
					response.toSucceed();
					return response;
				}
			}catch (Exception e){
				log.error("queryPositionData查询岗位信息异常-{}",e.getMessage(),e);
				response.toError("查询岗位信息异常");
			}
			return response ;
		}else {
			log.info("queryPositionData - 原有逻辑");
			return positionRecordService.queryPositionWithIsMatchAppFunc(positionCode);

		}
	}

	@Override
	public JdCResponse<PositionData> queryPositionInfo(String positionCode) {
		if(uccPropertyConfiguration.isJyBasicServerSwitch()){
			log.info("queryPositionInfo - 获取基础服务数据");
			JdCResponse<PositionData> response = new JdCResponse<>();
			response.toFail();
			try{
				log.info("UserSignGatewayServiceImpl.queryPositionInfo 入参-{}",positionCode);
				Result<com.jdl.basic.api.domain.position.PositionData> result = positionManager.queryPositionInfo(positionCode);
				if(result == null){
					response.setMessage("查询岗位码失败！");
					return response;
				}
				if(result.getData() == null){
					response.setMessage(result.getMessage());
					return response;
				}
				PositionData positionData = new PositionData();
				BeanUtils.copyProperties(result.getData(),positionData);
				response.setData(positionData);
				if(result.isSuccess()){
					response.toSucceed();
					return response;
				}
			}catch (Exception e){
				log.error("queryPositionData查询岗位信息异常-{}",e.getMessage(),e);
				response.toError("查询岗位信息异常");
			}
			return response ;
		}else{
			log.info("queryPositionInfo - 原有逻辑");
			return positionRecordService.queryPositionInfo(positionCode);
		}

	}

	@Override
	public JdCResponse<ScanUserData> queryScanUserData(String scanUserCode) {
		JdCResponse<ScanUserData> result = new JdCResponse<ScanUserData>();
		result.toSucceed();
		
		if(!BusinessUtil.isScanUserCode(scanUserCode)) {
			result.toFail("请扫描正确的人员码！");
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
				&& !JobTypeEnum.JOBTYPE2.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE6.getCode().equals(jobCode)) {
			result.toFail("请扫描[正式工、派遣工、支援]人员码！");
			return result;
		}
		return result;
	}
	@Override
	public JdCResponse<UserSignRecordData> signInWithGroup(UserSignRequest signInRequest) {
		return userSignRecordService.signInWithGroup(signInRequest);
	}
	@Override
	public JdCResponse<UserSignRecordData> signOutWithGroup(UserSignRequest signOutRequest) {
		return userSignRecordService.signOutWithGroup(signOutRequest);
	}
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.signAutoWithGroup",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<UserSignRecordData> signAutoWithGroup(UserSignRequest userSignRequest) {
		return userSignRecordService.signAutoWithGroup(userSignRequest);
	}

}
