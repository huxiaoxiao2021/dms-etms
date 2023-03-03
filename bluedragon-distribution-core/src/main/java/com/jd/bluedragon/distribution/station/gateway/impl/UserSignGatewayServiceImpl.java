package com.jd.bluedragon.distribution.station.gateway.impl;


import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jdl.basic.common.utils.Result;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.PositionData;
import com.jd.bluedragon.common.dto.station.ScanForLoginRequest;
import com.jd.bluedragon.common.dto.station.ScanUserData;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.distribution.station.enums.JobTypeEnum;
import com.jd.bluedragon.distribution.station.gateway.UserSignGatewayService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jsf.gd.util.StringUtils;
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
	private PositionManager positionManager;
	
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.signInWithPosition",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})	
	@Override
	public JdCResponse<UserSignRecordData> signInWithPosition(UserSignRequest signInRequest) {
		return userSignRecordService.signInWithPosition(signInRequest);
	}
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.signOutWithPosition",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})	
	@Override
	public JdCResponse<UserSignRecordData> signOutWithPosition(UserSignRequest signOutRequest) {
		return userSignRecordService.signOutWithPosition(signOutRequest);
	}
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.signAuto",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})	
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
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.queryPositionData",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})	
	@Override
	public JdCResponse<PositionData> queryPositionData(String positionCode) {
		log.info("queryPositionData - 获取基础服务数据");
		JdCResponse<PositionData> response = new JdCResponse<>();
		try{
			log.info("UserSignGatewayServiceImpl.queryPositionData 入参-{}",positionCode);
			Result<com.jdl.basic.api.domain.position.PositionData> result = positionManager.queryPositionWithIsMatchAppFunc(positionCode);
			if(result == null){
				response.setMessage("查询岗位码失败！");
				return response;
			}
			if(result.isSuccess()){
				PositionData positionData = new PositionData();
				BeanUtils.copyProperties(result.getData(),positionData);
				response.setData(positionData);
				response.toSucceed(result.getMessage());
				return response;
			}
			response.toFail(result.getMessage());
		}catch (Exception e){
			log.error("queryPositionData查询岗位信息异常-{}",e.getMessage(),e);
			response.toError("查询岗位信息异常");
		}
		return response ;

	}
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.queryPositionInfo",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<PositionData> queryPositionInfo(String positionCode) {
		log.info("queryPositionInfo - 获取基础服务数据");
		JdCResponse<PositionData> response = new JdCResponse<>();
		try{
			log.info("UserSignGatewayServiceImpl.queryPositionInfo 入参-{}",positionCode);
			Result<com.jdl.basic.api.domain.position.PositionData> result = positionManager.queryPositionInfo(positionCode);
			if(result == null){
				response.setMessage("查询岗位码失败！");
				return response;
			}
			if(result.isSuccess()){
				PositionData positionData = new PositionData();
				BeanUtils.copyProperties(result.getData(),positionData);
				response.setData(positionData);
				response.toSucceed(result.getMessage());
				return response;
			}
			response.toFail(result.getMessage());
		}catch (Exception e){
			log.error("queryPositionData查询岗位信息异常-{}",e.getMessage(),e);
			response.toError("查询岗位信息异常");
		}
		return response ;
	}
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.queryScanUserData",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.queryLastUserSignRecordData",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})	
	@Override
	public JdCResponse<UserSignRecordData> queryLastUserSignRecordData(UserSignQueryRequest query) {
		return userSignRecordService.queryLastUserSignRecordData(query);
	}
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.queryScanUserDataForLogin",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.deleteUserSignRecord",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<UserSignRecordData> deleteUserSignRecord(UserSignRequest userSignRequest) {
		return userSignRecordService.deleteUserSignRecord(userSignRequest);
	}
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.checkBeforeSignIn",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<UserSignRecordData> checkBeforeSignIn(UserSignRequest userSignRequest) {
		JdCResponse<UserSignRecordData> result = new JdCResponse<>();
		result.toSucceed();
		if(userSignRequest == null) {
			result.toFail("请求参数不能为空！");
			return result;
		}
		String positionCode = userSignRequest.getPositionCode();
		String scanUserCode = userSignRequest.getScanUserCode();
		if(StringHelper.isNotEmpty(scanUserCode)) {
			if(!BusinessUtil.isScanUserCode(scanUserCode)){
				result.toFail("请扫描正确的人员码！");
				return result;
			}
			userSignRequest.setJobCode(BusinessUtil.getJobCodeFromScanUserCode(scanUserCode));
			userSignRequest.setUserCode(BusinessUtil.getUserCodeFromScanUserCode(scanUserCode));
		}else if(StringHelper.isEmpty(userSignRequest.getUserCode())) {
			result.toFail("用户编码不能为空！");
			return result;
		}
		return checkUserSignStatus(positionCode,userSignRequest.getJobCode(),userSignRequest.getUserCode());
	}
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.queryUserDataForLogin",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<ScanUserData> queryUserDataForLogin(ScanForLoginRequest scanRequest) {
		JdCResponse<ScanUserData> result = new JdCResponse<>();
		result.toSucceed();
		if(scanRequest ==null || !BusinessUtil.isScanUserCode(scanRequest.getScanUserCode())) {
			result.toFail("请扫描正确的人员码！");
			return result;
		}
		String scanUserCode = scanRequest.getScanUserCode();
		String positionCode = scanRequest.getPositionCode();
		Integer jobCode =  BusinessUtil.getJobCodeFromScanUserCode(scanUserCode);
		String userCode = BusinessUtil.getUserCodeFromScanUserCode(scanUserCode);
		if(!JobTypeEnum.JOBTYPE1.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE2.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE6.getCode().equals(jobCode)) {
			result.toFail("请扫描[正式工、派遣工、支援]人员码！");
			return result;
		}
		//已扫描岗位码，校验在岗状态
		if(StringUtils.isNotBlank(positionCode)) {
			JdCResponse<UserSignRecordData> checkResult = checkUserSignStatus(positionCode,jobCode,userCode);
			if(checkResult.needConfirm()) {
				result.toConfirm(checkResult.getMessage());
			}
		}
		//设置返回值对象
		ScanUserData data = new ScanUserData();
		data.setJobCode(jobCode);
		data.setUserCode(userCode);
		result.setData(data);
		return result;
	}
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.queryPositionDataForLogin",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<PositionData> queryPositionDataForLogin(ScanForLoginRequest scanRequest) {
		JdCResponse<PositionData> result = new JdCResponse<>();
		result.toSucceed();
		if(scanRequest ==null || StringUtils.isBlank(scanRequest.getPositionCode())) {
			result.toFail("请扫描正确的网格码！");
			return result;
		}
		String positionCode = scanRequest.getPositionCode();
		try{
			log.info("UserSignGatewayServiceImpl.queryPositionInfo 入参-{}",positionCode);
			Result<com.jdl.basic.api.domain.position.PositionData> apiResult = positionManager.queryPositionInfo(positionCode);
			if(apiResult == null){
				result.toFail("查询网格码失败！");
				return result;
			}
			if(!apiResult.isSuccess()
					|| apiResult.getData() == null){
				result.toFail("扫描的网格码无效！");
				return result;
			}
			//已扫描人员码，校验在岗状态
			if(StringUtils.isNotBlank(scanRequest.getUserCode())) {
				JdCResponse<UserSignRecordData> checkResult = checkUserSignStatus(positionCode,scanRequest.getJobCode(),scanRequest.getUserCode());
				if(checkResult.needConfirm()) {
					result.toConfirm(checkResult.getMessage());
				}
			}
			//设置返回值对象
			PositionData positionData = new PositionData();
			BeanUtils.copyProperties(apiResult.getData(),positionData);
			result.setData(positionData);
		}catch (Exception e){
			log.error("queryPositionData查询岗位信息异常-{}",e.getMessage(),e);
			result.toError("查询岗位信息异常");
		}
		return result ;
	}
	private JdCResponse<UserSignRecordData> checkUserSignStatus(String positionCode,Integer jobCode,String userCode){
		JdCResponse<UserSignRecordData> result = new JdCResponse<UserSignRecordData>();
		result.toSucceed();
		UserSignQueryRequest query = new UserSignQueryRequest();
		query.setUserCode(userCode);
		UserSignRecordData lastUnSignOutData = null;
		JdCResponse<UserSignRecordData> lastUnSignOutResult = this.userSignRecordService.queryLastUnSignOutRecordData(query);
		if(lastUnSignOutResult != null
				&&lastUnSignOutResult.getData() != null) {
			lastUnSignOutData = lastUnSignOutResult.getData();
		}
		//判断在岗状态，在岗岗位码和当前不一致，给出提示
		if(lastUnSignOutData != null 
				&& lastUnSignOutData.getPositionCode() != null
				&& !positionCode.equals(lastUnSignOutData.getPositionCode())) {
			String workName = lastUnSignOutData.getPositionCode();
            Map<String, String> argsMap = new HashMap<>();
            if(StringUtils.isNotBlank(lastUnSignOutData.getGridName())
            		&& StringUtils.isNotBlank(lastUnSignOutData.getWorkName())) {
            	workName = StringHelper.append(lastUnSignOutData.getGridName(), Constants.SEPARATOR_COMMA_CN+lastUnSignOutData.getWorkName());
            }else {
            	workName = lastUnSignOutData.getPositionCode();
            }
            argsMap.put(HintArgsConstants.ARG_FIRST, workName);
            String defaultMsg = String.format(HintCodeConstants.CONFIRM_CHANGE_GW_FOR_SIGN_MSG, workName);
			result.toConfirm(HintService.getHint(defaultMsg,HintCodeConstants.CONFIRM_CHANGE_GW_FOR_SIGN, argsMap));
		}
		return result;
	}
}
