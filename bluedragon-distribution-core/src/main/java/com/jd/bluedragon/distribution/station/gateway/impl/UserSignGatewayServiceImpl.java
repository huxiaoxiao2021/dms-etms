package com.jd.bluedragon.distribution.station.gateway.impl;


import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.*;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.station.enums.JobTypeEnum;
import com.jd.bluedragon.distribution.station.gateway.UserSignGatewayService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.basic.domain.BaseStaff;
import com.jd.ql.basic.dto.BaseStaffSiteDTO;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

	@Autowired
	private BaseMajorManager baseMajorManager;

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
				if (Objects.nonNull(positionData.getDefaultMenuCode())) {
					positionData.setPositionName(JyFuncCodeEnum.getFuncNameByCode(positionData.getDefaultMenuCode()));
				}
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
				&& !JobTypeEnum.JOBTYPE6.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE7.getCode().equals(jobCode)) {
			result.toFail("请扫描[正式工、派遣工、支援、联盟工]人员码！");
			return result;
		}
		return result;
	}
	@Override
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.signInWithGroup",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
	@Override
	/**
	 * 查询最近一次-已签未退记录
	 * @param query
	 * @return
	 */
	public JdCResponse<UserSignRecordData> queryLastUnSignOutRecordData(UserSignQueryRequest query) {
		return userSignRecordService.queryLastUnSignOutRecordData(query);
	}
	@JProfiler(jKey = "dmsWeb.server.userSignGatewayService.checkBeforeSignIn",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<UserSignRecordData> checkBeforeSignIn(UserSignRequest userSignRequest) {
		return userSignRecordService.checkBeforeSignIn(userSignRequest);
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
		ScanUserData scanUserData = new ScanUserData();

		String scanUserCode = scanRequest.getScanUserCode();
		String positionCode = scanRequest.getPositionCode();
		Integer jobCode =  BusinessUtil.getJobCodeFromScanUserCode(scanUserCode);
		String userCode = BusinessUtil.getUserCodeFromScanUserCode(scanUserCode);
		scanUserData.setUserCode(userCode);
		if(!JobTypeEnum.JOBTYPE1.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE2.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE4.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE5.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE6.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE7.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE8.getCode().equals(jobCode)){
			result.toFail("请扫描[正式工、派遣工、临时工、小时工、支援、长期联盟工、达达]人员码！");
			return result;
		}
		if(JobTypeEnum.JOBTYPE4.getCode().equals(jobCode)
				|| JobTypeEnum.JOBTYPE5.getCode().equals(jobCode)
				|| JobTypeEnum.JOBTYPE8.getCode().equals(jobCode)){
			if(!BusinessUtil.isIdCardNo(userCode)){
				result.toFail("临时工、小时工 人员码必须包含身份证号");
				return result;
			}

			result.setData(scanUserData);
			ScanUserData scanUserDataNotJdSelf = getLoginUserInfo(result, userCode);
			if(!result.getCode().equals(JdResponse.CODE_OK)){
				return result;
			}
			if(scanUserDataNotJdSelf == null){
				return result;
			}
			scanUserData.setUserId(scanUserDataNotJdSelf.getUserId());
			scanUserData.setUserCode(scanUserDataNotJdSelf.getUserCode());
		}

		//设置返回值对象
		scanUserData.setJobCode(jobCode);
		result.setData(scanUserData);

		//已扫描岗位码，校验在岗状态
		if(StringUtils.isNotBlank(positionCode)) {
			JdCResponse<UserSignRecordData> checkResult = userSignRecordService.checkUserSignStatus(positionCode, jobCode, userCode);
			if(checkResult.needConfirm()) {
				result.toConfirm(checkResult.getMessage());
			}
		}

		return result;
	}



	/**
	 * 获取登录用户的PIN码
	 *
	 * @param erpAccount ERP账户
	 * @return 登录用户的PIN码
	 */
	private ScanUserData getLoginUserInfo(JdCResponse<ScanUserData> response, String erpAccount){
		ScanUserData scanUserData = new ScanUserData();
		try{
			log.info("获取登录用户的PIN码 checkIDCardNoExists 入参-{}",erpAccount);
			BaseStaff baseStaff = baseMajorManager.checkIDCardNoExists(erpAccount);
			log.info("获取登录用户的PIN码 checkIDCardNoExists 出参-{}", JSON.toJSONString(baseStaff));
			if(baseStaff == null || baseStaff.getStaffNo() == null){
				response.setMessage("未获取到人员数据，请检查青龙基础资料中是否存在员工信息!");
				response.setCode(JdResponse.CODE_INTERNAL_ERROR);
				return null;
			}
			log.info("获取登录用户的PIN码 queryBaseStaffByStaffId 入参-{}",baseStaff.getStaffNo());
			BaseStaffSiteDTO staffInfo = baseMajorManager.queryBaseStaffByStaffId(baseStaff.getStaffNo());
			log.info("获取登录用户的PIN码 queryBaseStaffByStaffId 出参-{}",JSON.toJSONString(staffInfo));
			if(staffInfo== null ||org.apache.commons.lang.StringUtils.isBlank(staffInfo.getPin())){
				response.setMessage("未获取到人员数据，请检查青龙基础资料中是否存在员工信息!");
				response.setCode(JdResponse.CODE_INTERNAL_ERROR);
				return null;
			}
			scanUserData.setUserId(baseStaff.getStaffNo().longValue());
			scanUserData.setUserCode(Constants.PDA_THIRDPL_TYPE+staffInfo.getPin());
		}catch (Exception e){
			log.error("获取人员数据信息异常！{}",erpAccount,e);
			response.setMessage("获取人员数据信息异常！{"+erpAccount+"}");
			response.setCode(JdResponse.CODE_INTERNAL_ERROR);
			return null;
		}
		return null;
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
			Result<com.jdl.basic.api.domain.position.PositionData> apiResult = positionManager.queryPositionWithIsMatchAppFunc(positionCode);
			if(apiResult == null){
				result.toFail("查询网格码失败！");
				return result;
			}
			if(!apiResult.isSuccess()
					|| apiResult.getData() == null){
				result.toFail("扫描的网格码无效！");
				return result;
			}

			//设置返回值对象
			PositionData positionData = new PositionData();
			BeanUtils.copyProperties(apiResult.getData(),positionData);
			result.setData(positionData);

			//已扫描人员码，校验在岗状态
			if(StringUtils.isNotBlank(scanRequest.getUserCode())) {
				JdCResponse<UserSignRecordData> checkResult = userSignRecordService.checkUserSignStatus(positionCode, scanRequest.getJobCode(), scanRequest.getUserCode());
				if(checkResult.needConfirm()) {
					result.toConfirm(checkResult.getMessage());
				}
			}
		}catch (Exception e){
			log.error("queryPositionData查询岗位信息异常-{}",e.getMessage(),e);
			result.toError("查询岗位信息异常");
		}
		return result ;
	}
}
