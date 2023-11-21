package com.jd.bluedragon.distribution.station.gateway.impl;


import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.attBlackList.AttendanceBlackListManager;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.ql.basic.domain.BaseStaff;
import com.jd.ql.basic.dto.BaseStaffSiteDTO;
import com.jd.ql.basic.dto.ResultData;
import com.jdl.basic.api.domain.attBlackList.AttendanceBlackList;
import com.jdl.basic.common.utils.DateUtil;
import com.jdl.basic.common.utils.Result;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.PositionData;
import com.jd.bluedragon.common.dto.station.ScanForLoginRequest;
import com.jd.bluedragon.common.dto.station.ScanUserData;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.station.enums.JobTypeEnum;
import com.jd.bluedragon.distribution.station.gateway.UserSignGatewayService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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
	
	@Value("${beans.userSignGatewayService.needCheckAutoSignOutHours:2}")
	private int needCheckAutoSignOutHours;

	@Autowired
	private AttendanceBlackListManager attendanceBlackListManager;

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


		String userCode=userSignRequest.getUserCode();
		boolean isCarId = BusinessUtil.isIdCardNo(userCode);
		if(isCarId){
			String  msg=checkAttendanceBlackList(result,userCode);
			if(StringUtils.isNotBlank(msg)){
				return result;
			}
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
				&& !JobTypeEnum.JOBTYPE4.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE5.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE6.getCode().equals(jobCode)
				&& !JobTypeEnum.JOBTYPE7.getCode().equals(jobCode)){
			result.toFail("请扫描[正式工、派遣工、临时工、小时工、支援、长期联盟工]人员码！");
			return result;
		}
		if(JobTypeEnum.JOBTYPE4.getCode().equals(jobCode) || JobTypeEnum.JOBTYPE5.getCode().equals(jobCode)){
			if(!BusinessUtil.isIdCardNo(userCode)){
				result.toFail("临时工、小时工 人员码必须包含身份证号");
				return result;
			}

			String loginUserPin = getLoginUserPin(result, userCode);
			if(!result.getCode().equals(JdResponse.CODE_OK)){
				return result;
			}
			userCode = loginUserPin;
		}

		//设置返回值对象
		ScanUserData data = new ScanUserData();
		data.setJobCode(jobCode);
		data.setUserCode(userCode);
		result.setData(data);

		//已扫描岗位码，校验在岗状态
		if(StringUtils.isNotBlank(positionCode)) {
			JdCResponse<UserSignRecordData> checkResult = checkUserSignStatus(positionCode,jobCode,userCode);
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
	private String getLoginUserPin(JdCResponse<ScanUserData> response, String erpAccount){
		try{
			log.info("获取登录用户的PIN码 checkIDCardNoExists 入参-{}",erpAccount);
			BaseStaff baseStaff = baseMajorManager.checkIDCardNoExists(erpAccount);
			log.info("获取登录用户的PIN码 checkIDCardNoExists 出参-{}", JSON.toJSONString(baseStaff));
			if(baseStaff == null || baseStaff.getStaffNo() == null){
				response.setMessage("未获取达达人员数据，请检查青龙基础资料中是否存在员工信息!");
				response.setCode(JdResponse.CODE_INTERNAL_ERROR);
				return "";
			}
			log.info("获取登录用户的PIN码 queryBaseStaffByStaffId 入参-{}",baseStaff.getStaffNo());
			ResultData<BaseStaffSiteDTO> staffInfo = baseMajorManager.queryBaseStaffByStaffId(baseStaff.getStaffNo());
			log.info("获取登录用户的PIN码 queryBaseStaffByStaffId 出参-{}",JSON.toJSONString(staffInfo));
			if(staffInfo == null || staffInfo.getData() == null ||org.apache.commons.lang.StringUtils.isBlank(staffInfo.getData().getPin())){
				response.setMessage("未获取达达人员数据，请检查青龙基础资料中是否存在员工信息!");
				response.setCode(JdResponse.CODE_INTERNAL_ERROR);
				return "";
			}
			return Constants.PDA_THIRDPL_TYPE+staffInfo.getData().getPin();
		}catch (Exception e){
			log.error("获取达达人员数据信息异常！{}",erpAccount,e);
			response.setMessage("获取达达人员数据信息异常！{"+erpAccount+"}");
			response.setCode(JdResponse.CODE_INTERNAL_ERROR);
			return "";
		}
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
				JdCResponse<UserSignRecordData> checkResult = checkUserSignStatus(positionCode,scanRequest.getJobCode(),scanRequest.getUserCode());
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
			return result;
		}

		// 校验网格码场地和用户场地是否一致
		if (!this.checkOperatorBaseInfo(positionCode, userCode)) {
			result.toConfirm(HintService.getHint(HintCodeConstants.CONFIRM_ITE_OR_PROVINCE_DIFF_FOR_SIGN_MSG,
					HintCodeConstants.CONFIRM_ITE_OR_PROVINCE_DIFF_FOR_SIGN_CODE, false));
			return result;
		}
		//判断上次签退是否人脸识别自动签退
		if(lastUnSignOutData == null) {
			UserSignQueryRequest lastSignQuery = new UserSignQueryRequest();
			lastSignQuery.setUserCode(userCode);
			JdCResponse<UserSignRecordData> lastSignResult = this.userSignRecordService.queryLastUserSignRecordData(lastSignQuery);
			UserSignRecordData lastSignData = null;
			if(lastSignResult != null
					&&lastSignResult.getData() != null) {
				lastSignData = lastSignResult.getData();
				//需要判断当前时间与系统自动签退时间是否小于2小时，若小于2小时,需要确认
				Date checkTime = DateHelper.addHours(new Date(), -needCheckAutoSignOutHours);
				if(DmsConstants.USER_CODE_AUTO_SIGN_OUT_FORM_RZ.equals(lastSignData.getUpdateUser())
						&& lastSignData.getSignOutTime() != null
						&& lastSignData.getSignOutTime().after(checkTime)) {
					result.toConfirm(HintCodeConstants.CONFIRM_AUTO_SIGN_OUT_FOR_SIGN_MSG);	
				}
			}
		}
		return result;
	}

	/**
	 * 作业APP网格码错误检验
	 */
	private boolean checkOperatorBaseInfo(String positionCode, String userCode) {
		if (StringUtils.isBlank(positionCode) || StringUtils.isBlank(userCode)) {
			return true;
		}
		// 查询网格码信息
		Result<PositionDetailRecord> apiResult = positionManager.queryOneByPositionCode(positionCode);
		if(apiResult == null || !apiResult.isSuccess()  || apiResult.getData() == null){
			return true;
		}
		BaseSiteInfoDto dtoStaff = baseMajorManager.getBaseSiteInfoBySiteId(apiResult.getData().getSiteCode());
		if (dtoStaff == null) {
			return true;
		}
		// 查询人员信息
		BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(userCode);
		if(baseStaffByErp == null) {
			return true;
		}
		// 网格码为分拣场地类型
		if (BusinessUtil.isSortingCenter(dtoStaff.getSortType(), dtoStaff.getSortSubType(),dtoStaff.getSortThirdType())) {
			// 所属场地是否与当前网格码对应场地一致
			return baseStaffByErp.getSiteCode().equals(apiResult.getData().getSiteCode());
		}
		// 网格码为接货仓场地类型
		if (BusinessUtil.isReceivingWarehouse(dtoStaff.getSortType())) {
			// 所属场地对应省区与网格码所属接货仓省区是否一致
			if (StringUtils.isBlank(baseStaffByErp.getProvinceAgencyCode()) || StringUtils.isBlank(apiResult.getData().getProvinceAgencyCode())) {
				return true;
			}
			return baseStaffByErp.getProvinceAgencyCode().equals(apiResult.getData().getProvinceAgencyCode());
		}
		return true;
	}
	private String checkAttendanceBlackList(JdCResponse<UserSignRecordData> result,String userCode){
		//查询出勤黑名单，并校验
		com.jdl.basic.common.utils.Result<AttendanceBlackList> rs=attendanceBlackListManager.queryByUserCode(userCode);
		if(rs == null){
			return "调用AttendanceBlackListJsfService失败";
		}
		if(rs.isSuccess()){
			AttendanceBlackList attendanceBlackList=rs.getData();
			if(attendanceBlackList !=null){
				int cancelFlag=attendanceBlackList.getCancelFlag();
				Date takeTime=attendanceBlackList.getTakeTime();
				String dateStr= DateUtil.format(new Date(),DateUtil.FORMAT_DATE_MINUTE);
				Date currentTime=DateUtil.parse(dateStr,DateUtil.FORMAT_DATE_MINUTE);
				if(cancelFlag ==Constants.NUMBER_ZERO && (currentTime.compareTo(takeTime) < Constants.NUMBER_ZERO)){
					//待生效
					String defaultMsg = String.format(HintCodeConstants.ATTENDANCE_BLACK_LIST_TOBE_EFFECTIVE_MSG, userCode,DateUtil.format(takeTime,DateUtil.FORMAT_DATE));
					result.toConfirm(defaultMsg);
					return defaultMsg;
				}
			}
		}
		return  "";
	}
}
