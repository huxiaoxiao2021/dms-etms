package com.jd.bluedragon.distribution.station.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.JobCodeHoursDto;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.group.GroupMemberData;
import com.jd.bluedragon.common.dto.group.GroupMemberRequest;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.attBlackList.AttendanceBlackListManager;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.core.jsf.workStation.WorkGridScheduleManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationAttendPlanManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;
import com.jd.bluedragon.distribution.base.domain.SysConfigJobCodeHoursContent;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.group.JyGroupEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberTypeEnum;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupService;
import com.jd.bluedragon.core.jsf.jyJobType.JyJobTypeManager;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordDao;
import com.jd.bluedragon.distribution.station.domain.*;
import com.jd.bluedragon.distribution.station.entity.AttendDetailChangeTopicData;
import com.jd.bluedragon.distribution.station.entity.PositionSignNumDto;
import com.jd.bluedragon.distribution.station.entity.*;
import com.jd.bluedragon.distribution.station.enums.JobTypeEnum;
import com.jd.bluedragon.distribution.station.enums.ScheduleEnum;
import com.jd.bluedragon.distribution.station.enums.WaveTypeEnum;
import com.jd.bluedragon.distribution.station.enums.WaveTypeNewEnum;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.distribution.station.service.WorkStationAttendPlanService;
import com.jd.bluedragon.distribution.station.service.WorkStationGridService;
import com.jd.bluedragon.distribution.station.service.WorkStationService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.*;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.basic.domain.BaseSite;
import com.jd.ql.basic.domain.BaseStaff;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.attBlackList.AttendanceBlackList;
import com.jdl.basic.api.domain.jyJobType.JyJobType;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.domain.schedule.WorkGridSchedule;
import com.jdl.basic.api.domain.schedule.WorkGridScheduleRequest;
import com.jdl.basic.api.domain.workStation.WorkStation;
import com.jdl.basic.api.domain.workStation.WorkStationAttendPlan;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.domain.workStation.WorkStationJobTypeDto;
import com.jdl.basic.common.utils.DateUtil;
import com.jdl.jy.flat.dto.schedule.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 人员签到表--Service接口实现
 *
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Slf4j
@Service("userSignRecordService")
public class UserSignRecordServiceImpl implements UserSignRecordService {

	@Autowired
	@Qualifier("userSignRecordDao")
	private UserSignRecordDao userSignRecordDao;

	@Value("${beans.userSignRecordService.signDateRangeMaxDays:2}")
	private int signDateRangeMaxDays;

	@Value("${beans.userSignRecordService.queryByPositionRangeDays:2}")
	private int queryByPositionRangeDays;

	@Value("${beans.userSignGatewayService.needCheckAutoSignOutHours:2}")
	private int needCheckAutoSignOutHours;

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
	private WorkGridScheduleManager workGridScheduleManager;

	@Autowired
	private PositionManager positionManager;

	@Autowired
	@Qualifier("jyGroupMemberService")
	private JyGroupMemberService jyGroupMemberService;

	@Autowired
	@Qualifier("jyGroupService")
	private JyGroupService jyGroupService;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private DmsConfigManager dmsConfigManager;

	@Autowired
	private SysConfigService sysConfigService;

	/**
	 * 签到作废-小时数限制
	 */
	@Value("${beans.userSignRecordService.deleteCheckHours:4}")
	private double deleteCheckHours;
	@Autowired
	private AttendanceBlackListManager attendanceBlackListManager;
	/**
	 * 人资-自动签退（偏差当前时间：秒）
	 */
	@Value("${beans.userSignRecordService.autoSignOutByMqSenconds:30}")
	private int autoSignOutByMqOffSenconds;

	@Value("${app.config.runningMode:uat}")
	protected String env;

	private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0.00");
	private static final DecimalFormat RATE_FORMAT = new DecimalFormat("0.00%");
	private static final String MSG_EMPTY_OPERATE = "操作人信息为空，请退出重新登录后操作！";
	private static final String MSG_FORMAT_HAS_NO_PERMISSION_1 = "您不可以操作，请联系签到操作人%s操作!";
	private static final String MSG_FORMAT_HAS_NO_PERMISSION_2 = "您不可以操作，请联系签到操作人%s、网格负责人%s操作！";
	private static final String MSG_FORMAT_AUTO_SIGN_OUT_TITLE = "自动签退通知";
	private static final String MSG_FORMAT_AUTO_SIGN_OUT_CONTENT = "您好，系统识别当前您已通过人资人脸识别下班打卡，将自动签退您在%s的工作，有疑问可联系网格组长%s";
	public static final String CHECK_STANDARD_NUM_SIGN_MSG = "当前工序的标准编制人数为【%s】，目前已签到【%s】，请确认网格内是否有【%s】(人数)人作业，若不对，请联系网格组长ERP【%s】操作签退处理；";

	private static final String MSG_FORMAT_AUTO_SIGN_OUT_GATE_CONTENT = "您好，系统识别当前您已通过场地人闸识别下班打卡，将自动签退您在%s的工作，有疑问可联系网格组长%s";
	@Autowired
	private WorkStationManager workStationManager;
	@Autowired
	private WorkStationGridManager workStationGridManager;

	@Autowired
	private JyJobTypeManager jyJobTypeManager;

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(UserSignRecord insertData){
		Result<Boolean> result = Result.success();
		// fill basic site info
		if(insertData.getSiteCode() != null){
			BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(insertData.getSiteCode());
			insertData.setOrgCode(baseSite == null ? null : baseSite.getOrgId());
			insertData.setOrgName(baseSite == null ? null : baseSite.getOrgName());
			insertData.setProvinceAgencyCode(baseSite == null ? null : baseSite.getProvinceAgencyCode());
			insertData.setProvinceAgencyName(baseSite == null ? null : baseSite.getProvinceAgencyName());
			insertData.setAreaHubCode(baseSite == null ? null : baseSite.getAreaCode());
			insertData.setAreaHubName(baseSite == null ? null : baseSite.getAreaName());	
		}
		insertData.setIdCard(BusinessUtil.encryptIdCardDoubleStar(insertData.getUserCode()));
		result.setData(userSignRecordDao.insert(insertData) == 1);
		return result;
	 }
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public Result<Boolean> updateById(UserSignRecord updateData){
		Result<Boolean> result = Result.success();
		result.setData(userSignRecordDao.updateById(updateData) == 1);
		return result;
	 }
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	public Result<Boolean> deleteById(UserSignRecord deleteData){
		Result<Boolean> result = Result.success();
		result.setData(userSignRecordDao.deleteById(deleteData) == 1);
		return result;
	 }
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Result<UserSignRecord> queryById(Long id){
		Result<UserSignRecord> result = Result.success();
		result.setData(toUserSignRecordVo(userSignRecordDao.queryById(id)));
		return result;
	 }
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public Result<PageDto<UserSignRecord>> queryPageList(UserSignRecordQuery query){
		Result<PageDto<UserSignRecord>> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		PageDto<UserSignRecord> pageData = new PageDto<>(query.getPageNumber(), query.getPageSize());
		Long totalCount = userSignRecordDao.queryCount(query);
		if(totalCount != null && totalCount > 0){
		    List<UserSignRecord> dataList = userSignRecordDao.queryList(query);
		    for (UserSignRecord tmp : dataList) {
		    	this.fillOtherInfo(tmp);
		    }
		    pageData.setResult(dataList);
		    pageData.setTotalRow(totalCount.intValue());
		}else {
			pageData.setResult(new ArrayList<UserSignRecord>());
			pageData.setTotalRow(0);
		}
		result.setData(pageData);
		return result;
	 }
	/**
	 * 查询参数校验
	 * @param query
	 * @return
	 */
	public Result<Boolean> checkParamForQueryPageList(UserSignRecordQuery query){
		Result<Boolean> result = Result.success();
		if(query.getPageSize() == null
				|| query.getPageSize() <= 0) {
			query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT);
		}
		query.setOffset(0);
		query.setLimit(query.getPageSize());
		if(query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
		}
		//当天0点
		Date nowDate = DateHelper.parseDate(DateHelper.getDateOfyyMMdd2(),DateHelper.DATE_FORMAT_YYYYMMDD);
		Date signDate = null;
		if(StringHelper.isNotEmpty(query.getSignDateStr())) {
			signDate = DateHelper.parseDate(query.getSignDateStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
		}
		if(signDate == null) {
			Date signDateStart = null;
			Date signDateEnd = null;
			if(StringHelper.isNotEmpty(query.getSignDateStartStr())) {
				signDateStart = DateHelper.parseDate(query.getSignDateStartStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
			}
			if(StringHelper.isNotEmpty(query.getSignDateEndStr())) {
				signDateEnd = DateHelper.parseDate(query.getSignDateEndStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
			}
			//有日期查询范围，校验是否超过7天
			if(signDateStart != null && signDateEnd != null) {
				Date checkDate = DateHelper.addDate(signDateStart,signDateRangeMaxDays);
				if(signDateEnd.after(checkDate)) {
					return result.toFail("查询日期范围不能超过" + (signDateRangeMaxDays + 1) + "天");
				}
				query.setSignDateStart(signDateStart);
				query.setSignDateEnd(signDateEnd);
			}else {
				signDate = DateHelper.parseDate(DateHelper.getDateOfyyMMdd2(),DateHelper.DATE_FORMAT_YYYYMMDD);
			}
		}
		query.setSignDate(signDate);
		//设置实时在岗计算时间
		query.setNowDateStart(DateHelper.addDate(nowDate, -1));
		query.setNowDateEnd(DateHelper.add(nowDate, Calendar.SECOND, (int)DateHelper.ONE_DAY_SECONDS - 1));
		return result;
	 }

	/**
	 * 对象转换成vo
	 * @param data
	 * @return
	 */
	public UserSignRecord toUserSignRecordVo(UserSignRecord data){
		if(data == null) {
			return null;
		}
		UserSignRecord vo = new UserSignRecord();
		BeanUtils.copyProperties(data, vo);
		//特殊字段设置
		fillOtherInfo(vo);
		return vo;
	 }
	public UserSignRecord fillOtherInfo(UserSignRecord vo) {
		if(vo == null) {
			return null;
		}
		vo.setWaveName(WaveTypeEnum.getNameByCode(vo.getWaveCode()));
		vo.setJobName(JobTypeEnum.getNameByCode(vo.getJobCode()));
		if(vo.getSignInTime() != null
				&& vo.getSignOutTime() != null) {
			String workHours = "";
			double workHoursDouble = DateHelper.betweenHours(vo.getSignInTime(),vo.getSignOutTime());
			workHours = NUMBER_FORMAT.format(workHoursDouble);
			vo.setWorkHours(workHours);
		}
		return vo;
	}
	@Override
	public Result<PageDto<UserSignRecordReportVo>> queryReportPageList(UserSignRecordQuery query) {
		Result<PageDto<UserSignRecordReportVo>> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		PageDto<UserSignRecordReportVo> PageDto = new PageDto<>(query.getPageNumber(), query.getPageSize());
		Long totalCount = userSignRecordDao.queryReportCount(query);
		if(totalCount != null && totalCount > 0){
		    List<UserSignRecordReportVo> dataList = userSignRecordDao.queryReportList(query);
		    for (UserSignRecordReportVo tmp : dataList) {
		    	tmp.setWaveName(WaveTypeEnum.getNameByCode(tmp.getWaveCode()));
		    }
		    PageDto.setResult(dataList);
			PageDto.setTotalRow(totalCount.intValue());
		}else {
		    PageDto.setResult(new ArrayList<UserSignRecordReportVo>());
			PageDto.setTotalRow(0);
		}

		result.setData(PageDto);
		return result;
	}
	@Override
	public Result<Boolean> signIn(UserSignRecord signInRequest) {
		Result<Boolean> result = Result.success();

		Integer siteCode = signInRequest.getSiteCode();
		Integer floor = signInRequest.getFloor();
		String gridNo = signInRequest.getGridNo();
		String areaCode = signInRequest.getAreaCode();
		String workCode = signInRequest.getWorkCode();

		signInRequest.setUserCode(signInRequest.getUserCode().trim());
		log.info("signIn -获取基础服务数据");
		WorkStation workStationCheckQuery = new WorkStation ();
		workStationCheckQuery.setWorkCode(workCode);
		workStationCheckQuery.setAreaCode(areaCode);
		com.jdl.basic.common.utils.Result<WorkStation> workStationData = workStationManager.queryByBusinessKey(workStationCheckQuery);
		if(workStationData == null
				|| workStationData.getData() == null) {
			return result.toFail("作业区工序信息不存在，请先维护作业区及工序信息！");
		}
		WorkStation workStation = workStationData.getData();
		signInRequest.setRefStationKey(workStation.getBusinessKey());
		//校验并设置网格信息
		com.jdl.basic.api.domain.workStation.WorkStationGrid  workStationGridCheckQuery = new com.jdl.basic.api.domain.workStation.WorkStationGrid ();
		workStationGridCheckQuery.setFloor(floor);
		workStationGridCheckQuery.setSiteCode(siteCode);
		workStationGridCheckQuery.setGridNo(gridNo);
		workStationGridCheckQuery.setRefStationKey(workStation.getBusinessKey());
		com.jdl.basic.common.utils.Result<com.jdl.basic.api.domain.workStation.WorkStationGrid> workStationGridData = workStationGridManager.queryByBusinessKey(workStationGridCheckQuery);
		if(workStationGridData == null
				|| workStationGridData.getData() == null) {
			return result.toFail("网格信息不存在，请先维护场地网格信息！");
		}

		signInRequest.setRefGridKey(workStationGridData.getData().getBusinessKey());
		//查询设置计划信息
		com.jdl.basic.api.domain.workStation.WorkStationAttendPlan  workStationAttendPlanQuery = new com.jdl.basic.api.domain.workStation.WorkStationAttendPlan ();
		workStationAttendPlanQuery.setRefGridKey(workStationGridData.getData().getBusinessKey());
		workStationAttendPlanQuery.setWaveCode(signInRequest.getWaveCode());
		com.jdl.basic.common.utils.Result<com.jdl.basic.api.domain.workStation.WorkStationAttendPlan> planData = workStationAttendPlanManager.queryByBusinessKeys(workStationAttendPlanQuery);
		if(planData != null
				&& planData.getData() != null) {
			signInRequest.setRefPlanKey(planData.getData().getBusinessKey());
		}
        Date now = new Date();

        // 自动将上次未签退数据签退。
        boolean autoSignOutSuccess = autoSignOutLastSignInRecord(signInRequest, now);

        Date signInTime = now;
        if (autoSignOutSuccess) {
            signInTime = new Date(now.getTime() + 1000);
        }
        signInRequest.setCreateTime(signInTime);
        signInRequest.setSignInTime(signInTime);
        signInRequest.setSignDate(signInRequest.getSignInTime());
		// 设置战区信息
		setWarZoneInfo(signInRequest);
        this.insert(signInRequest);

        if (autoSignOutSuccess) {
            result = new Result<>(201, "签到成功，自动将上次签到数据签退！");
        }

        return result;
	}

    /**
     * 自动签退逻辑
     * @param signInRequest
     * @param signOutTime
     */
    private boolean autoSignOutLastSignInRecord(UserSignRecord signInRequest, Date signOutTime) {
        UserSignRecord lastSignRecord = getLastSignRecord(signInRequest);
        if (lastSignRecord != null && lastSignRecord.getSignOutTime() == null) {
            UserSignRecord signOutRequest = new UserSignRecord();

            signOutRequest.setId(lastSignRecord.getId());
            signOutRequest.setUpdateTime(signOutTime);
            signOutRequest.setSignOutTime(signOutTime);
            return userSignRecordDao.updateById(signOutRequest) > 0;
        }

        return false;
    }

    @Override
	public Result<Boolean> signOut(UserSignRecord signOutRequest) {
		Result<Boolean> result = Result.success();
		signOutRequest.setUserCode(signOutRequest.getUserCode().trim());

		UserSignRecord data = new UserSignRecord();
		if(signOutRequest.getId() != null) {
			data.setId(signOutRequest.getId());
		}else {
            UserSignRecord lastSignRecord = getLastSignRecord(signOutRequest);
            if(lastSignRecord == null) {
				return result.toFail("该用户未签到，无法签退！");
			}
			data.setId(lastSignRecord.getId());
		}
		data.setUpdateTime(new Date());
		data.setSignOutTime(new Date());
        data.setUpdateUser(signOutRequest.getUpdateUser());
        data.setUpdateUserName(signOutRequest.getUpdateUserName());
		userSignRecordDao.updateById(data);
		return result;
	}

    /**
     * 获取用户最近一次签到数据
     * @param signOutRequest
     * @return
     */
    private UserSignRecord getLastSignRecord(UserSignRecord signOutRequest) {
        UserSignRecordQuery query = new UserSignRecordQuery();
        query.setSiteCode(signOutRequest.getSiteCode());
        query.setUserCode(signOutRequest.getUserCode());
        query.setSignDate(signOutRequest.getSignDate());
        return userSignRecordDao.queryLastSignRecord(query);
    }

    @Override
	public Result<UserSignRecord> queryLastSignRecord(UserSignRecordQuery query) {
		Result<UserSignRecord> result = Result.success();
		result.setData(fillOtherInfo(userSignRecordDao.queryLastSignRecord(query)));
		return result;
	}
	private UserSignRecord queryLastUnSignOutRecord(UserSignRecordQuery query) {
		return fillOtherInfo(userSignRecordDao.queryLastUnSignOutRecord(query));
	}

	/**
	 * 忽略身份证末尾字母大小写查询签到记录
	 * 查最新一条未签退记录
	 * @param userCodeList
	 * @return
	 */
	private UserSignRecord queryLastUnSignOutRecordIgnoreCase(List<String> userCodeList) {
		return fillOtherInfo(userSignRecordDao.queryLastUnSignOutRecordIgnoreCase(userCodeList));
	}
	@Override
	public Result<UserSignRecordReportSumVo> queryReportSum(UserSignRecordQuery query) {
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		Result<UserSignRecordReportSumVo> result = Result.success();
		result.setData(userSignRecordDao.queryReportSum(query));
		if(result.getData() != null) {
			Integer attendNumSum = result.getData().getAttendNumSum();
			Integer planAttendNumSum = result.getData().getPlanAttendNumSum();
			if(NumberHelper.gt0(planAttendNumSum)
					&& attendNumSum != null) {
				double offValue = Math.abs((attendNumSum - planAttendNumSum) * 1.0 / planAttendNumSum);
				result.getData().setDeviationPlanRate(RATE_FORMAT.format(offValue));
			}else {
				result.getData().setDeviationPlanRate("--");
			}
		}
		return result;
	}

    @Override
    @JProfiler(jKey = "DMS.WEB.UserSignRecordService.autoHandleSignInRecord", jAppName= Constants.UMP_APP_NAME_DMSWORKER, mState={JProEnum.TP, JProEnum.FunctionError})
    public Result<Integer> autoHandleSignInRecord() {
        Result<Integer> result = Result.success();
		// 新增扫描数据范围
		List<Integer> siteCodeList = getSiteCodeByConfig();

		SysConfigJobCodeHoursContent content = sysConfigService.getSysConfigJobCodeHoursContent(Constants.SYS_CONFIG_NOT_SIGNED_OUT_RECORD_MORE_THAN_HOURS);
		if(content == null){
			return result;
		}
		Integer defaultHours=content.getDefaultHours();
		Integer notSignedOutRecordRangeHours = dmsConfigManager.getPropertyConfig().getNotSignedOutRecordRangeHours();
		//扫描范围不能小于1小时
		if(notSignedOutRecordRangeHours < 1) {
			notSignedOutRecordRangeHours = 1;
		}
		Date signInTimeEnd = DateHelper.add(new Date(),Calendar.HOUR_OF_DAY, -defaultHours);
		Date signInTimeStart = DateHelper.add(signInTimeEnd,Calendar.HOUR_OF_DAY,-notSignedOutRecordRangeHours);
		List<Map<String,Object>> list=content.getJobCodeHours();
		List<JobCodeHoursDto> jobCodeHoursRecordList=new ArrayList<>();
		List<Integer> allSpecialJobCodeList=new ArrayList();
		if(CollectionUtils.isNotEmpty(list)){
			for (Map<String, Object> map : list) {
				int jobCode=(int)map.get("jobCode");
				int hour=(int)map.get("hour");
				JobCodeHoursDto jobCodeHoursRecord=new JobCodeHoursDto();
				jobCodeHoursRecord.setJobCode(jobCode);
				jobCodeHoursRecord.setHour(hour);
				jobCodeHoursRecordList.add(jobCodeHoursRecord);
				allSpecialJobCodeList.add(jobCode);
			}
		}
		Map<Integer,List<JobCodeHoursDto>> jobCodeHoursRecordMap=jobCodeHoursRecordList.stream().collect(Collectors.groupingBy(JobCodeHoursDto::getHour));
		List<JobCodeHoursDto> jobCodeHoursList=new ArrayList<>();
		for (Map.Entry<Integer, List<JobCodeHoursDto>> entry : jobCodeHoursRecordMap.entrySet()) {
			Integer hour=entry.getKey();
			List<JobCodeHoursDto> jhList=entry.getValue();
			List<Integer> jobCodes = jhList.stream().map(JobCodeHoursDto::getJobCode).collect(
					Collectors.toList());
			Date endDate = DateHelper.add(new Date(),Calendar.HOUR_OF_DAY, -hour);
			Date startDate = DateHelper.add(endDate,Calendar.HOUR_OF_DAY,-notSignedOutRecordRangeHours);
			JobCodeHoursDto jobCodeHoursRecord=new JobCodeHoursDto();
			jobCodeHoursRecord.setJobCodes(jobCodes);
			jobCodeHoursRecord.setStartTime(startDate);
			jobCodeHoursRecord.setEndTime(endDate);
			jobCodeHoursList.add(jobCodeHoursRecord);
		}

        List<Long> toSignOutPks;
        Date now = new Date();
        int updateRows = 0;
        log.info("自动签退数据扫描：{} - {}", DateHelper.formatDateTimeMs(signInTimeStart),DateHelper.formatDateTimeMs(signInTimeEnd));
        try {

			// 查询新逻辑试用场地列表
			List<String> siteList = sysConfigService.getStringListConfig(Constants.AUTO_SIGN_OUT_SCHEDULE_SITE);

            do {
                toSignOutPks = userSignRecordDao.querySignInMoreThanSpecifiedTime(allSpecialJobCodeList,
					jobCodeHoursList,signInTimeStart,signInTimeEnd, siteCodeList, env, 100);

				if (CollectionUtils.isNotEmpty(toSignOutPks)) {
					// 根据工种，更新签退时间=签到时间+工种设置的定时时间，工种没有时间，为默认时间
					UserSignRecord updateData = new UserSignRecord();
					updateData.setSignOutTime(now);
					updateData.setUpdateTime(now);
					updateData.setUpdateUser(DmsConstants.USER_CODE_AUTO_SIGN_OUT_TIME_OUT);
					updateData.setUpdateUserName(updateData.getUpdateUser());

					// 未排班的非自有员工签到记录主键列表,用来兜底
					List<Long> noScheduleList = new ArrayList<>();
					// 查询排班计划执行自动签退
					for (Long id : toSignOutPks) {
						autoSignOutNew(id, siteList, now, noScheduleList);
					}
					if (CollectionUtils.isNotEmpty(noScheduleList)) {
						userSignRecordDao.signOutById(updateData, noScheduleList);
					}
					updateRows += toSignOutPks.size();
        			GroupMemberRequest removeMemberRequest = new GroupMemberRequest();
        			removeMemberRequest.setSignRecordIdList(toSignOutPks);
        			removeMemberRequest.setOperateUserCode(updateData.getUpdateUser());
        			removeMemberRequest.setOperateUserName(updateData.getUpdateUserName());
                    this.jyGroupMemberService.removeMembers(removeMemberRequest);
                }

				Thread.sleep(200);

			} while (CollectionUtils.isNotEmpty(toSignOutPks));

			result.setData(updateRows);
		}
        catch (Exception e) {
            log.error("自动关闭未签退数据异常.", e);
            result.toFail(e.getMessage());
        }

        return result;
    }

	private void autoSignOutNew(Long recordId, List<String> siteCodeList, Date currentDate, List<Long> noScheduleList) {
		try {
			// 根据签到记录主键查询签到记录详情
			UserSignRecord userSignRecord = userSignRecordDao.queryByIdForFlow(recordId);
			if (userSignRecord == null) {
				log.warn("autoSignOutNew|根据主键未查询到签到记录详情:id={}", recordId);
				return;
			}
			// 签到人场地
			Integer siteCode = userSignRecord.getSiteCode();
			// 如果签到人场地在新逻辑试用场地中或者开启全国
			if (siteCodeList.contains(String.valueOf(siteCode)) || siteCodeList.contains(String.valueOf(Constants.NUMBER_ZERO))) {
				// 走新逻辑-基于排班自动签退
				handleAutoSignOut(userSignRecord, currentDate, noScheduleList);
			} else {
				// 如果不在,则走原有逻辑
				noScheduleList.add(recordId);
			}
		} catch (Exception e) {
			log.error("autoSignOutNew|自动签退新逻辑出现异常:id={}", recordId, e);
		}
	}

	private void handleAutoSignOut(UserSignRecord userSignRecord, Date currentDate, List<Long> noScheduleList) {
		// 主键
		Long recordId = userSignRecord.getId();
		// 员工ERP|拼音|身份证号
		String idCard = userSignRecord.getIdCard();
		// 工种
		Integer jobCode = userSignRecord.getJobCode();

		// 设置更新参数
		userSignRecord.setUpdateTime(currentDate);
		userSignRecord.setUpdateUser(Constants.SYS_NAME);
		userSignRecord.setUpdateUserName(Constants.SYS_NAME);

		// 签到日期
		Date signDate = userSignRecord.getSignDate();
		// 签到日期前一天
		String yesterday = DateHelper.formatDate(DateUtils.addDays(signDate, Constants.SIGN_BEFORE_ONE_DAY));

		// 查询指定人员指定日期的排班记录
		List<UserGridScheduleDto> totalScheduleList = findScheduleListByCondition(yesterday, userSignRecord);
		// 如果没有排班
		if (CollectionUtils.isEmpty(totalScheduleList)) {
			log.warn("handleAutoSignOut|根据条件未查询到排班计划:id={},idCard={},jobCode={}", recordId, idCard, jobCode);
			// 如果是正式工或派遣工，将该签到数据作废处理；若场地补签，则按网格出勤管理进行补签，审核即可
			if (JobTypeEnum.JOBTYPE1.getCode().equals(jobCode) || JobTypeEnum.JOBTYPE2.getCode().equals(jobCode)) {
				// 留痕-设置修改类型为2-人员未排班数据作废处理
				userSignRecord.setModifyType(Constants.CONSTANT_NUMBER_TWO);
				userSignRecordDao.deleteById(userSignRecord);
			} else {
				// 如果是非自有员工，需要走兜底逻辑----使用自动签退时间
				noScheduleList.add(recordId);
			}
			return;
		}
		// 签到日期的排班记录
		List<UserGridScheduleDto> signDateScheduleList = new ArrayList<>();
		// 签到日期前一天的排班记录
		List<UserGridScheduleDto> yesterdayScheduleList = new ArrayList<>();
		// 筛选出当天的和前一天的排班记录
		filterScheduleListByDate(totalScheduleList, signDateScheduleList, yesterdayScheduleList, yesterday);
		log.info("handleAutoSignOut|根据条件查询到排班计划:id={},idCard={},jobCode={},signDateList={},yesterdayList={}",
				recordId, idCard, jobCode, signDateScheduleList.size(), yesterdayScheduleList.size());

		// 开始执行签退
		startSignOut(userSignRecord, signDateScheduleList, yesterdayScheduleList, noScheduleList, currentDate);
	}

	private void filterScheduleListByDate(List<UserGridScheduleDto> totalList, List<UserGridScheduleDto> signDateScheduleList,
										  List<UserGridScheduleDto> yesterdayScheduleList, String yesterday) {
		for (UserGridScheduleDto scheduleDto : totalList) {
			if (yesterday.equals(scheduleDto.getScheduleDate())) {
				yesterdayScheduleList.add(scheduleDto);
			} else {
				signDateScheduleList.add(scheduleDto);
			}
		}
	}

	private List<UserGridScheduleDto> findScheduleListByCondition(String yesterday, UserSignRecord userSignRecord) {
		// 排班查询参数对象
		UserGridScheduleQueryDto scheduleQueryDto = new UserGridScheduleQueryDto();
		// 排班日期集合
		scheduleQueryDto.setScheduleDateList(Arrays.asList(yesterday, DateHelper.formatDate(userSignRecord.getSignDate())));
		// 工种
		scheduleQueryDto.setNature(String.valueOf(userSignRecord.getJobCode()));
		// 签到人erp
		scheduleQueryDto.setUserUniqueCode(userSignRecord.getUserCode());
		return workGridScheduleManager.getUserScheduleByCondition(scheduleQueryDto);
	}

	private void startSignOut(UserSignRecord userSignRecord, List<UserGridScheduleDto> signDateScheduleList,
							  List<UserGridScheduleDto> yesterdayScheduleList, List<Long> noScheduleList, Date currentDate) {
		// 签到时间
		Date signInTime = userSignRecord.getSignInTime();
		// 判断当前签到时间是否命中当天人员排班开始时间前一小时~排班结束时间（不能正好等于排班结束时间）
		// 若存在，则直接以排班结束时间替换自动签退时间 如果存在多条，选择最早的一条的数据
		Date minEndDate = computeMinScheduleEndDateBySceneOne(signDateScheduleList, signInTime);
		if (minEndDate != null) {
			signOutByTransaction(userSignRecord, minEndDate);
			return;
		}
		// 若不存在，则需要进一步判断
		// （对应跨天场景）签到时间是否在人员签到日期的前一天的排班计划人员排班开始时间前一小时~排班结束时间（不能正好等于排班结束时间），若存在，则以前一天排班计划的排班结束时间替换自动签退时间
		minEndDate = computeMinScheduleEndDateBySceneOne(yesterdayScheduleList, signInTime);
		if (minEndDate != null) {
			signOutByTransaction(userSignRecord, minEndDate);
			return;
		}
		// 如果当天和前一天都不满足
		// 则判断当天的排班计划中，是否存在排班班次的结束时间在签到时间~自动签退时间之间
		minEndDate = computeMinScheduleEndDateBySceneTwo(signDateScheduleList, signInTime, currentDate);
		// 若存在则更新班次结束时间为自动签退时间
		if (minEndDate != null) {
			signOutByTransaction(userSignRecord, minEndDate);
			return;
		}
		// 以上都不满足的情况下
		Integer jobCode = userSignRecord.getJobCode();
		// 如果是正式工或派遣工
		if (JobTypeEnum.JOBTYPE1.getCode().equals(jobCode) || JobTypeEnum.JOBTYPE2.getCode().equals(jobCode)) {
			// 留痕-设置修改类型为2-人员未排班数据作废处理
			userSignRecord.setModifyType(Constants.CONSTANT_NUMBER_TWO);
			// 认为该人员今天未排班，将该签到数据作废处理；若场地补签，则按网格出勤管理进行补签，审核即可
			userSignRecordDao.deleteById(userSignRecord);
		} else {
			// 如果是非自有员工，需要走兜底逻辑--使用自动签退时间
			noScheduleList.add(userSignRecord.getId());
		}
	}


	/**
	 * 场景二：比较排班结束时间是否在签到时间和当前时间之间，如果命中多个，取最早的结束时间
	 */
	private Date computeMinScheduleEndDateBySceneTwo(List<UserGridScheduleDto> scheduleList, Date signInTime, Date currentDate) {
		Date minEndDate = null;
		if (CollectionUtils.isEmpty(scheduleList)) {
			return null;
		}
		for (UserGridScheduleDto scheduleDto : scheduleList) {
			// 排班日期 yyyy-MM-dd
			String scheduleDate = scheduleDto.getScheduleDate();
			// 排班开始时间 HH:mm
			String startTime = scheduleDto.getStartTime();
			// 排班结束时间 HH:mm
			String endTime = scheduleDto.getEndTime();
			if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
				continue;
			}
			// 排班结束日期 yyyy-MM-dd HH:mm:ss
			Date endDate;
			// 如果结束日期代表第二天的时间点，则转换时需要加一天
			if (LocalTime.parse(startTime).isAfter(LocalTime.parse(endTime))) {
				endDate = getSpecialDateByStr(endTime, scheduleDate, Constants.NUMBER_ONE);
			} else {
				endDate = getSpecialDateByStr(endTime, scheduleDate, Constants.NUMBER_ZERO);
			}
			if (endDate.getTime() >= signInTime.getTime() && endDate.getTime() <= currentDate.getTime()) {
				if (minEndDate == null) {
					minEndDate = endDate;
				} else {
					if (minEndDate.after(endDate)) {
						minEndDate = endDate;
					}
				}
			}
		}
		return minEndDate;
	}

	/**
	 * 场景一：比较签到时间是否在排班计划开始时间前一小时和排班结束时间之间，如果命中多个，取最早的结束时间
	 */
	private Date computeMinScheduleEndDateBySceneOne(List<UserGridScheduleDto> scheduleList, Date signInTime) {
		Date minEndDate = null;
		if (CollectionUtils.isEmpty(scheduleList)) {
			return null;
		}
		for (UserGridScheduleDto scheduleDto : scheduleList) {
			// 排班日期 yyyy-MM-dd
			String scheduleDate = scheduleDto.getScheduleDate();
			// 排班开始时间 HH:mm
			String startTime = scheduleDto.getStartTime();
			// 排班结束时间 HH:mm
			String endTime = scheduleDto.getEndTime();
			if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime) || StringUtils.isBlank(scheduleDate)) {
				continue;
			}
			// 排班开始日期 yyyy-MM-dd HH:mm:ss
			Date startDate;
			// 排班结束日期 yyyy-MM-dd HH:mm:ss
			Date endDate;
			// 如果结束日期代表第二天的时间点，则转换时需要加一天
			if (LocalTime.parse(startTime).isAfter(LocalTime.parse(endTime))) {
				endDate = getSpecialDateByStr(endTime, scheduleDate, Constants.NUMBER_ONE);
            } else {
				endDate = getSpecialDateByStr(endTime, scheduleDate, Constants.NUMBER_ZERO);
            }
            startDate = getSpecialDateByStr(startTime, scheduleDate, Constants.NUMBER_ZERO);
            // 排班开始日期前一小时
			Date startDateBeforeOneHour = DateHelper.addHours(startDate, Constants.SCHEDULE_BEFORE_ONE_HOUR);
			if (signInTime.getTime() >= startDateBeforeOneHour.getTime() && signInTime.getTime() < endDate.getTime()) {
				if (minEndDate == null) {
					minEndDate = endDate;
				} else {
					if (minEndDate.after(endDate)) {
						minEndDate = endDate;
					}
				}
			}
		}
		return minEndDate;
	}



	/**
	 * 以事务的形式进行签退操作
	 */
	private void signOutByTransaction(UserSignRecord userSignRecord, Date minEndDate) {
		UserSignRecordServiceImpl userSignRecordService = (UserSignRecordServiceImpl) SpringHelper
				.getBean("userSignRecordService");
		userSignRecord.setSignOutTime(minEndDate);
		userSignRecordService.signOutByDeleteAndInsert(userSignRecord);
	}

	/**
	 * 为保证计提回算 以上逻辑均采用删除+增加，而不针对原有数据进行修改。
	 */
	@Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void signOutByDeleteAndInsert(UserSignRecord userSignRecord) {
		// 留痕-设置修改类型为1-系统修改未人工签退
		userSignRecord.setModifyType(Constants.NUMBER_ONE);
		// 先逻辑删除
		userSignRecordDao.deleteById(userSignRecord);
		userSignRecord.setId(null);
		// 再新增一条
		userSignRecordDao.insert(userSignRecord);
	}

	/**
	 * 将排班班次开始时间或结束时间由HH:mm形式转为yyyy-MM-dd HH:mm:ss形式
	 */
	private Date getSpecialDateByStr(String timeStr, String scheduleDate, Integer addDay) {
		LocalTime localTime = LocalTime.parse(timeStr);
		int hour = localTime.getHour();
		int minute = localTime.getMinute();
		LocalDate localDate = LocalDate.parse(scheduleDate);
		localDate = localDate.plusDays(addDay);
		ZoneId defaultZoneId = ZoneId.systemDefault();
		int year = localDate.getYear();
		Month month = localDate.getMonth();
		int day = localDate.getDayOfMonth();
		LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
        return Date.from(localDateTime.atZone(defaultZoneId).toInstant());
	}

	@Override
	public JdResult<Integer> autoHandleSignOutByAttendJmq(AttendDetailChangeTopicData mqData) {
		JdResult<Integer> result = new JdResult<Integer>();
		result.toSuccess();
		//只处理更新和新增操作的消息
		if(!AttendDetailChangeTopicData.OP_TYPE_ADD.equals(mqData.getOpType())
				&& !AttendDetailChangeTopicData.OP_TYPE_UPDATE.equals(mqData.getOpType())) {
			return result;
		}
		if(StringUtils.isBlank(mqData.getUserErp())) {
			log.info("autoHandleSignOutByAttendJmq：userErp为空，无需处理！");
			return result;
		}
		if(StringUtils.isBlank(mqData.getActualOffTime())) {
			log.info("autoHandleSignOutByAttendJmq：签退时间为空，无需处理！");
			return result;
		}
		Date actualOffTime = DateHelper.parseDateTime(mqData.getActualOffTime());
		if(actualOffTime == null) {
			log.warn("autoHandleSignOutByAttendJmq：签退时间【{}】格式不正确，无需处理！",mqData.getActualOffTime());
			return result;
		}
		Date curTime = new Date();
		if(new Date().before(actualOffTime)) {
			log.warn("autoHandleSignOutByAttendJmq：签退时间【{}】大于当前时间，无需处理！",mqData.getActualOffTime());
			return result;
		}
		Date checkTime = DateHelper.add(curTime, Calendar.SECOND, -autoSignOutByMqOffSenconds);
		if(actualOffTime.before(checkTime)) {
			log.warn("autoHandleSignOutByAttendJmq：用户【{}】签退时间【{}】偏差当前时间超过{}秒，无需处理！",mqData.getUserErp(),mqData.getActualOffTime(),autoSignOutByMqOffSenconds);
			return result;
		}
		//根据erp+场地查询，已签未退的数据
		UserSignRecordQuery query = new UserSignRecordQuery();
		query.setUserCode(mqData.getUserErp());
		UserSignRecord lastUnSignOutRecord = userSignRecordDao.queryLastUnSignOutRecord(query);
		if(lastUnSignOutRecord == null) {
			log.info("autoHandleSignOutByAttendJmq：用户【{}】已签未退数据为空，无需处理！",mqData.getUserErp());
			return result;
		}
		if(!actualOffTime.after(lastUnSignOutRecord.getSignInTime())) {
			log.info("autoHandleSignOutByAttendJmq：用户【{}】打卡签退时间签退时间【{}】小于签到时间，无需处理！",mqData.getUserErp(),mqData.getActualOffTime());
			return result;
		}
        if (lastUnSignOutRecord.getSiteCode() == null) {
        	log.info("autoHandleSignOutByAttendJmq：站点为空，无需处理！");
        	return result;
        }
        SysConfigContent content = sysConfigService.getSysConfigJsonContent(Constants.SYS_CONFIG_AUTOHANDLESIGNOUTSITECODES);
        if (content != null
        		&& !Boolean.TRUE.equals(content.getMasterSwitch())
        		&& !content.getSiteCodes().contains(lastUnSignOutRecord.getSiteCode())) {
        	log.info("autoHandleSignOutByAttendJmq：站点【{}】未开启自动签退，无需处理！",lastUnSignOutRecord.getSiteCode());
        	return result;
        }
		//执行-签退逻辑
		List<Long> toSignOutPks = new ArrayList<>();
        UserSignRecord updateData = new UserSignRecord();
        updateData.setSignOutTime(actualOffTime);
        updateData.setUpdateTime(new Date());
        updateData.setUpdateUser(DmsConstants.USER_CODE_AUTO_SIGN_OUT_FORM_RZ);
        updateData.setUpdateUserName(DmsConstants.USER_NAME_AUTO_SIGN_OUT_FORM_RZ);
        toSignOutPks.add(lastUnSignOutRecord.getId());
        userSignRecordDao.signOutById(updateData, toSignOutPks);
		GroupMemberRequest removeMemberRequest = new GroupMemberRequest();
		removeMemberRequest.setSignRecordIdList(toSignOutPks);
		removeMemberRequest.setOperateUserCode(updateData.getUpdateUser());
		removeMemberRequest.setOperateUserName(updateData.getUpdateUserName());
        this.jyGroupMemberService.removeMembers(removeMemberRequest);

        List<String> erpList = new ArrayList<>();
        erpList.add(mqData.getUserErp());

		com.jdl.basic.api.domain.workStation.WorkStationGridQuery  workStationGridCheckQuery = new com.jdl.basic.api.domain.workStation.WorkStationGridQuery ();
		workStationGridCheckQuery.setBusinessKey(lastUnSignOutRecord.getRefGridKey());
        this.workStationGridManager.queryByGridKey(workStationGridCheckQuery);
		com.jdl.basic.common.utils.Result<com.jdl.basic.api.domain.workStation.WorkStationGrid> workStationGridData = workStationGridManager.queryByGridKey(workStationGridCheckQuery);
		// 查询网格-负责人信息
		String ownerUserErp = "";
		String gridName = "";
		if(workStationGridData != null
				&& workStationGridData.getData() != null) {
			ownerUserErp = StringHelper.getValueFormatNull(workStationGridData.getData().getOwnerUserErp());
			gridName = workStationGridData.getData().getGridName();
		}
		//自动签退完发送咚咚通知
        NoticeUtils.noticeToTimelineWithNoUrl(MSG_FORMAT_AUTO_SIGN_OUT_TITLE, String.format(MSG_FORMAT_AUTO_SIGN_OUT_CONTENT, gridName,ownerUserErp), erpList);
		return result;
	}

	public static void main(String[] args) {
		Date actualOffTime = DateHelper.parseDateTime("2024-05-11 11:31:35");
		System.out.println(actualOffTime);
	}
	/**
	 * 人闸考勤数据-topic自动签退处理
	 * @param mqData
	 * @return
	 */
	@Override
	public JdResult<Integer> autoHandleSignOutByAttendGateJmq(AttendDetailChangeGateTopicData mqData) {
		JdResult<Integer> result = new JdResult<Integer>();
		result.toSuccess();
		mqData.setErpOrIdCard(org.apache.commons.lang3.StringUtils.isNotBlank(mqData.getErp()) ? mqData.getErp() : mqData.getIdCard());

		if(StringUtils.isBlank(mqData.getPassStatus()) || !mqData.getPassStatus().equalsIgnoreCase("出门")) {
			log.info("autoHandleSignOutByAttendGateJmq：非出门数据，无需处理！");
			return result;
		}

		if(StringUtils.isBlank(mqData.getErpOrIdCard())) {
			log.info("autoHandleSignOutByAttendGateJmq：userErp和idCard为空，无需处理！");
			return result;
		}
		if(StringUtils.isBlank(mqData.getPassTime())) {
			log.info("autoHandleSignOutByAttendGateJmq：签退时间为空，无需处理！");
			return result;
		}

		// 解析字符串为LocalDateTime对象
		LocalDateTime passTime = LocalDateTime.parse(mqData.getPassTime());
		// 定义新的日期时间格式
		mqData.setPassTime(passTime.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)));
		log.info("autoHandleSignOutByAttendGateJmq 入参 mqData:{}", JSON.toJSONString(mqData));

		Date actualOffTime = DateHelper.parseDateTime(mqData.getPassTime());
		if(actualOffTime == null) {
			log.info("autoHandleSignOutByAttendGateJmq：签退时间【{}】格式不正确，无需处理！",mqData.getPassTime());
			return result;
		}
		Date curTime = new Date();
		if(new Date().before(actualOffTime)) {
			log.info("autoHandleSignOutByAttendGateJmq：签退时间【{}】大于当前时间，无需处理！",mqData.getPassTime());
			return result;
		}
		Date checkTime = DateHelper.add(curTime, Calendar.SECOND, -autoSignOutByMqOffSenconds);
		if(actualOffTime.before(checkTime)) {
			log.info("autoHandleSignOutByAttendGateJmq：用户【{}】签退时间【{}】偏差当前时间超过{}秒，无需处理！",mqData.getErpOrIdCard(),mqData.getPassTime(),autoSignOutByMqOffSenconds);
			return result;
		}
		log.info("autoHandleSignOutByAttendGateJmq 校验通过 mqData:{}", mqData);
		//根据erp+场地查询，已签未退的数据
		UserSignRecordQuery query = new UserSignRecordQuery();
		query.setUserCode(mqData.getErpOrIdCard());
		UserSignRecord lastUnSignOutRecord = userSignRecordDao.queryLastUnSignOutRecord(query);
		log.info("autoHandleSignOutByAttendGateJmq 校验通过 query:{},lastUnSignOutRecord:{}", JSON.toJSONString(query), JSON.toJSONString(lastUnSignOutRecord));
		if(lastUnSignOutRecord == null) {
			log.info("autoHandleSignOutByAttendGateJmq：用户【{}】已签未退数据为空，无需处理！",mqData.getErpOrIdCard());
			return result;
		}
		if(!actualOffTime.after(lastUnSignOutRecord.getSignInTime())) {
			log.info("autoHandleSignOutByAttendGateJmq：用户【{}】打卡签退时间签退时间【{}】小于签到时间，无需处理！",mqData.getErpOrIdCard(),mqData.getPassTime());
			return result;
		}
		if (lastUnSignOutRecord.getSiteCode() == null) {
			log.info("autoHandleSignOutByAttendGateJmq：站点为空，无需处理！");
			return result;
		}
		SysConfigContent content = sysConfigService.getSysConfigJsonContent(Constants.SYS_CONFIG_AUTOHANDLESIGNOUTSITECODES);
		log.info("autoHandleSignOutByAttendGateJmq 获取配置 query:{},content:{}", JSON.toJSONString(query), JSON.toJSONString(content));
		if (content != null
				&& !Boolean.TRUE.equals(content.getMasterSwitch())
				&& !content.getSiteCodes().contains(lastUnSignOutRecord.getSiteCode())) {
			log.info("autoHandleSignOutByAttendGateJmq：站点【{}】未开启自动签退，无需处理！",lastUnSignOutRecord.getSiteCode());
			return result;
		}
		//执行-签退逻辑
		List<Long> toSignOutPks = new ArrayList<>();
		UserSignRecord updateData = new UserSignRecord();
		updateData.setSignOutTime(actualOffTime);
		updateData.setUpdateTime(new Date());
		updateData.setUpdateUser(DmsConstants.USER_CODE_AUTO_SIGN_OUT_FORM_RZ);
		updateData.setUpdateUserName(DmsConstants.USER_NAME_AUTO_SIGN_OUT_FORM_GATE);
		toSignOutPks.add(lastUnSignOutRecord.getId());
		log.info("autoHandleSignOutByAttendGateJmq 执行-签退逻辑 updateData:{},toSignOutPks:{}", JSON.toJSONString(updateData), toSignOutPks);
		userSignRecordDao.signOutById(updateData, toSignOutPks);
		GroupMemberRequest removeMemberRequest = new GroupMemberRequest();
		removeMemberRequest.setSignRecordIdList(toSignOutPks);
		removeMemberRequest.setOperateUserCode(updateData.getUpdateUser());
		removeMemberRequest.setOperateUserName(updateData.getUpdateUserName());
		log.info("autoHandleSignOutByAttendGateJmq 执行-removeMembers removeMemberRequest:{}", JSON.toJSONString(removeMemberRequest));
		this.jyGroupMemberService.removeMembers(removeMemberRequest);

		List<String> erpList = new ArrayList<>();
		erpList.add(mqData.getErpOrIdCard());

		com.jdl.basic.api.domain.workStation.WorkStationGridQuery  workStationGridCheckQuery = new com.jdl.basic.api.domain.workStation.WorkStationGridQuery ();
		workStationGridCheckQuery.setBusinessKey(lastUnSignOutRecord.getRefGridKey());
		com.jdl.basic.common.utils.Result<com.jdl.basic.api.domain.workStation.WorkStationGrid> workStationGridData = workStationGridManager.queryByGridKey(workStationGridCheckQuery);

		log.info("autoHandleSignOutByAttendGateJmq 执行-queryByGridKey workStationGridCheckQuery:{}, workStationGridData:{}", JSON.toJSONString(workStationGridCheckQuery), JSON.toJSONString(workStationGridData));
		// 查询网格-负责人信息
		String ownerUserErp = "";
		String gridName = "";
		if(workStationGridData != null
				&& workStationGridData.getData() != null) {
			ownerUserErp = StringHelper.getValueFormatNull(workStationGridData.getData().getOwnerUserErp());
			gridName = workStationGridData.getData().getGridName();
		}
		log.info("autoHandleSignOutByAttendGateJmq 发送消息 1:{}, 2:{},erpList:{}", MSG_FORMAT_AUTO_SIGN_OUT_TITLE, String.format(MSG_FORMAT_AUTO_SIGN_OUT_GATE_CONTENT, gridName,ownerUserErp), erpList);
		//自动签退完发送咚咚通知
		NoticeUtils.noticeToTimelineWithNoUrl(MSG_FORMAT_AUTO_SIGN_OUT_TITLE, String.format(MSG_FORMAT_AUTO_SIGN_OUT_GATE_CONTENT, gridName,ownerUserErp), erpList);
		return result;
	}

	/**
     * 根据条件查询-转成通知对象
     * @param query
     * @return
     */
    public Result<UserSignNoticeVo> queryUserSignRecordToNoticeVo(UserSignRecordQuery query) {
    	Result<UserSignNoticeVo> result = Result.success();
    	UserSignNoticeVo notice = userSignRecordDao.queryUserSignNoticeVo(query);
    	if(notice == null
    			|| notice.getGridCount() == 0) {
    		log.warn("咚咚通知：数据异常，不存在签到数据！请求参数：{}",JsonHelper.toJson(query));
    		result.toFail("咚咚通知：数据异常，不存在签到数据！");
    		return result;
    	}

    	List<UserSignNoticeWaveItemVo> waveItems = userSignRecordDao.queryUserSignNoticeWaveItems(query);
    	if(CollectionUtils.isEmpty(waveItems)) {
    		log.warn("咚咚通知：数据异常，不存在签到班次汇总数据！请求参数：{}",JsonHelper.toJson(query));
    		result.toFail("咚咚通知：数据异常，不存在签到班次汇总数据！");
    		return result;
    	}
    	List<UserSignNoticeJobItemVo> jobItems = userSignRecordDao.queryUserSignNoticeJobItems(query);
    	if(CollectionUtils.isEmpty(jobItems)) {
    		log.warn("咚咚通知：数据异常，不存在签到班次、工种汇总数据！请求参数：{}",JsonHelper.toJson(query));
    		result.toFail("咚咚通知：数据异常，不存在签到班次、工种汇总数据");
    		return result;
    	}
    	notice.setWaveItems(waveItems);

    	Map<Integer,UserSignNoticeWaveItemVo> waveMap = new HashMap<Integer,UserSignNoticeWaveItemVo>();
    	for(UserSignNoticeWaveItemVo waveData: waveItems) {
    		waveMap.put(waveData.getWaveCode(), waveData);
    	}
    	for(UserSignNoticeJobItemVo jobData: jobItems) {
    		UserSignNoticeWaveItemVo waveData = waveMap.get(jobData.getWaveCode());
    		if(waveData == null) {
    			log.warn("咚咚通知：数据异常，waveItems不存在{}班次数据！",jobData.getWaveCode());
        		result.toFail("咚咚通知：数据异常，不存在签到班次、工种汇总数据");
        		return result;
    		}
    		if(waveData.getJobItems() == null) {
    			waveData.setJobItems(new ArrayList<UserSignNoticeJobItemVo>());
    			waveData.setAttendNumSum(0);
    		}
    		waveData.getJobItems().add(jobData);
    		waveData.setAttendNumSum(waveData.getAttendNumSum() + jobData.getAttendNumSum());
			if(NumberHelper.gt0(waveData.getPlanAttendNumSum())) {
				double offValue = Math.abs((waveData.getAttendNumSum() - waveData.getPlanAttendNumSum()) * 1.0 / waveData.getPlanAttendNumSum());
				waveData.setDeviationPlanRate(RATE_FORMAT.format(offValue));
			}else {
				waveData.setDeviationPlanRate("--");
			}
    	}
    	result.setData(notice);
    	return result;
    }
	@Override
	public Result<Long> queryCount(UserSignRecordQuery query) {
		Result<Long> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		result.setData(userSignRecordDao.queryCount(query));
		return result;
	}
	@Override
	public Result<List<UserSignRecord>> queryListForExport(UserSignRecordQuery query) {
		Result<List<UserSignRecord>> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
	    List<UserSignRecord> dataList = userSignRecordDao.queryListForExport(query);
	    for (UserSignRecord tmp : dataList) {
	    	this.fillOtherInfo(tmp);
	    }
		result.setData(dataList);
		return result;
	}
	@Override
	public JdCResponse<UserSignRecordData> signInWithPosition(UserSignRequest signInRequest) {
		UserSignContext context = new UserSignContext();
		context.userSignRequest = signInRequest;
		return this.doSignIn(context);
	}
	public JdCResponse<UserSignRecordData> doSignIn(UserSignContext context) {
		UserSignRequest signInRequest = context.userSignRequest;
		JdCResponse<UserSignRecordData> result = checkAndFillUserInfo(signInRequest);
		if(!result.isSucceed()) {
			return result;
		}
		//校验岗位码,并获取网格信息
		JdCResponse<WorkStationGrid> gridResult = this.checkAndGetWorkStationGrid(signInRequest);
		if(!gridResult.isSucceed()) {
			result.toFail(gridResult.getMessage());
			return result;
		}
		WorkStationGrid gridInfo = gridResult.getData();
		//校验并组装签到数据
        UserSignRecord signInData = new UserSignRecord();
        result = this.checkAndFillSignInInfo(signInRequest,signInData,gridInfo);
        if(!result.isSucceed()) {
        	return result;
        }
        //开发者模式-不做签到、添加组员等
        if(Boolean.TRUE.equals(signInRequest.getDeveloperFlag())) {
        	this.doSignInForDeveloper(signInData);
        	result.setData(this.toUserSignRecordData(signInData));
        	result.toSucceed("开发者模式-不做签到、添加组员！");
        	return result;
        }
		UserSignRecordQuery lastSignRecordQuery = new UserSignRecordQuery();
		lastSignRecordQuery.setUserCode(signInRequest.getUserCode());
		//查询签到记录，先签退
        UserSignRecord lastSignRecord = this.queryLastUnSignOutRecord(lastSignRecordQuery);
        boolean needSignOut = false;
        boolean needSignIn = true;
        if(lastSignRecord != null) {
        	//上次签到岗位和本次相同，不需要签到、签退
            if(gridInfo.getBusinessKey() != null
            		&& gridInfo.getBusinessKey().equals(lastSignRecord.getRefGridKey())) {
            	needSignIn = false;
            	needSignOut = false;
            }else {
            	//不同岗位，需要签退并重新签到
            	needSignOut = true;
            	needSignIn = true;
            }
		}
        if(needSignOut) {
        	UserSignRecord signOutRequest = new UserSignRecord();
        	signOutRequest.setId(lastSignRecord.getId());
        	signOutRequest.setUpdateUser(signInRequest.getOperateUserCode());
        	signOutRequest.setUpdateUserName(signInRequest.getOperateUserName());
    		this.doSignOut(signOutRequest);

    		context.signOutData = this.toUserSignRecordData(lastSignRecord);
    		context.signOutData.setSignOutTime(signOutRequest.getSignOutTime());
        }
        if(needSignIn) {
            if(this.doSignIn(signInData)) {
            	result.setData(this.toUserSignRecordData(signInData));
            	if(needSignOut) {
            		result.toSucceed("签到成功，自动将上次签到数据签退！");
            	}else {
            		result.toSucceed("签到成功！");
            	}
            	context.signInData = result.getData();
            	context.signInFlag = true;
            }else {
            	result.toFail("签到失败！");
            }
        }else {
        	result.setData(this.toUserSignRecordData(lastSignRecord));
			// 设置网格信息
			setWorkGridKey(result.getData(), gridInfo);
        	result.toSucceed("已签到！");
        }
		return result;
	}
	@Override
	public JdCResponse<UserSignRecordData> signOutWithPosition(UserSignRequest signOutRequest) {
		UserSignContext context = new UserSignContext();
		context.userSignRequest = signOutRequest;
		return this.doSignOut(context);
	}
	public JdCResponse<UserSignRecordData> doSignOut(UserSignContext context) {
		UserSignRequest signOutRequest = context.userSignRequest;
		JdCResponse<UserSignRecordData> result = checkAndFillUserInfo(signOutRequest);
		if(!result.isSucceed()) {
			return result;
		}
		UserSignRecordData lastSignRecord = null;
		UserSignRecord signOutData = new UserSignRecord();
		if(signOutRequest.getRecordId() != null) {
			signOutData.setId(signOutRequest.getRecordId());
			lastSignRecord = queryUserSignRecordDataById(signOutRequest.getRecordId());
			if(lastSignRecord == null || lastSignRecord.getSignOutTime() != null) {
				result.toFail("签到数据无效|已签退！");
				return result;
			}
		}else {
			UserSignRecordQuery lastSignRecordQuery = new UserSignRecordQuery();
			lastSignRecordQuery.setUserCode(signOutRequest.getUserCode());
            lastSignRecord = this.toUserSignRecordData(queryLastUnSignOutRecord(lastSignRecordQuery));
            if(lastSignRecord == null) {
            	result.toFail("该用户未签到，无法签退！");
				return result;
			}
			signOutData.setId(lastSignRecord.getId());
		}
		//权限校验
		JdCResponse<Boolean> permissionCheckResult = checkHavePermission(lastSignRecord,signOutRequest.getOperateUserCode());
		if(!permissionCheckResult.isSucceed()) {
			result.toFail(permissionCheckResult.getMessage());
			return result;
		}
        signOutData.setUpdateUser(signOutRequest.getOperateUserCode());
        signOutData.setUpdateUserName(signOutRequest.getOperateUserName());
        if(this.doSignOut(signOutData)) {
        	result.setData(this.queryUserSignRecordDataById(signOutData.getId()));
        	result.toSucceed("签退成功！");
        	context.signOutData = result.getData();
    		context.signOutData.setSignOutTime(signOutData.getSignOutTime());
        }else {
        	result.toFail("签退失败！");
        }
		return result;
	}
	@Override
	public JdCResponse<UserSignRecordData> signAuto(UserSignRequest userSignRequest) {
		UserSignContext context = new UserSignContext();
		context.userSignRequest = userSignRequest;
		return this.doSignAuto(context);
	}
	public JdCResponse<UserSignRecordData> doSignAuto(UserSignContext context) {
		UserSignRequest userSignRequest = context.getUserSignRequest();
		JdCResponse<UserSignRecordData> result = checkAndFillUserInfo(userSignRequest);
		if(!result.isSucceed()) {
			return result;
		}
		//校验岗位码,并获取网格信息
		JdCResponse<WorkStationGrid> gridResult = this.checkAndGetWorkStationGrid(userSignRequest);
		if(!gridResult.isSucceed()) {
			result.toFail(gridResult.getMessage());
			return result;
		}

		WorkStationGrid gridInfo = gridResult.getData();
		//查询签到记录，自动签退
		UserSignRecordData lastSignRecord;
		if (BusinessUtil.isIdCardNo(userSignRequest.getUserCode()) && dmsConfigManager.getUccPropertyConfiguration().getUserSignIgnoreCaseSwitch()) {
			List<String> userCodeList = Arrays.asList(userSignRequest.getUserCode(), userSignRequest.getUserCode().toLowerCase());
			lastSignRecord = this.toUserSignRecordData(this.queryLastUnSignOutRecordIgnoreCase(userCodeList));
		} else {
			UserSignRecordQuery lastSignRecordQuery = new UserSignRecordQuery();
			lastSignRecordQuery.setUserCode(userSignRequest.getUserCode());
		 	lastSignRecord = this.toUserSignRecordData(this.queryLastUnSignOutRecord(lastSignRecordQuery));
		}
        boolean needSignIn = true;
        boolean needSignOut = false;
        boolean changeGrid = false;

        if(lastSignRecord != null) {
        	needSignOut = true;
        	//上次签到岗位和本次相同，不需要签到
            if(gridInfo.getBusinessKey() != null
            		&& gridInfo.getBusinessKey().equals(lastSignRecord.getRefGridKey())) {
            	needSignIn = false;
            }else {
            	changeGrid = true;
            }
		}

        UserSignRecord signInData = new UserSignRecord();
        //校验并组装签到数据
        if(needSignIn) {
            result = this.checkAndFillSignInInfo(userSignRequest,signInData,gridResult.getData());
            if(!result.isSucceed()) {
            	return result;
            }
        }
        if(needSignOut) {
        	//不切换岗位，签退需要校验权限
    		if(!changeGrid) {
            	//权限校验
        		JdCResponse<Boolean> permissionCheckResult = checkHavePermission(lastSignRecord,userSignRequest.getOperateUserCode());
        		if(!permissionCheckResult.isSucceed()) {
        			result.toFail(permissionCheckResult.getMessage());
        			return result;
        		}
    		}
        	UserSignRecord signOutData = new UserSignRecord();
        	signOutData.setId(lastSignRecord.getId());
        	signOutData.setUpdateUser(userSignRequest.getOperateUserCode());
        	signOutData.setUpdateUserName(userSignRequest.getOperateUserName());
            this.doSignOut(signOutData);
            //不需要签到，直接返回签退结果
            if(!needSignIn) {
            	result.setData(this.queryUserSignRecordDataById(signOutData.getId()));
				// 设置网格信息
				setWorkGridKey(result.getData(), gridInfo);
                result.toSucceed("签退成功！");
                context.signOutData = result.getData();
        		return result;
            }else {
            	context.signOutData = lastSignRecord;
            	context.signOutData.setSignOutTime(signOutData.getSignOutTime());
            }
        }

        if(this.doSignIn(signInData)) {
        	result.setData(this.toUserSignRecordData(signInData));
        	if(needSignOut) {
        		result.toSucceed("签到成功，自动将上次签到数据签退！");
        	}else {
        		result.toSucceed("签到成功！");
        	}
        	context.signInData = result.getData();
        	context.signInFlag = true;
        }else {
        	result.toFail("签到失败！");
        }
		return result;
	}

    /**
     * 设置用户签到记录数据的网格业务主键
     * @param userSignRecordData 用户签到记录数据
     * @param gridInfo 网格信息
     */
	private void setWorkGridKey(UserSignRecordData userSignRecordData, WorkStationGrid gridInfo) {
		if (userSignRecordData != null) {
			userSignRecordData.setRefWorkGridKey(gridInfo.getRefWorkGridKey());
		}
	}

	/**
	 * 签到、签退设置用户信息
	 * @param signRequest
	 * @return
	 */
	private JdCResponse<UserSignRecordData> checkAndFillUserInfo(UserSignRequest signRequest){
		JdCResponse<UserSignRecordData> result = new JdCResponse<>();
		result.toSucceed();
		String scanUserCode = signRequest.getScanUserCode();
		if(StringHelper.isNotEmpty(scanUserCode)) {
			if(!BusinessUtil.isScanUserCode(scanUserCode)){
				result.toFail("请扫描正确的人员码！");
				return result;
			}
			String userCode = BusinessUtil.getUserCodeFromScanUserCode(scanUserCode);
			if (userCode != null) userCode = userCode.trim();
			// 身份证字母转大写
			if (BusinessUtil.isIdCardNo(userCode)) {
				userCode = userCode.toUpperCase();
			}
			signRequest.setJobCode(BusinessUtil.getJobCodeFromScanUserCode(scanUserCode));
			signRequest.setUserCode(userCode);
		}else if(StringHelper.isEmpty(signRequest.getUserCode())) {
			result.toFail("用户编码不能为空！");
			return result;
		}
		if (signRequest.getUserCode().contains("$")) {
			result.toFail("用户编码不能包含特殊字符");
			return result;
		}
		if(StringUtils.isBlank(signRequest.getOperateUserCode())
				|| StringUtils.isBlank(signRequest.getOperateUserName())) {
			result.toFail(MSG_EMPTY_OPERATE);
			return result;
		}
		return result;
	}
	private JdCResponse<WorkStationGrid> checkAndGetWorkStationGrid(UserSignRequest signInRequest){
		JdCResponse<WorkStationGrid> result = new JdCResponse<>();
		result.toSucceed();
		if(signInRequest == null
				|| StringHelper.isEmpty(signInRequest.getPositionCode())) {
			result.toFail("岗位码不能为空！");
			return result;
		}
		log.info("checkAndGetWorkStationGrid - 获取基础服务数据");
		com.jdl.basic.common.utils.Result<com.jdl.basic.api.domain.position.PositionDetailRecord> positionData = positionManager.queryOneByPositionCode(signInRequest.getPositionCode());
		if(positionData == null
				|| positionData.getData() == null) {
			result.toFail("岗位码无效，联系【作业流程组】小哥维护岗位码！");
			return result;
		}
		String gridKey = positionData.getData().getRefGridKey();
		com.jdl.basic.api.domain.workStation.WorkStationGridQuery  workStationGridCheckQuery = new com.jdl.basic.api.domain.workStation.WorkStationGridQuery ();
		workStationGridCheckQuery.setBusinessKey(gridKey);
		com.jdl.basic.common.utils.Result<com.jdl.basic.api.domain.workStation.WorkStationGrid> workStationGridData = workStationGridManager.queryByGridKey(workStationGridCheckQuery);
		// todo 增加结果的调用是否成功判断
		if(workStationGridData == null
				|| workStationGridData.getData() == null) {
			result.toFail("岗位码对应的网格信息不存在，请先维护场地网格信息！");
			return result;
		}
		com.jdl.basic.api.domain.workStation.WorkStationGrid data = workStationGridData.getData();
		WorkStationGrid resultData = new WorkStationGrid();
		BeanUtils.copyProperties(data,resultData);
		result.setData(resultData);
		return result;
	}
	private JdCResponse<UserSignRecordData> checkAndFillSignInInfo(UserSignRequest signInRequest,UserSignRecord signInData, WorkStationGrid gridInfo){
		JdCResponse<UserSignRecordData> result = new JdCResponse<>();
		result.toSucceed();

		String gridKey = gridInfo.getBusinessKey();
		String stationKey = gridInfo.getRefStationKey();
		signInData.setJobCode(signInRequest.getJobCode());
		signInData.setUserCode(signInRequest.getUserCode());
		signInData.setCreateUser(signInRequest.getOperateUserCode());
		signInData.setCreateUserName(signInRequest.getOperateUserName());
		signInData.setSiteCode(gridInfo.getSiteCode());
		signInData.setOrgCode(gridInfo.getOrgCode());
		signInData.setRefGridKey(gridKey);
		signInData.setRefStationKey(stationKey);
		signInData.setRefWorkGridKey(gridInfo.getRefWorkGridKey());
		//身份证拍照签到的直接设置姓名，erp签到的需要查基础资料
		signInData.setUserName(signInRequest.getUserName());
		signInData.setModeType(signInRequest.getModeType());
		// 计算班次
		Integer waveCode = calculateWave(signInRequest);
		signInData.setWaveCode(waveCode);
		signInData.setWaveName(WaveTypeEnum.getNameByCode(waveCode));
		signInData.setRefWorkGridKey(gridInfo.getRefWorkGridKey());
		// 计算班次 新逻辑
		calculateWaveNew(signInRequest, signInData);
		signInData.setRefPlanKey(queryPlanKey(signInData));
		setWarZoneInfo(signInData);

		Integer jobCode = signInData.getJobCode();
		String userCode = signInData.getUserCode();
		boolean isCarId = BusinessUtil.isIdCardNo(userCode);

		if(isCarId){
			String  msg=checkAttendanceBlackList(result,userCode);
			if(StringUtils.isNotBlank(msg)){
				return result;
			}
		}
		String checkMsg = checkJobCodeSignIn(gridInfo, jobCode);
		log.info("校验签到工种checkBeforeSignIn checkMsg-{}",checkMsg);
		if(StringUtils.isNotBlank(checkMsg)){
			result.toFail(checkMsg);
			return result;
		}

		if(JobTypeEnum.JOBTYPE1.getCode().equals(jobCode)
				||JobTypeEnum.JOBTYPE2.getCode().equals(jobCode)
				||JobTypeEnum.JOBTYPE7.getCode().equals(jobCode)) {
			//正式工设置erp对应的名称
			BaseStaffSiteOrgDto userInfo = baseMajorManager.getBaseStaffIgnoreIsResignByErp(signInData.getUserCode());
			boolean isEffectErp = false;
			if(userInfo != null
					&& Constants.FLAG_USER_Is_Resign.equals(userInfo.getIsResign())) {
				isEffectErp = true;
				if (userInfo.getStaffNo() != null) {
					signInData.setUserId(userInfo.getStaffNo());
				}
			}
			if(!isEffectErp) {
				if(JobTypeEnum.JOBTYPE1.getCode().equals(jobCode)
							||JobTypeEnum.JOBTYPE2.getCode().equals(jobCode)
							||JobTypeEnum.JOBTYPE7.getCode().equals(jobCode)) {
					result.toFail("签到失败，ERP在中台基础资料中不存在！");
					return result;
				}
			}
			//设置用户名称
			if(isEffectErp
					&& userInfo.getStaffName() != null) {
				signInData.setUserName(userInfo.getStaffName());
			}
		}else if(!JobTypeEnum.JOBTYPE6.getCode().equals(jobCode) && !isCarId){
			result.toFail("签到失败，无效的身份证号！");
			return result;
		}
		if(isCarId){
			final BaseStaff baseStaff = baseMajorManager.checkIDCardNoExists(userCode);
			if (baseStaff != null) {
				signInData.setUserId(baseStaff.getStaffNo());
			}
		}
		return result;
	}

	/**
	 * 校验工种是否允许签到
	 * @param workStationGrid
	 * @param jobCode
	 * @return
	 */
	private String  checkJobCodeSignIn(WorkStationGrid workStationGrid,Integer jobCode){
		log.info("checkJobCodeSignIn,当前登录的工种:{}", jobCode);
		//添加开关 以便于上线后没维护工种类型 都进行卡控
		if(!dmsConfigManager.getPropertyConfig().isJobTypeLimitSwitch() || JobTypeEnum.JOBTYPE8.getCode().equals(jobCode)){
			log.warn("网格工种限制功能开关关闭!");
			return "";
		}

		String ownerUserErp = workStationGrid.getOwnerUserErp();
		String gridName=workStationGrid.getGridName();

		//获取当前网格的工种信息
		List<WorkStationJobTypeDto> jobTypes = workStationManager.queryWorkStationJobTypeBybusinessKey(workStationGrid.getRefStationKey());
		log.info("checkJobCodeSignIn -获取网格工种信息 入参-{}，出参-{}",workStationGrid.getRefStationKey(), JSON.toJSONString(jobTypes));
		//判断网关工种维护关系之前,再判断维护的工种是否处于生效的状态
		JyJobType query = buildJyJobType(jobCode);
		List<JyJobType> jyJobTypeList = jyJobTypeManager.getListByCondition(query);
		if(log.isInfoEnabled()){
			log.info("checkJobCodeSignIn -获取所有工种信息，出参-{}", JSON.toJSONString(jyJobTypeList));
		}
		if (CollectionUtils.isNotEmpty(jyJobTypeList)){
			JyJobType jyJobType = jyJobTypeList.get(0);
			if (Objects.isNull(jyJobType)){
				return String.format(HintCodeConstants.JY_SIGN_IN_JOB_TYPE_MSG,jobCode,ownerUserErp);
			}

			//网格工种没维护或者维护的工种中没匹配到传入的工种都返回提示
			String jobTypeName = jyJobType.getName();
			if(org.apache.commons.collections.CollectionUtils.isEmpty(jobTypes)){
				return String.format(HintCodeConstants.JY_SIGN_IN_JOB_TYPE_TIP_MSG,gridName,jobTypeName,ownerUserErp);
			}

			boolean flag = false;
			for (int i = 0; i < jobTypes.size(); i++) {
				if(Objects.nonNull(jyJobType) && Objects.equals(jobCode,jobTypes.get(i).getJobCode())){
					flag =true;
					break;
				}
			}
			if(!flag){
				return String.format(HintCodeConstants.JY_SIGN_IN_JOB_TYPE_TIP_MSG,gridName,jobTypeName,ownerUserErp);
			}
		}
		return "";
	}


    /**
     * 构建JyJobType对象
     * @param jobCode 工作代码
     * @return JyJobType对象
     */
	private JyJobType buildJyJobType(Integer jobCode) {
		JyJobType jyJobType = new JyJobType();
		jyJobType.setCode(jobCode);
		jyJobType.setStatus(Constants.NUMBER_ONE);
		return jyJobType;
	}

	private void setWarZoneInfo(UserSignRecord signInData) {
		if(signInData.getSiteCode() == null){
			return;
		}
		BaseSite baseSite = baseMajorManager.getSiteBySiteCode(signInData.getSiteCode());
		signInData.setOrgCode(baseSite == null ? null : baseSite.getOrgId());
		signInData.setOrgName(baseSite == null ? null : baseSite.getOrgName());
		signInData.setProvinceAgencyCode(baseSite == null ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyCode());
		signInData.setProvinceAgencyName(baseSite == null ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyName());
		signInData.setWarZoneCode(baseSite == null ? null : baseSite.getProvinceCompanyCode());
		signInData.setWarZoneName(baseSite == null ? null : baseSite.getProvinceCompanyName());
	}

	private String queryPlanKey(UserSignRecord signInData) {
		//查询设置计划信息
		log.info("queryPlanKey -获取基础服务数据");
		com.jdl.basic.api.domain.workStation.WorkStationAttendPlan  workStationAttendPlanQuery = new com.jdl.basic.api.domain.workStation.WorkStationAttendPlan ();
		workStationAttendPlanQuery.setRefGridKey(signInData.getRefGridKey());
		workStationAttendPlanQuery.setWaveCode(signInData.getWaveCode());
		com.jdl.basic.common.utils.Result<WorkStationAttendPlan> planData = workStationAttendPlanManager.queryByBusinessKeys(workStationAttendPlanQuery);
		if(planData != null
				&& planData.getData() != null) {
			return planData.getData().getBusinessKey();
		}
		return null;
	}

	/**
	 * 计算班次
	 * 	fix：
	 * 		1、0<h<=6: 如果前一天18-24未签到过则为白班，签到过则为晚班
	 * 		2、6<h<=12: 如果当天0-6未签到过则为白班，签到过则为晚班
	 * 		3、12<h<=18: 如果当天6-12未签到过则为中班，签到过则为白班
	 * 		4、18<h<=24: 如果当天12-18未签到过则为晚班，签到过则为中班
	 * @param signInRequest
	 * @return
	 */
	private Integer calculateWave(UserSignRequest signInRequest) {
		long oldStartTime = System.currentTimeMillis();
		// 当前时间
		Date currentDate = new Date();
		long currentTime = currentDate.getTime();
		// 当天零点、6、12、18、24
		long currentZero = DateHelper.getZero(currentDate);
		long currentZeroAdd6 = DateHelper.add(new Date(currentZero), Calendar.HOUR_OF_DAY, 6).getTime();
		long currentZeroAdd12 = DateHelper.add(new Date(currentZero), Calendar.HOUR_OF_DAY, 12).getTime();
		long currentZeroAdd18 = DateHelper.add(new Date(currentZero), Calendar.HOUR_OF_DAY, 18).getTime();
		long currentZeroAdd24 = DateHelper.add(new Date(currentZero), Calendar.HOUR_OF_DAY, 24).getTime();
		// 昨天零点、18、24
		long yesterdayZero = DateHelper.getZero(DateHelper.addDate(currentDate, -1));
		long yesterdayZeroAdd18 = DateHelper.add(new Date(yesterdayZero), Calendar.HOUR_OF_DAY, 18).getTime();
		// 按规则判断
		UserSignQueryRequest query = new UserSignQueryRequest();
		query.setSiteCode(signInRequest.getSiteCode());
		query.setUserCode(signInRequest.getUserCode());
		if(currentZero < currentTime && currentTime <= currentZeroAdd6){
			query.setSignInTimeStart(new Date(yesterdayZeroAdd18));
			query.setSignInTimeEnd(new Date(currentZero));
			if(userSignRecordDao.queryLastUserSignRecordData(query) == null){
				return WaveTypeEnum.DAY.getCode();
			}
			return WaveTypeEnum.NIGHT.getCode();
		}else if (currentZeroAdd6 < currentTime && currentTime <= currentZeroAdd12){
			query.setSignInTimeStart(new Date(currentZero));
			query.setSignInTimeEnd(new Date(currentZeroAdd6));
			if(userSignRecordDao.queryLastUserSignRecordData(query) == null){
				return WaveTypeEnum.DAY.getCode();
			}
			return WaveTypeEnum.NIGHT.getCode();
		}else if(currentZeroAdd12 < currentTime && currentTime <= currentZeroAdd18){
			query.setSignInTimeStart(new Date(currentZeroAdd6));
			query.setSignInTimeEnd(new Date(currentZeroAdd12));
			if(userSignRecordDao.queryLastUserSignRecordData(query) == null){
				return WaveTypeEnum.MIDDLE.getCode();
			}
			return WaveTypeEnum.DAY.getCode();
		}else if(currentZeroAdd18 < currentTime && currentTime <= currentZeroAdd24){
			query.setSignInTimeStart(new Date(currentZeroAdd12));
			query.setSignInTimeEnd(new Date(currentZeroAdd18));
			if(userSignRecordDao.queryLastUserSignRecordData(query) == null){
				return WaveTypeEnum.NIGHT.getCode();
			}
			return WaveTypeEnum.MIDDLE.getCode();
		}
		log.info("calculateWave|耗时={}ms", System.currentTimeMillis() - oldStartTime);
		return null;
	}

	private void calculateWaveNew(UserSignRequest request, UserSignRecord signInData) {
		long newStartTime = System.currentTimeMillis();
		// 针对自有员工（正式工或派遣工）
		if (JobTypeEnum.JOBTYPE1.getCode().equals(request.getJobCode()) || JobTypeEnum.JOBTYPE2.getCode().equals(request.getJobCode())) {
			assembleRegularEmployeesWaveCode(request, signInData);
		} else {
			// 针对非自有员工
			assembleInformalEmployeesWaveCode(request, signInData);
		}
		log.info("calculateWaveNew|耗时={}ms", System.currentTimeMillis() - newStartTime);
	}

	private Date getMinStartDate(Date minStartDate, Date startDate, Map<Integer, String> minStartTimeMap, String startTime, Integer waveType) {
		if (minStartDate == null) {
			minStartTimeMap.put(waveType, startTime);
			return startDate;
		} else {
			if (minStartDate.after(startDate)) {
				minStartTimeMap.put(waveType, startTime);
				return startDate;
			}
		}
		return minStartDate;
	}

	private Date getMaxStartDate(Date maxStartDate, Date startDate) {
		if (maxStartDate == null) {
			return startDate;
		} else {
			if (maxStartDate.before(startDate)) {
				return startDate;
			}
		}
		return maxStartDate;
	}

	private Date getMaxEndDate(Date maxEndDate, Date endDate, Map<Integer, String> maxEndTimeMap, String endTime, Integer waveType) {
		if (maxEndDate == null) {
			maxEndTimeMap.put(waveType, endTime);
			return endDate;
		} else {
			if (maxEndDate.before(endDate)) {
				maxEndTimeMap.put(waveType, endTime);
				return endDate;
			}
		}
		return maxEndDate;
	}



	private Date getSpecialDateByStr(String timeStr, Integer addDay) {
		LocalTime localTime = LocalTime.parse(timeStr);
		int hour = localTime.getHour();
		int minute = localTime.getMinute();
		Calendar calendar = Calendar.getInstance();
		if (addDay != null) {
			calendar.add(Calendar.DATE, addDay);
		}
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, Constants.CONSTANT_NUMBER_ZERO);
		return calendar.getTime();
	}

	private boolean isNatureUserCountLessThanOrEqualToZero(List<ScheduleAggsDto> scheduleAggsDtoList) {
		for (ScheduleAggsDto scheduleAggsDto : scheduleAggsDtoList) {
			Integer natureUserCount = scheduleAggsDto.getNatureUserCount();
			if (natureUserCount != null && natureUserCount > Constants.NUMBER_ZERO) {
				return false;
			}
		}
		return true;
	}



	private void assembleInformalEmployeesWaveCode(UserSignRequest request, UserSignRecord signInData) {
		Date currentDate = new Date();
		// 查询网格下该工种的排班计划
		DataScheduleNatureDto natureDto = new DataScheduleNatureDto();
		natureDto.setNature(String.valueOf(request.getJobCode()));
		natureDto.setScheduleDate(DateHelper.getSpecialDateOfyyMMdd2(currentDate.getTime()));
		natureDto.setWorkGridKey(signInData.getRefWorkGridKey());
		List<ScheduleAggsDto> scheduleAggsDtoList = workGridScheduleManager.findListByGridKeyAndNature(natureDto);
		// 查看该网格当天是否排班了对应的工种（工种数量>0则视为排班），若未排班，则展示未排班
		if (CollectionUtils.isEmpty(scheduleAggsDtoList)) {
			log.warn("assembleInformalEmployeesWaveCode|根据指定条件从flat查询排班计划工种人数统计返回空:request={},signInData={}", JsonHelper.toJson(request), JsonHelper.toJson(signInData));
			return;
		}
		if (log.isInfoEnabled()) {
			log.info("assembleInformalEmployeesWaveCode|网格下该工种人数统计:request={},signInData={},scheduleAggsDtoList={}", JsonHelper.toJson(request), JsonHelper.toJson(signInData), JsonHelper.toJson(scheduleAggsDtoList));
		}
		// 如果记录不为空，但是每条记录上的工种人数都不大于0，则视为未排班
		if (isNatureUserCountLessThanOrEqualToZero(scheduleAggsDtoList)) {
			log.warn("assembleInformalEmployeesWaveCode|根据指定条件从flat查询排班计划工种人数不满足数量大于0,:request={},signInData={}", JsonHelper.toJson(request), JsonHelper.toJson(signInData));
			return;
		}

		// 当天0点
		long currentZero = DateHelper.getZero(currentDate);
		// 当天24点
		long currentZeroAdd24 = DateHelper.add(new Date(currentZero), Calendar.HOUR_OF_DAY, Constants.NUMBER_TWENTY_FOUR).getTime();
		// 首次签到时间
		Date firstSignTime;
		// 判断当前打卡是否是当天首次打卡
		UserSignQueryRequest userSignQueryRequest = new UserSignQueryRequest();
		userSignQueryRequest.setSignInTimeStart(new Date(currentZero));
		userSignQueryRequest.setSignInTimeEnd(new Date(currentZeroAdd24));
		userSignQueryRequest.setSiteCode(signInData.getSiteCode());
		userSignQueryRequest.setUserCode(signInData.getUserCode());
		UserSignRecordData userSignRecordData = userSignRecordDao.queryFirstUserSignRecordData(userSignQueryRequest);
		if (userSignRecordData == null) {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|当前签到是当天首次签到:request={},userSignQueryRequest={}", JsonHelper.toJson(request), JsonHelper.toJson(userSignQueryRequest));
			}
			// 若是首次打卡，取当前时间
			firstSignTime = currentDate;
		} else {
			// 若非首次打卡，取首次打卡时间
			firstSignTime = userSignRecordData.getSignInTime();
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|当前签到不是当天首次签到:request={},firstSignTime={},userSignQueryRequest={}", JsonHelper.toJson(request), firstSignTime, JsonHelper.toJson(userSignQueryRequest));
			}
		}
		// 查询网格下所有班次
		WorkGridScheduleRequest workGridScheduleRequest = new WorkGridScheduleRequest();
		workGridScheduleRequest.setWorkGridKey(signInData.getRefWorkGridKey());
		List<WorkGridSchedule> scheduleList = workGridScheduleManager.queryByWorkGridKey(workGridScheduleRequest);
		if (CollectionUtils.isEmpty(scheduleList)) {
			log.warn("assembleInformalEmployeesWaveCode|根据网格key从basic查询网格下所有班次返回空:request={},signInData={}", JsonHelper.toJson(request), JsonHelper.toJson(signInData));
			return;
		}
		if (log.isInfoEnabled()) {
			log.info("assembleInformalEmployeesWaveCode|网格下所有班次:request={},signInData={},scheduleList={}", JsonHelper.toJson(request), JsonHelper.toJson(signInData), JsonHelper.toJson(scheduleList));
		}
		// 白班最小起始时间
		Date minDayStartDate = null;
		// 白班最大起始时间
		Date maxDayStartDate = null;
		// 白班最大结束时间
		Date maxDayEndDate = null;
		// 中班最小起始时间
		Date minMiddleStartDate = null;
		// 中班最大起始时间
		Date maxMiddleStartDate = null;
		// 中班最大结束时间
		Date maxMiddleEndDate = null;
		// 晚班最小起始时间
		Date minNightStartDate = null;
		// 晚班最大起始时间
		Date maxNightStartDate = null;
		// 晚班最大结束时间
		Date maxNightEndDate = null;
		// <班次，最大结束时间>
		Map<Integer, String> maxEndTimeMap = new HashMap<>(Constants.NUMBER_THREE);
		// <班次，最小开始时间>
		Map<Integer, String> minStartTimeMap = new HashMap<>(Constants.NUMBER_THREE);

		for (WorkGridSchedule workGridSchedule : scheduleList) {
			// 班次时分形式 HH:mm
			String startTime = workGridSchedule.getStartTime();
			String endTime = workGridSchedule.getEndTime();
			// 如果修改过班次时间，但是属于非立即生效场景，需要使用原来的时间
			if (StringUtils.isNotBlank(workGridSchedule.getOldStartTime())) {
				startTime = workGridSchedule.getOldStartTime();
				endTime = workGridSchedule.getOldEndTime();
			}
			// 班次日期形式 yyyy-MM-dd HH:mm:ss
			Date startDate = getSpecialDateByStr(startTime, null);
			Date endDate;
			// 如果结束日期代表第二天的时间点，则转换时需要加一天
			if (LocalTime.parse(startTime).isAfter(LocalTime.parse(endTime))) {
				endDate = getSpecialDateByStr(endTime, Constants.NUMBER_ONE);
			} else {
				endDate = getSpecialDateByStr(endTime, null);
			}
			// 班次类型
			Integer scheduleType = workGridSchedule.getScheduleType();
			// 如果是白班
			if (WaveTypeEnum.DAY.getCode().equals(scheduleType)) {
				minDayStartDate = getMinStartDate(minDayStartDate, startDate, minStartTimeMap, startTime, WaveTypeEnum.DAY.getCode());
				maxDayStartDate = getMaxStartDate(maxDayStartDate, startDate);
				maxDayEndDate = getMaxEndDate(maxDayEndDate, endDate, maxEndTimeMap, endTime, WaveTypeEnum.DAY.getCode());
				// 如果是中班
			} else if (WaveTypeEnum.MIDDLE.getCode().equals(scheduleType)) {
				minMiddleStartDate = getMinStartDate(minMiddleStartDate, startDate, minStartTimeMap, startTime, WaveTypeEnum.MIDDLE.getCode());
				maxMiddleStartDate = getMaxStartDate(maxMiddleStartDate, startDate);
				maxMiddleEndDate = getMaxEndDate(maxMiddleEndDate, endDate, maxEndTimeMap, endTime, WaveTypeEnum.MIDDLE.getCode());
				// 如果是晚班
			} else if (WaveTypeEnum.NIGHT.getCode().equals(scheduleType)) {
				minNightStartDate = getMinStartDate(minNightStartDate, startDate, minStartTimeMap, startTime, WaveTypeEnum.NIGHT.getCode());
				maxNightStartDate = getMaxStartDate(maxNightStartDate, startDate);
				maxNightEndDate = getMaxEndDate(maxNightEndDate, endDate, maxEndTimeMap, endTime, WaveTypeEnum.NIGHT.getCode());
			}
		}
		if (log.isInfoEnabled()) {
			log.info("assembleInformalEmployeesWaveCode|各个班次最小起始时间与最大起始时间:request={},minDayStartDate={},maxDayStartDate={},minMiddleStartDate={},maxMiddleStartDate={},minNightStartDate={},maxNightStartDate={}",
					JsonHelper.toJson(request), minDayStartDate, maxDayStartDate, minMiddleStartDate, maxMiddleStartDate, minNightStartDate, maxNightStartDate);
			log.info("assembleInformalEmployeesWaveCode|各个班次最小起始时间与最大结束时间:request={},minStartTimeMap={},maxEndTimeMap={}", JsonHelper.toJson(request), JsonHelper.toJson(minStartTimeMap), JsonHelper.toJson(maxEndTimeMap));
		}
		boolean isBetweenDayFlag = false;
		Date minDayStartDateBeforeOneHour = null;
		// 判断首次签到时间是否在白班的最早开始时间(减1小时)至白班最晚开始时间内
		if (minDayStartDate != null && maxDayStartDate != null) {
			minDayStartDateBeforeOneHour = DateHelper.addHours(minDayStartDate, Constants.NEGATIVE_NUMBER_ONE);
			if (firstSignTime.getTime() <= maxDayStartDate.getTime() && firstSignTime.getTime() >= minDayStartDateBeforeOneHour.getTime()) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|首次签到时间在白班的最早开始时间(减1小时)至白班最晚开始时间内:request={},firstSignTime={},minDayStartDate={},maxDayStartDate={}",
							JsonHelper.toJson(request), firstSignTime, minDayStartDate, maxDayStartDate);
				}
				isBetweenDayFlag = true;
			}
		}
		boolean isBetweenMiddleFlag = false;
		Date minMiddleStartDateBeforeOneHour = null;
		// 判断首次签到时间是否在中班的最早开始时间(减1小时)至中班最晚开始时间内
		if (minMiddleStartDate != null && maxMiddleStartDate != null) {
			minMiddleStartDateBeforeOneHour = DateHelper.addHours(minMiddleStartDate, Constants.NEGATIVE_NUMBER_ONE);
			if (firstSignTime.getTime() <= maxMiddleStartDate.getTime() && firstSignTime.getTime() >= minMiddleStartDateBeforeOneHour.getTime()) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|首次签到时间在中班的最早开始时间(减1小时)至中班最晚开始时间内:request={},firstSignTime={},minDayStartDate={},maxDayStartDate={}",
							JsonHelper.toJson(request), firstSignTime, minMiddleStartDate, maxMiddleStartDate);
				}
				isBetweenMiddleFlag = true;
			}
		}
		boolean isBetweenNightFlag = false;
		Date minNightStartDateBeforeOneHour = null;
		// 判断首次签到时间是否在晚班的最早开始时间(减1小时)至晚班最晚开始时间内
		if (minNightStartDate != null && maxNightStartDate != null) {
			minNightStartDateBeforeOneHour = DateHelper.addHours(minNightStartDate, Constants.NEGATIVE_NUMBER_ONE);
			if (firstSignTime.getTime() <= maxNightStartDate.getTime() && firstSignTime.getTime() >= minNightStartDateBeforeOneHour.getTime()) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|首次签到时间在晚班的最早开始时间(减1小时)至晚班最晚开始时间内:request={},firstSignTime={},minDayStartDate={},maxDayStartDate={}",
							JsonHelper.toJson(request), firstSignTime, minNightStartDate, maxNightStartDate);
				}
				isBetweenNightFlag = true;
			}
		}
		// 如果只有白班符合
		if (isBetweenDayFlag && !isBetweenMiddleFlag && !isBetweenNightFlag) {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|只有白班符合:request={},firstSignTime={}", JsonHelper.toJson(request), firstSignTime);
			}
			signInData.setWaveCodeNew(String.valueOf(WaveTypeNewEnum.DAY.getCode()));
			String waveTime = minStartTimeMap.get(WaveTypeEnum.DAY.getCode()) + Constants.SEPARATOR_TILDE + maxEndTimeMap.get(WaveTypeEnum.DAY.getCode());
			signInData.setWaveTime(waveTime);
            // 如果只有中班符合
		} else if (!isBetweenDayFlag && isBetweenMiddleFlag && !isBetweenNightFlag) {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|只有中班符合:request={},firstSignTime={}", JsonHelper.toJson(request), firstSignTime);
			}
			signInData.setWaveCodeNew(String.valueOf(WaveTypeNewEnum.MIDDLE.getCode()));
			String waveTime = minStartTimeMap.get(WaveTypeEnum.MIDDLE.getCode()) + Constants.SEPARATOR_TILDE + maxEndTimeMap.get(WaveTypeEnum.MIDDLE.getCode());
			signInData.setWaveTime(waveTime);
            // 如果只有晚班符合
		} else if (!isBetweenDayFlag && !isBetweenMiddleFlag && isBetweenNightFlag) {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|只有晚班符合:request={},firstSignTime={}", JsonHelper.toJson(request), firstSignTime);
			}
			signInData.setWaveCodeNew(String.valueOf(WaveTypeNewEnum.NIGHT.getCode()));
			String waveTime = minStartTimeMap.get(WaveTypeEnum.NIGHT.getCode()) + Constants.SEPARATOR_TILDE + maxEndTimeMap.get(WaveTypeEnum.NIGHT.getCode());
			signInData.setWaveTime(waveTime);
            // 如果白班和中班同时符合
		} else if (isBetweenDayFlag && isBetweenMiddleFlag && !isBetweenNightFlag) {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|白班和中班同时符合:request={},firstSignTime={}", JsonHelper.toJson(request), firstSignTime);
			}
			long minDayDifference = firstSignTime.getTime() - minDayStartDateBeforeOneHour.getTime();
			long minMiddleDifference = firstSignTime.getTime() - minMiddleStartDateBeforeOneHour.getTime();
			if (minDayDifference < minMiddleDifference) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|白班和中班同时符合,首次签到时间距离白班最小起始时间更接近:request={},firstSignTime={},minDayDifference={},minMiddleDifference={}",
							JsonHelper.toJson(request), firstSignTime, minDayDifference, minMiddleDifference);
				}
				signInData.setWaveCodeNew(String.valueOf(WaveTypeNewEnum.DAY.getCode()));
				String waveTime = minStartTimeMap.get(WaveTypeEnum.DAY.getCode()) + Constants.SEPARATOR_TILDE + maxEndTimeMap.get(WaveTypeEnum.DAY.getCode());
				signInData.setWaveTime(waveTime);
				return;
			}
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|白班和中班同时符合,首次签到时间距离中班最小起始时间更接近:request={},firstSignTime={},minDayDifference={},minMiddleDifference={}",
						JsonHelper.toJson(request), firstSignTime, minDayDifference, minMiddleDifference);
			}
			signInData.setWaveCodeNew(String.valueOf(WaveTypeNewEnum.MIDDLE.getCode()));
			String waveTime = minStartTimeMap.get(WaveTypeEnum.MIDDLE.getCode()) + Constants.SEPARATOR_TILDE + maxEndTimeMap.get(WaveTypeEnum.MIDDLE.getCode());
			signInData.setWaveTime(waveTime);
            // 如果白班和晚班同时符合
		} else if (isBetweenDayFlag && !isBetweenMiddleFlag && isBetweenNightFlag) {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|白班和晚班同时符合:request={},firstSignTime={}", JsonHelper.toJson(request), firstSignTime);
			}
			long minDayDifference = firstSignTime.getTime() - minDayStartDateBeforeOneHour.getTime();
			long minNightDifference = firstSignTime.getTime() - minNightStartDateBeforeOneHour.getTime();
			if (minDayDifference < minNightDifference) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|白班和晚班同时符合,首次签到时间距离白班最小起始时间更接近:request={},firstSignTime={},minDayDifference={},minMiddleDifference={}",
							JsonHelper.toJson(request), firstSignTime, minDayDifference, minNightDifference);
				}
				signInData.setWaveCodeNew(String.valueOf(WaveTypeNewEnum.DAY.getCode()));
				String waveTime = minStartTimeMap.get(WaveTypeEnum.DAY.getCode()) + Constants.SEPARATOR_TILDE + maxEndTimeMap.get(WaveTypeEnum.DAY.getCode());
				signInData.setWaveTime(waveTime);
				return;
			}
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|白班和晚班同时符合,首次签到时间距离晚班最小起始时间更接近:request={},firstSignTime={},minDayDifference={},minMiddleDifference={}",
						JsonHelper.toJson(request), firstSignTime, minDayDifference, minNightDifference);
			}
			signInData.setWaveCodeNew(String.valueOf(WaveTypeNewEnum.NIGHT.getCode()));
			String waveTime = minStartTimeMap.get(WaveTypeEnum.NIGHT.getCode()) + Constants.SEPARATOR_TILDE + maxEndTimeMap.get(WaveTypeEnum.NIGHT.getCode());
			signInData.setWaveTime(waveTime);
            // 如果中班和晚班同时符合
		} else if (!isBetweenDayFlag && isBetweenMiddleFlag && isBetweenNightFlag) {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|中班和晚班同时符合:request={},firstSignTime={}", JsonHelper.toJson(request), firstSignTime);
			}
			long minMiddleDifference = firstSignTime.getTime() - minMiddleStartDateBeforeOneHour.getTime();
			long minNightDifference = firstSignTime.getTime() - minNightStartDateBeforeOneHour.getTime();
			if (minMiddleDifference < minNightDifference) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|中班和晚班同时符合,首次签到时间距离中班最小起始时间更接近:request={},firstSignTime={},minDayDifference={},minMiddleDifference={}",
							JsonHelper.toJson(request), firstSignTime, minMiddleDifference, minNightDifference);
				}
				signInData.setWaveCodeNew(String.valueOf(WaveTypeNewEnum.MIDDLE.getCode()));
				String waveTime = minStartTimeMap.get(WaveTypeEnum.MIDDLE.getCode()) + Constants.SEPARATOR_TILDE + maxEndTimeMap.get(WaveTypeEnum.MIDDLE.getCode());
				signInData.setWaveTime(waveTime);
				return;
			}
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|中班和晚班同时符合,首次签到时间距离晚班最小起始时间更接近:request={},firstSignTime={},minDayDifference={},minMiddleDifference={}",
						JsonHelper.toJson(request), firstSignTime, minMiddleDifference, minNightDifference);
			}
			signInData.setWaveCodeNew(String.valueOf(WaveTypeNewEnum.NIGHT.getCode()));
			String waveTime = minStartTimeMap.get(WaveTypeEnum.NIGHT.getCode()) + Constants.SEPARATOR_TILDE + maxEndTimeMap.get(WaveTypeEnum.NIGHT.getCode());
			signInData.setWaveTime(waveTime);
            // 如果白班、中班和晚班同时符合
		} else if (isBetweenDayFlag && isBetweenMiddleFlag && isBetweenNightFlag) {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|白班以及中班和晚班同时符合:request={},firstSignTime={}", JsonHelper.toJson(request), firstSignTime);
			}
			compareThreeTimeAndSetData(signInData, firstSignTime, minDayStartDateBeforeOneHour, minMiddleStartDateBeforeOneHour,
					minNightStartDateBeforeOneHour, maxEndTimeMap, minStartTimeMap);
			// 如果白班、中班和晚班都不符合
		} else {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|白班以及中班和晚班都不符合:request={},firstSignTime={}", JsonHelper.toJson(request), firstSignTime);
			}
			compareThreeTimeAndSetData(signInData, firstSignTime, minDayStartDateBeforeOneHour, minMiddleStartDateBeforeOneHour,
					minNightStartDateBeforeOneHour, maxEndTimeMap, minStartTimeMap);
		}
	}

	private void compareThreeTimeAndSetData(UserSignRecord signInData, Date firstSignTime, Date minDayStartDateBeforeOneHour,
								  Date minMiddleStartDateBeforeOneHour, Date minNightStartDateBeforeOneHour,
								  Map<Integer, String> maxEndTimeMap, Map<Integer, String> minStartTimeMap) {
		// 首次签到时间距离最早的白班开始时间(减1小时)的差值
		long minDayDifference = 0;
		// 首次签到时间距离最早的中班开始时间(减1小时)的差值
		long minMiddleDifference = 0;
		// 首次签到时间距离最早的晚班开始时间(减1小时)的差值
		long minNightDifference = 0;
		if (minDayStartDateBeforeOneHour != null) {
			minDayDifference = Math.abs(firstSignTime.getTime() - minDayStartDateBeforeOneHour.getTime());
		}
		if (minMiddleStartDateBeforeOneHour != null) {
			minMiddleDifference = Math.abs(firstSignTime.getTime() - minMiddleStartDateBeforeOneHour.getTime());
		}
		if (minNightStartDateBeforeOneHour != null) {
			minNightDifference = Math.abs(firstSignTime.getTime() - minNightStartDateBeforeOneHour.getTime());
		}
		// 如果白班、中班、晚班都不为空
		if (minDayDifference > 0 && minMiddleDifference > 0 && minNightDifference > 0) {
			if (minDayDifference < minMiddleDifference && minDayDifference < minNightDifference) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,白中晚都不为空,首次签到时间距离白班最小起始时间更接近:signInData={},firstSignTime={},minDayDifference={},minMiddleDifference={},minNightDifference={}",
							JsonHelper.toJson(signInData), firstSignTime, minDayDifference, minMiddleDifference, minNightDifference);
				}
				setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.DAY.getCode());
			} else if (minMiddleDifference < minDayDifference && minMiddleDifference < minNightDifference) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,白中晚都不为空,首次签到时间距离中班最小起始时间更接近:signInData={},firstSignTime={},minDayDifference={},minMiddleDifference={},minNightDifference={}",
							JsonHelper.toJson(signInData), firstSignTime, minDayDifference, minMiddleDifference, minNightDifference);
				}
				setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.MIDDLE.getCode());
			} else if (minNightDifference < minDayDifference && minNightDifference < minMiddleDifference) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,白中晚都不为空,首次签到时间距离晚班最小起始时间更接近:signInData={},firstSignTime={},minDayDifference={},minMiddleDifference={},minNightDifference={}",
							JsonHelper.toJson(signInData), firstSignTime, minDayDifference, minMiddleDifference, minNightDifference);
				}
				setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.NIGHT.getCode());
			}
			// 如果只有白班、中班不为空
		} else if (minDayDifference > 0 && minMiddleDifference > 0) {
			if (minDayDifference < minMiddleDifference) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,白中不为空,首次签到时间距离白班最小起始时间更接近:signInData={},firstSignTime={},minDayDifference={},minMiddleDifference={}",
							JsonHelper.toJson(signInData), firstSignTime, minDayDifference, minMiddleDifference);
				}
				setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.DAY.getCode());
			} else {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,白中不为空,首次签到时间距离中班最小起始时间更接近:signInData={},firstSignTime={},minDayDifference={},minMiddleDifference={}",
							JsonHelper.toJson(signInData), firstSignTime, minDayDifference, minMiddleDifference);
				}
				setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.MIDDLE.getCode());
			}
			// 如果只有白班、晚班不为空
		} else if (minDayDifference > 0 && minNightDifference > 0) {
			if (minDayDifference < minNightDifference) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,白晚不为空,首次签到时间距离白班最小起始时间更接近:signInData={},firstSignTime={},minDayDifference={},minNightDifference={}",
							JsonHelper.toJson(signInData), firstSignTime, minDayDifference, minNightDifference);
				}
				setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.DAY.getCode());
			} else {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,白晚不为空,首次签到时间距离晚班最小起始时间更接近:signInData={},firstSignTime={},minDayDifference={},minNightDifference={}",
							JsonHelper.toJson(signInData), firstSignTime, minDayDifference, minNightDifference);
				}
				setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.NIGHT.getCode());
			}
			// 如果只有中班、晚班不为空
		} else if (minMiddleDifference > 0 && minNightDifference > 0) {
			if (minMiddleDifference < minNightDifference) {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,中晚不为空,首次签到时间距离白班最小起始时间更接近:signInData={},firstSignTime={},minMiddleDifference={},minNightDifference={}",
							JsonHelper.toJson(signInData), firstSignTime, minMiddleDifference, minNightDifference);
				}
				setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.MIDDLE.getCode());
			} else {
				if (log.isInfoEnabled()) {
					log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,中晚不为空,首次签到时间距离晚班最小起始时间更接近:signInData={},firstSignTime={},minMiddleDifference={},minNightDifference={}",
							JsonHelper.toJson(signInData), firstSignTime, minMiddleDifference, minNightDifference);
				}
				setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.NIGHT.getCode());
			}
			// 如果只有白班不为空
		} else if (minDayDifference > 0) {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,只有白不为空,首次签到时间距离白班最小起始时间更接近:signInData={},firstSignTime={},minDayDifference={}",
						JsonHelper.toJson(signInData), firstSignTime, minDayDifference);
			}
			setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.DAY.getCode());
			// 如果只有中班不为空
		} else if (minMiddleDifference > 0) {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,只有中不为空,首次签到时间距离中班最小起始时间更接近:signInData={},firstSignTime={},minMiddleDifference={}",
						JsonHelper.toJson(signInData), firstSignTime, minMiddleDifference);
			}
			setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.MIDDLE.getCode());
			// 如果只有晚班不为空
		} else if (minNightDifference > 0) {
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,只有晚不为空,首次签到时间距离晚班最小起始时间更接近:signInData={},firstSignTime={},minNightDifference={}",
						JsonHelper.toJson(signInData), firstSignTime, minNightDifference);
			}
			// 如果只有晚班不为空
			setInformalWaveInfo(signInData, minStartTimeMap, maxEndTimeMap, WaveTypeEnum.NIGHT.getCode());
		} else {
			// 如果白班、中班、晚班都为空
			if (log.isInfoEnabled()) {
				log.info("assembleInformalEmployeesWaveCode|白班及中班和晚班比较,白中晚都为空:signInData={},firstSignTime={}", JsonHelper.toJson(signInData), firstSignTime);
			}
		}
	}

	private void setInformalWaveInfo(UserSignRecord signInData, Map<Integer, String> minStartTimeMap, Map<Integer, String> maxEndTimeMap, Integer waveType) {
		signInData.setWaveCodeNew(String.valueOf(waveType));
		String waveTime = minStartTimeMap.get(waveType) + Constants.SEPARATOR_TILDE + maxEndTimeMap.get(waveType);
		signInData.setWaveTime(waveTime);
	}

	private void assembleRegularEmployeesWaveCode(UserSignRequest request, UserSignRecord signInData) {
		// 查询网格下所有班次
		WorkGridScheduleRequest workGridScheduleRequest = new WorkGridScheduleRequest();
		workGridScheduleRequest.setWorkGridKey(signInData.getRefWorkGridKey());
		List<WorkGridSchedule> scheduleList = workGridScheduleManager.queryByWorkGridKey(workGridScheduleRequest);
		if (CollectionUtils.isEmpty(scheduleList)) {
			log.warn("assembleRegularEmployeesWaveCode|根据网格key从basic查询网格下所有班次返回空:request={},signInData={}", JsonHelper.toJson(request), JsonHelper.toJson(signInData));
			return;
		}
		if (log.isInfoEnabled()) {
			log.info("assembleRegularEmployeesWaveCode|网格下所有班次:request={},signInData={},scheduleList={}", JsonHelper.toJson(request), JsonHelper.toJson(signInData), JsonHelper.toJson(scheduleList));
		}
		// 最早的白班开始时间
		String earlierDayTime = null;
		// 对班次列表按照时间排序
		for (WorkGridSchedule workGridSchedule : scheduleList) {
			String startTime = workGridSchedule.getStartTime();
			// 如果修改过班次时间，但是属于非立即生效场景，需要使用原来的时间
			if (StringUtils.isNotBlank(workGridSchedule.getOldStartTime())) {
				startTime = workGridSchedule.getOldStartTime();
			}
			if (StringUtils.isBlank(earlierDayTime)) {
				earlierDayTime = startTime;
			} else {
				LocalTime localEarlierDayTime = LocalTime.parse(earlierDayTime);
				LocalTime localStartTime = LocalTime.parse(startTime);
				if (localEarlierDayTime.isAfter(localStartTime)) {
					earlierDayTime = startTime;
				}
			}
		}
		if (earlierDayTime == null) {
			log.warn("assembleRegularEmployeesWaveCode|筛选最早的白班开始时间为空:request={}", JsonHelper.toJson(request));
			return;
		}

		// 当前网格当前用户的排班计划列表
		List<ScheduleDetailDto> scheduleDetailDtoList;
		UserGridScheduleQueryDto queryDto = new UserGridScheduleQueryDto();
		queryDto.setUserErp(request.getUserCode());
		queryDto.setWorkGridKey(signInData.getRefWorkGridKey());
		// 获取当前时间的时分
		Date currentDate = new Date();
		LocalTime localCurrentTime = LocalTime.parse(earlierDayTime);
		// 将最早的白班开始时间转为date格式
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, localCurrentTime.getHour());
		calendar.set(Calendar.MINUTE, localCurrentTime.getMinute());
		calendar.set(Calendar.SECOND, Constants.NUMBER_ZERO);
		Date earlierStartDate = calendar.getTime();
		// 排班日期标识：0代表当天，-1代表前一天
		Integer scheduleDateType;
		// 当前打卡时间是否在当天首个班次的起始时间前1个小时
		Date earlierStartDateBeforeOneHour = DateHelper.addHours(earlierStartDate, Constants.NEGATIVE_NUMBER_ONE);
		if (currentDate.getTime() >= earlierStartDateBeforeOneHour.getTime()) {
			if (log.isInfoEnabled()) {
				log.info("assembleRegularEmployeesWaveCode|签到时间在当天首个班次的起始时间前1个小时之内,因此获取当天的排班数据:request={},earlierDayTime={},earlierDayTimeBeforeOneHour={}", JsonHelper.toJson(request), earlierDayTime, earlierStartDateBeforeOneHour);
			}
			// 获取当天的排班数据
			queryDto.setScheduleDate(DateHelper.getSpecialDateOfyyMMdd2(currentDate.getTime()));
			scheduleDateType = Constants.NUMBER_ZERO;
		} else {
			if (log.isInfoEnabled()) {
				log.info("assembleRegularEmployeesWaveCode|签到时间不在当天首个班次的起始时间前1个小时之内,因此获取前一天的排班数据:request={},earlierDayTime={},earlierDayTimeBeforeOneHour={}", JsonHelper.toJson(request), earlierDayTime, earlierStartDateBeforeOneHour);
			}
			// 获取前一天的排班数据
			queryDto.setScheduleDate(DateHelper.getSpecialDateOfyyMMdd2(currentDate.getTime() - DateHelper.ONE_DAY));
			scheduleDateType = Constants.NEGATIVE_NUMBER_ONE;
		}
		scheduleDetailDtoList = workGridScheduleManager.findListByGridKeyAndErp(queryDto);
		// 若当前人员在没有存在排班计划，则该出勤数据展示“未排班”
		if (CollectionUtils.isEmpty(scheduleDetailDtoList)) {
			log.warn("assembleRegularEmployeesWaveCode|根据指定条件从flat查询排班计划返回空:request={},signInData={}", JsonHelper.toJson(request), JsonHelper.toJson(signInData));
			return;
		}
		if (log.isInfoEnabled()) {
			log.info("assembleRegularEmployeesWaveCode|当前人员当前网格下所有排班计划:request={},signInData={},scheduleDetailDtoList={}", JsonHelper.toJson(request), JsonHelper.toJson(signInData), JsonHelper.toJson(scheduleDetailDtoList));
		}
		// 若当前人员在当天仅存在一条排班计划
		if (scheduleDetailDtoList.size() == Constants.CONSTANT_NUMBER_ONE) {
			ScheduleDetailDto scheduleDetailDto = scheduleDetailDtoList.get(Constants.CONSTANT_NUMBER_ZERO);
			if (log.isInfoEnabled()) {
				log.info("assembleRegularEmployeesWaveCode|当前人员在当天仅存在一条排班计划:request={},scheduleDetailDto={}", JsonHelper.toJson(request), JsonHelper.toJson(scheduleDetailDto));
			}
			// 且当前erp签到时间处于该排班计划对应班次时间的前后1小时内，则班次类型展示对应的班次，班次时间展示对应班次的时间
			if (isBetweenBeforeAndAfterOneHour(scheduleDetailDto, currentDate, scheduleDateType)) {
				Integer waveType = getWaveType(scheduleDetailDto.getScheduleKey());
				signInData.setWaveCodeNew(String.valueOf(waveType));
				signInData.setWaveTime(scheduleDetailDto.getStartTime() + Constants.SEPARATOR_TILDE + scheduleDetailDto.getEndTime());
				return;
			}
			log.warn("assembleRegularEmployeesWaveCode|当前人员在当天仅存在一条排班计划,但是不满足排班计划对应班次时间的前后1小时内:request={}", JsonHelper.toJson(request));
			return;
		}
		if (log.isInfoEnabled()) {
			log.info("assembleRegularEmployeesWaveCode|当前人员在当天存在多条排班计划:request={},scheduleDetailDtoList={}", JsonHelper.toJson(request), JsonHelper.toJson(scheduleDetailDtoList));
		}
		StringBuilder waveCodeStr = new StringBuilder(Constants.EMPTY_FILL);
		StringBuilder waveTimeStr = new StringBuilder(Constants.EMPTY_FILL);
		// 若当前人员存在多个排班计划,依次判断签到时间是否处于每个排班计划对应班次时间的前后1小时内，多个中间用顿号隔开
		for (ScheduleDetailDto scheduleDetailDto : scheduleDetailDtoList) {
			if (isBetweenBeforeAndAfterOneHour(scheduleDetailDto, currentDate, scheduleDateType)) {
				Integer waveType = getWaveType(scheduleDetailDto.getScheduleKey());
				String waveTime = scheduleDetailDto.getStartTime() + Constants.SEPARATOR_TILDE + scheduleDetailDto.getEndTime();
				waveCodeStr.append(Constants.SEPARATOR_COMMA).append(waveType);
				waveTimeStr.append(Constants.SEPARATOR_COMMA).append(waveTime);
			}
		}
		if (StringUtils.isNotBlank(waveCodeStr)) {
			signInData.setWaveCodeNew(waveCodeStr.substring(Constants.CONSTANT_NUMBER_ONE));
		} else {
			log.warn("assembleRegularEmployeesWaveCode|当前人员在当天存在多条排班计划,但是都不满足排班计划对应班次时间的前后1小时内:request={}", JsonHelper.toJson(request));
		}
		if (StringUtils.isNotBlank(waveTimeStr)) {
			signInData.setWaveTime(waveTimeStr.substring(Constants.CONSTANT_NUMBER_ONE));
		}
	}

	private Integer getWaveType(String scheduleKey) {
		String[] scheduleArray = scheduleKey.split(Constants.SEPARATOR_HYPHEN);
		// 班次类型
		Integer scheduleType = Integer.valueOf(scheduleArray[1]);
		// 班次顺序
		Integer scheduleNo = Integer.valueOf(scheduleArray[2]);
		if (WaveTypeEnum.DAY.getCode().equals(scheduleType) && ScheduleEnum.TIME_1.getCode().equals(scheduleNo)) {
			return WaveTypeNewEnum.DAY1.getCode();
		} else if (WaveTypeEnum.DAY.getCode().equals(scheduleType) && ScheduleEnum.TIME_2.getCode().equals(scheduleNo)) {
			return WaveTypeNewEnum.DAY2.getCode();
		} else if (WaveTypeEnum.DAY.getCode().equals(scheduleType) && ScheduleEnum.TIME_3.getCode().equals(scheduleNo)) {
			return WaveTypeNewEnum.DAY3.getCode();
		} else if (WaveTypeEnum.MIDDLE.getCode().equals(scheduleType) && ScheduleEnum.TIME_1.getCode().equals(scheduleNo)) {
			return WaveTypeNewEnum.MIDDLE1.getCode();
		} else if (WaveTypeEnum.MIDDLE.getCode().equals(scheduleType) && ScheduleEnum.TIME_2.getCode().equals(scheduleNo)) {
			return WaveTypeNewEnum.MIDDLE2.getCode();
		} else if (WaveTypeEnum.MIDDLE.getCode().equals(scheduleType) && ScheduleEnum.TIME_3.getCode().equals(scheduleNo)) {
			return WaveTypeNewEnum.MIDDLE3.getCode();
		} else if (WaveTypeEnum.NIGHT.getCode().equals(scheduleType) && ScheduleEnum.TIME_1.getCode().equals(scheduleNo)) {
			return WaveTypeNewEnum.NIGHT1.getCode();
		} else if (WaveTypeEnum.NIGHT.getCode().equals(scheduleType) && ScheduleEnum.TIME_2.getCode().equals(scheduleNo)) {
			return WaveTypeNewEnum.NIGHT2.getCode();
		} else if (WaveTypeEnum.NIGHT.getCode().equals(scheduleType) && ScheduleEnum.TIME_3.getCode().equals(scheduleNo)) {
			return WaveTypeNewEnum.NIGHT3.getCode();
		}
		return null;
	}

	private boolean isBetweenBeforeAndAfterOneHour(ScheduleDetailDto scheduleDetailDto, Date currentDate, Integer scheduleDateType) {
		// 班次时分形式 HH:mm
		String startTime = scheduleDetailDto.getStartTime();
		String endTime = scheduleDetailDto.getEndTime();
		if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
			log.warn("assembleRegularEmployeesWaveCode|比较当前erp签到时间是否处于该排班计划对应班次时间的前后1小时内,startTime和endTime为空:scheduleDetailDto={}", JsonHelper.toJson(scheduleDetailDto));
			return false;
		}
		// 班次日期形式 yyyy-MM-dd HH:mm:ss
		Date startDate = getSpecialDateByStr(startTime, scheduleDateType);
		Date endDate;
		// 如果结束日期代表后一天的时间点，则转换时需要加一天
		if (LocalTime.parse(startTime).isAfter(LocalTime.parse(endTime))) {
			endDate = getSpecialDateByStr(endTime, scheduleDateType + Constants.NUMBER_ONE);
		} else {
			endDate = getSpecialDateByStr(endTime, scheduleDateType);
		}
		Date startDateBeforeOneHour = DateHelper.addHours(startDate, Constants.NEGATIVE_NUMBER_ONE);
		Date endDateAfterOneHour = DateHelper.addHours(endDate, Constants.NUMBER_ONE);
		if (log.isInfoEnabled()) {
			log.info("assembleRegularEmployeesWaveCode|开始比较当前erp签到时间是否处于该排班计划对应班次时间的前后1小时内:scheduleDetailDto={},startDateBeforeOneHour={},endDateAfterOneHour={},scheduleDateType={}",
					JsonHelper.toJson(scheduleDetailDto), startDateBeforeOneHour, endDateAfterOneHour, scheduleDateType);
		}
		// 且当前erp签到时间处于该排班计划对应班次时间的前后1小时内，则班次类型展示对应的班次，班次时间展示对应班次的时间
		if (currentDate.getTime() <= endDateAfterOneHour.getTime() && currentDate.getTime() >= startDateBeforeOneHour.getTime()) {
			return true;
		}
		return false;
	}

	private boolean doSignIn(UserSignRecord userSignRecord) {
		Date date = new Date();
		userSignRecord.setCreateTime(date);
		userSignRecord.setSignInTime(date);
		userSignRecord.setSignDate(date);
		return this.insert(userSignRecord).getData();
	}
	/**
	 * 设置相关的字段信息
	 * @param userSignRecord
	 * @return
	 */
	private boolean doSignInForDeveloper(UserSignRecord userSignRecord) {
		Date date = new Date();
		userSignRecord.setCreateTime(date);
		userSignRecord.setSignInTime(date);
		userSignRecord.setSignDate(date);
		userSignRecord.setYn(Constants.YN_YES);
		userSignRecord.setTs(date);
		return true;
	}
	private boolean doSignOut(UserSignRecord userSignRecord) {
		userSignRecord.setUpdateTime(new Date());
		userSignRecord.setSignOutTime(new Date());
		return userSignRecordDao.updateById(userSignRecord) == 1;
	}
	@Override
	public JdCResponse<PageDto<UserSignRecordData>> querySignListWithPosition(UserSignQueryRequest query) {
		JdCResponse<PageDto<UserSignRecordData>> result = new JdCResponse<>();
		result.toSucceed();
		if(query == null
				|| StringHelper.isEmpty(query.getPositionCode())) {
			result.toFail("岗位码不能为空！");
			return result;
		}

		log.info("querySignListWithPosition -获取基础服务数据");
		com.jdl.basic.common.utils.Result<com.jdl.basic.api.domain.position.PositionDetailRecord> positionData
				= positionManager.queryOneByPositionCode(query.getPositionCode());
		if(positionData == null
				|| positionData.getData() == null) {
			result.toFail("岗位码无效，联系【作业流程组】小哥维护岗位码！");
			return result;
		}
		//当天0点
		Date nowDate = DateHelper.parseDate(DateHelper.getDateOfyyMMdd2(),DateHelper.DATE_FORMAT_YYYYMMDD);
		//设置时间查询范围
		query.setSignInTimeStart(DateHelper.addDate(nowDate, -(this.queryByPositionRangeDays - 1)));
		query.setSignInTimeEnd(DateHelper.add(nowDate, Calendar.SECOND, (int)DateHelper.ONE_DAY_SECONDS - 1));
		query.setRefGridKey(positionData.getData().getRefGridKey());

		if(query.getPageSize() == null
				|| query.getPageSize() <= 0) {
			query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT);
		}
		query.setOffset(0);
		query.setLimit(query.getPageSize());
		if(query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
		}
		PageDto<UserSignRecordData> PageDto = new PageDto<>(query.getPageNumber(), query.getPageSize());
		Long totalCount = userSignRecordDao.queryCountWithPosition(query);
		if(totalCount != null && totalCount > 0){
		    List<UserSignRecordData> dataList = userSignRecordDao.queryListWithPosition(query);
		    Date currentDate = new Date();
		    for(UserSignRecordData data: dataList) {
		    	fillOtherInfo(data,currentDate);
		    }
		    PageDto.setResult(dataList);
			PageDto.setTotalRow(totalCount.intValue());
		}else {
		    PageDto.setResult(new ArrayList<UserSignRecordData>());
			PageDto.setTotalRow(0);
		}
		result.setData(PageDto);
		return result;
	}
	public UserSignRecordData fillOtherInfo(UserSignRecordData data,Date currentDate) {
		if(data == null) {
			return null;
		}
		data.setWaveName(WaveTypeEnum.getNameByCode(data.getWaveCode()));
		data.setJobName(JobTypeEnum.getNameByCode(data.getJobCode()));
		// 当前数据库user_name字段可能还有外包临时工身份证号，需要兼容加密防止泄漏身份证号
		data.setUserName(BusinessUtil.encryptIdCard(data.getUserName()));
		//只有user_code是身份证号的才需要将userName设置成身份证号
		if (BusinessUtil.isIdCardNo(data.getUserCode())) {
			data.setUserName(BusinessUtil.encryptIdCard(data.getUserCode()));
		}
		if(data.getSignInTime() != null) {
			String workHours = "";
			String workTimes = "--";
			double workHoursDouble = calculateWorkHours(data.getSignInTime(),data.getSignOutTime(),currentDate);
			if(workHoursDouble > 0) {
				workHours = NUMBER_FORMAT.format(workHoursDouble);
				data.setWorkHours(workHours);
				workTimes = DateHelper.hoursToHHMM(workHoursDouble);
			}
			data.setWorkTimes(workTimes);
		}
		return data;
	}
	private double calculateWorkHours(Date signInTime,Date signOutTime,Date currentDate) {
		Date workEndTime = signOutTime;
		if(workEndTime == null) {
			workEndTime = currentDate;
		}
		return DateHelper.betweenHours(signInTime,workEndTime);
	}
	@Override
	public JdCResponse<UserSignRecordData> queryLastUserSignRecordData(UserSignQueryRequest query) {
		JdCResponse<UserSignRecordData> result = new JdCResponse<>();
		result.toSucceed();
		if(query == null
				|| StringHelper.isEmpty(query.getUserCode())) {
			result.toFail("用户编码不能为空！");
			return result;
		}
		result.setData(fillOtherInfo(userSignRecordDao.queryLastUserSignRecordData(query),new Date()));
		return result;
	}
	private UserSignRecordData toUserSignRecordData(UserSignRecord record) {
		if(record == null) {
			return null;
		}
		UserSignRecordData data = new UserSignRecordData();
		BeanUtils.copyProperties(record, data);
		return fillOtherInfo(data,new Date());
	}
	private UserSignRecordData queryUserSignRecordDataById(Long id) {
		return fillOtherInfo(userSignRecordDao.queryUserSignRecordDataById(id),new Date());
	}
	@Override
	public List<UserSignRecordData> queryUserSignRecordDataByIds(List<Long> idList) {
		List<UserSignRecordData> dataList = userSignRecordDao.queryUserSignRecordDataByIds(idList);
		if(!CollectionUtils.isEmpty(dataList)) {
			Date currentDate = new Date();
		    for(UserSignRecordData data: dataList) {
		    	fillOtherInfo(data,currentDate);
		    }
		}
		return dataList;
	}
	@Override
	public List<UserSignRecord> queryUnSignOutListWithPosition(UserSignQueryRequest query) {
		return userSignRecordDao.queryUnSignOutListWithPosition(query);
	}
	@Override
	public JdCResponse<UserSignRecordData> signInWithGroup(UserSignRequest signInRequest) {
		UserSignContext context = new UserSignContext();
		context.userSignRequest = signInRequest;
		JdCResponse<UserSignRecordData> result = this.doSignIn(context);
		if(!result.isSucceed()) {
			return result;
		}
		this.addOrRemoveMember(context);
		if(context.groupData == null) {
        	//没有做签到，查询group信息
        	JdCResponse<GroupMemberData> groupResult = this.jyGroupMemberService.queryGroupMemberDataByPositionCode(signInRequest.getPositionCode());
        	if(groupResult!= null
        			&& groupResult.isSucceed()
        			&& groupResult.getData()!= null) {
        		result.getData().setGroupData(groupResult.getData());
        	}else if(groupResult!= null){
        		result.toFail(groupResult.getMessage());
        	}else {
        		result.toFail("获取岗位码对应的小组信息失败！");
        	}
		}else {
			result.getData().setGroupData(context.groupData);
		}
		return result;
	}
	@Override
	public JdCResponse<UserSignRecordData> signOutWithGroup(UserSignRequest signOutRequest) {
		UserSignContext context = new UserSignContext();
		context.userSignRequest = signOutRequest;
		JdCResponse<UserSignRecordData> result = this.doSignOut(context);
		if(!result.isSucceed()) {
			return result;
		}
		this.addOrRemoveMember(context);
		result.getData().setGroupData(context.groupData);
		return result;
	}
	@Override
	public JdCResponse<UserSignRecordData> signAutoWithGroup(UserSignRequest userSignRequest) {
		UserSignContext context = new UserSignContext();
		context.userSignRequest = userSignRequest;
		JdCResponse<UserSignRecordData> result = this.doSignAuto(context);
		if(!result.isSucceed()) {
			return result;
		}
		this.addOrRemoveMember(context);
		result.getData().setGroupData(context.groupData);
		return result;
	}
	/**
	 * 添加|剔除组员
	 * @param context
	 * @return
	 */
	private void addOrRemoveMember(UserSignContext context){

		if(context.deleteData != null) {
			GroupMemberRequest removeMemberRequest = new GroupMemberRequest();
			removeMemberRequest.setMemberType(JyGroupMemberTypeEnum.PERSON.getCode());
			removeMemberRequest.setSignRecordId(context.deleteData.getId());
			removeMemberRequest.setOperateUserCode(context.userSignRequest.getOperateUserCode());
			removeMemberRequest.setOperateUserName(context.userSignRequest.getOperateUserName());
			removeMemberRequest.setSignOutTime(context.deleteData.getSignOutTime());
			JdCResponse<GroupMemberData> removeMemberResult = jyGroupMemberService.deleteMember(removeMemberRequest);
			//签退设置-组
			if(removeMemberResult.isSucceed()) {
				context.groupData = removeMemberResult.getData();
			}
			return;
		}
		if(context.signOutData != null) {
			GroupMemberRequest removeMemberRequest = new GroupMemberRequest();
			removeMemberRequest.setMemberType(JyGroupMemberTypeEnum.PERSON.getCode());
			removeMemberRequest.setSignRecordId(context.signOutData.getId());
			removeMemberRequest.setOperateUserCode(context.userSignRequest.getOperateUserCode());
			removeMemberRequest.setOperateUserName(context.userSignRequest.getOperateUserName());
			removeMemberRequest.setSignOutTime(context.signOutData.getSignOutTime());
			JdCResponse<GroupMemberData> removeMemberResult = jyGroupMemberService.removeMember(removeMemberRequest);
			//签退设置-组
			if(removeMemberResult.isSucceed() && !context.signInFlag) {
				context.groupData = removeMemberResult.getData();
			}
		}
		if(context.signInData != null) {
			GroupMemberRequest addMemberRequest = new GroupMemberRequest();
			addMemberRequest.setMemberType(JyGroupMemberTypeEnum.PERSON.getCode());
			addMemberRequest.setSignInTime(context.signInData.getSignInTime());
			addMemberRequest.setSignRecordId(context.signInData.getId());
			addMemberRequest.setPositionCode(context.userSignRequest.getPositionCode());
			addMemberRequest.setJobCode(context.signInData.getJobCode());
			addMemberRequest.setUserCode(context.signInData.getUserCode());
			addMemberRequest.setUserName(context.signInData.getUserName());
			addMemberRequest.setOrgCode(context.signInData.getOrgCode());
			addMemberRequest.setSiteCode(context.signInData.getSiteCode());
			addMemberRequest.setOperateUserCode(context.userSignRequest.getOperateUserCode());
			addMemberRequest.setOperateUserName(context.userSignRequest.getOperateUserName());
			JdCResponse<GroupMemberData> addMemberResult = jyGroupMemberService.addMember(addMemberRequest);
			//签到设置-组
			if(addMemberResult.isSucceed() && context.signInFlag) {
				context.groupData = addMemberResult.getData();
			}
		}
	}

	@Override
	@JProfiler(jKey = "DMS.WEB.UserSignRecordService.queryUnsignedOutRecordByRefGridKey", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
	public Result<List<UserSignRecord>> queryUnsignedOutRecordByRefGridKey(String refGridKey){
		Result result = new Result<>();
		result.toSuccess();
		ArrayList<UserSignRecord> listData = new ArrayList<>();
		UserSignQueryRequest query = new UserSignQueryRequest();
		query.setRefGridKey(refGridKey);
		long total = userSignRecordDao.queryTotalUnsignedOutRecordByRefGridKey(refGridKey);
		for(int offset = 0; offset < total; offset += query.getLimit()){
			query.setOffset(offset);
			listData.addAll(userSignRecordDao.queryUnsignedOutRecordByRefGridKey(query));
		}
		result.setData(listData);
		return result;
	}
	@Override
	public JdCResponse<UserSignRecordData> deleteUserSignRecord(UserSignRequest userSignRequest) {
		UserSignContext context = new UserSignContext();
		context.userSignRequest = userSignRequest;
		JdCResponse<UserSignRecordData> result = this.doDelete(context);
		if(!result.isSucceed()) {
			return result;
		}
		this.addOrRemoveMember(context);
		result.getData().setGroupData(context.groupData);
		return result;
	}
	/**
	 * 作废操作
	 * @param context
	 * @return
	 */
	private JdCResponse<UserSignRecordData> doDelete(UserSignContext context) {
		JdCResponse<UserSignRecordData> result = new JdCResponse<>();
		result.toSucceed("作废成功");
		UserSignRequest userSignRequest = context.getUserSignRequest();
		if(userSignRequest == null || userSignRequest.getRecordId() == null) {
			result.toFail("签到记录Id不能为空！");
			return result;
		}
		if(StringUtils.isBlank(userSignRequest.getOperateUserCode())
				|| StringUtils.isBlank(userSignRequest.getOperateUserName())) {
			result.toFail(MSG_EMPTY_OPERATE);
			return result;
		}
		UserSignRecordData data = queryUserSignRecordDataById(userSignRequest.getRecordId());
		if(data == null) {
			result.toFail("签到数据无效|已作废！");
			return result;
		}
		Date currentDate = new Date();
		//作废时间限制
		double workHoursDouble = DateHelper.betweenHours(data.getSignInTime(), currentDate);
		if(workHoursDouble > this.deleteCheckHours) {
			result.toFail("签到时间已超过"+deleteCheckHours+"小时，无法操作作废！");
			return result;
		}
		//删除权限校验
		JdCResponse<Boolean> permissionCheckResult = checkHavePermission(data,userSignRequest.getOperateUserCode());
		if(!permissionCheckResult.isSucceed()) {
			result.toFail(permissionCheckResult.getMessage());
			return result;
		}
		UserSignRecord deleteData = new UserSignRecord();
		deleteData.setId(data.getId());
		deleteData.setUpdateTime(currentDate);
		deleteData.setUpdateUser(userSignRequest.getOperateUserCode());
		deleteData.setUpdateUserName(userSignRequest.getOperateUserName());
		this.deleteById(deleteData);
		context.deleteData = data;
		data.setYn(Constants.YN_NO);
		result.setData(data);
		return result;
	}
	/**
	 * 校验是否有作废权限
	 * @param data
	 * @param operateUserCode
	 * @return
	 */
	private JdCResponse<Boolean> checkHavePermission(UserSignRecordData data,String operateUserCode){
		JdCResponse<Boolean> result = new JdCResponse<>();
		result.toSucceed("校验成功");
		//本人|签到操作人，允许签退、作废
		if(ObjectUtils.equals(operateUserCode, data.getUserCode())
				|| ObjectUtils.equals(operateUserCode, data.getCreateUser())) {
			return result;
		}
		String createUser = data.getCreateUser();
		String ownerUserErp = null;
		com.jdl.basic.api.domain.workStation.WorkStationGridQuery  workStationGridCheckQuery = new com.jdl.basic.api.domain.workStation.WorkStationGridQuery ();
		workStationGridCheckQuery.setBusinessKey(data.getRefGridKey());
		com.jdl.basic.common.utils.Result<com.jdl.basic.api.domain.workStation.WorkStationGrid> workStationGridData = workStationGridManager.queryByGridKey(workStationGridCheckQuery);
		if(workStationGridData != null
				&& workStationGridData.getData() != null) {
			ownerUserErp = workStationGridData.getData().getOwnerUserErp();
		}
		//网格负责人，允许签退、作废
		if(ObjectUtils.equals(operateUserCode, ownerUserErp)) {
			return result;
		}
		//同时为空，不校验
		if(StringUtils.isBlank(ownerUserErp) && StringUtils.isBlank(createUser)) {
			return result;
		}
		//权限验证失败-网格负责人为空
		//1、、创建人和负责人是同一个人
		if(StringUtils.isBlank(ownerUserErp) || ObjectUtils.equals(createUser, ownerUserErp)) {
			result.toFail(String.format(MSG_FORMAT_HAS_NO_PERMISSION_1, createUser));
		}else {
			result.toFail(String.format(MSG_FORMAT_HAS_NO_PERMISSION_2, createUser,ownerUserErp));
		}
		return result;
	}
	/**
	 * 签到处理上下文
	 * @author wuyoude
	 *
	 */
	private class UserSignContext implements Serializable {
		private static final long serialVersionUID = 1L;
		/**
		 * 请求体
		 */
		UserSignRequest userSignRequest;
		/**
		 * 签到数据
		 */
		UserSignRecordData signInData;
		/**
		 * 签退数据
		 */
		UserSignRecordData signOutData;
		/**
		 * 作废数据
		 */
		UserSignRecordData deleteData;
		/**
		 * 签到标识
		 */
		boolean signInFlag;
		/**
		 * 组信息
		 */
		GroupMemberData groupData;

		/**
		 * 操作人code
		 */
		private String operateUserCode;
		/**
		 * 操作人name
		 */
		private String operateUserName;
		/**
		 * 操作时间
		 */
		private String operateTime;


		public UserSignRequest getUserSignRequest() {
			return userSignRequest;
		}
		public void setUserSignRequest(UserSignRequest userSignRequest) {
			this.userSignRequest = userSignRequest;
		}
		public UserSignRecordData getSignInData() {
			return signInData;
		}
		public void setSignInData(UserSignRecordData signInData) {
			this.signInData = signInData;
		}
		public UserSignRecordData getSignOutData() {
			return signOutData;
		}
		public void setSignOutData(UserSignRecordData signOutData) {
			this.signOutData = signOutData;
		}
		public UserSignRecordData getDeleteData() {
			return deleteData;
		}
		public void setDeleteData(UserSignRecordData deleteData) {
			this.deleteData = deleteData;
		}
		public boolean isSignInFlag() {
			return signInFlag;
		}
		public void setSignInFlag(boolean signInFlag) {
			this.signInFlag = signInFlag;
		}
		public GroupMemberData getGroupData() {
			return groupData;
		}
		public void setGroupData(GroupMemberData groupData) {
			this.groupData = groupData;
		}
		public String getOperateUserCode() {
			return operateUserCode;
		}
		public void setOperateUserCode(String operateUserCode) {
			this.operateUserCode = operateUserCode;
		}
		public String getOperateUserName() {
			return operateUserName;
		}
		public void setOperateUserName(String operateUserName) {
			this.operateUserName = operateUserName;
		}
		public String getOperateTime() {
			return operateTime;
		}
		public void setOperateTime(String operateTime) {
			this.operateTime = operateTime;
		}
	}
	@Override
	public JdCResponse<PageDto<UserSignRecordData>> querySignListByOperateUser(UserSignQueryRequest query) {
		JdCResponse<PageDto<UserSignRecordData>> result = new JdCResponse<>();
		result.toSucceed();
		Date signDate = query.getSignDate();
		if(signDate == null && StringHelper.isNotEmpty(query.getSignDateStr())) {
			signDate = DateHelper.parseDate(query.getSignDateStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
		}
		query.setSignDate(signDate);
		if(query.getPageSize() == null
				|| query.getPageSize() <= 0) {
			query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT);
		}
		query.setOffset(0);
		query.setLimit(query.getPageSize());
		if(query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
		}
		PageDto<UserSignRecordData> PageDto = new PageDto<>(query.getPageNumber(), query.getPageSize());
		Long totalCount = userSignRecordDao.queryCountByOperateUser(query);
		if(totalCount != null && totalCount > 0){
		    List<UserSignRecordData> dataList = userSignRecordDao.queryListByOperateUser(query);
		    Date currentDate = new Date();
		    for(UserSignRecordData data: dataList) {
		    	fillOtherInfo(data,currentDate);
		    }
		    PageDto.setResult(dataList);
			PageDto.setTotalRow(totalCount.intValue());
		}else {
		    PageDto.setResult(new ArrayList<UserSignRecordData>());
			PageDto.setTotalRow(0);
		}
		result.setData(PageDto);
		return result;
	}
	@Override
	public JdCResponse<UserSignRecordData> queryLastUnSignOutRecordData(UserSignQueryRequest query) {
		JdCResponse<UserSignRecordData> result = new JdCResponse<>();
		result.toSucceed();
		UserSignRecordQuery lastSignRecordQuery = new UserSignRecordQuery();
		lastSignRecordQuery.setUserCode(query.getUserCode());
        if(query.getUserId() != null){
            lastSignRecordQuery.setUserId(query.getUserId());
            lastSignRecordQuery.setUserCode(null);
        }
		UserSignRecord lastUnSignOutData = userSignRecordDao.queryLastUnSignOutRecord(lastSignRecordQuery);
		//加载网格相关数据
		loadGridData(lastUnSignOutData);
		UserSignRecordData signData = this.toUserSignRecordData(lastUnSignOutData);
		if(signData != null) {
			//查询组员信息
			JyGroupMemberEntity memberData = this.jyGroupMemberService.queryBySignRecordId(signData.getId());
			if(memberData != null) {
				GroupMemberData groupData = new GroupMemberData();
				groupData.setGroupCode(memberData.getRefGroupCode());
				signData.setGroupData(groupData);
				//查询分组信息
				JyGroupEntity group = this.jyGroupService.queryGroupByGroupCode(memberData.getRefGroupCode());
				if(group != null) {
					signData.setPositionCode(group.getPositionCode());
				}
			}
		}
		result.setData(signData);
		return result;
	}

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
			String  msg=checkAttendanceBlackListOfBefore(result,userCode);
			if(StringUtils.isNotBlank(msg)){
				return result;
			}
		}
		return checkUserSignStatus(positionCode, userSignRequest.getJobCode(), userSignRequest.getUserCode());
	}

	@Override
	public JdCResponse<UserSignRecordData> checkUserSignStatus(String positionCode, Integer jobCode, String userCode) {
		JdCResponse<UserSignRecordData> result = new JdCResponse<UserSignRecordData>();
		result.toSucceed();
		UserSignQueryRequest query = new UserSignQueryRequest();
		query.setUserCode(userCode);
		UserSignRecordData lastUnSignOutData = null;
		JdCResponse<UserSignRecordData> lastUnSignOutResult = this.queryLastUnSignOutRecordData(query);
		if(lastUnSignOutResult != null
				&&lastUnSignOutResult.getData() != null) {
			lastUnSignOutData = lastUnSignOutResult.getData();
		}

		// 新增：强卡，校验网格工序的标准配置人数
		PositionSignNumDto dto = new PositionSignNumDto();
		if (!this.checkStandardNum(dto, positionCode, userCode)) {
			String defaultMsg = String.format(CHECK_STANDARD_NUM_SIGN_MSG, dto.getPositionDetailRecord().getStandardNum(),
					dto.getNum(), dto.getNum(), dto.getPositionDetailRecord().getOwnerUserErp());
			result.toFail(defaultMsg);
			return result;
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
		if (!this.checkOperatorBaseInfo(positionCode, userCode, dto)) {
			result.toConfirm(HintService.getHint(HintCodeConstants.CONFIRM_ITE_OR_PROVINCE_DIFF_FOR_SIGN_MSG,
					HintCodeConstants.CONFIRM_ITE_OR_PROVINCE_DIFF_FOR_SIGN_CODE, false));
			return result;
		}

		//判断上次签退是否人脸识别自动签退
		if(lastUnSignOutData == null) {
			UserSignQueryRequest lastSignQuery = new UserSignQueryRequest();
			lastSignQuery.setUserCode(userCode);
			JdCResponse<UserSignRecordData> lastSignResult = this.queryLastUserSignRecordData(lastSignQuery);
			UserSignRecordData lastSignData = null;
			if(lastSignResult != null
					&&lastSignResult.getData() != null) {
				lastSignData = lastSignResult.getData();
				//需要判断当前时间与系统自动签退时间是否小于2小时，若小于2小时,需要确认
				Date checkTime = DateHelper.addHours(new Date(), - needCheckAutoSignOutHours);
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
     * 判断当前该工序已签到未签退的人数是否大于等于当前该工序的标准编制人数
     * @param dto 位置详情记录对象
     * @return 检查结果，布尔值
     */
	private boolean checkStandardNum(PositionSignNumDto dto, String positionCode, String userCode) {
		// 查询网格码信息
		com.jdl.basic.common.utils.Result<PositionDetailRecord> apiResult = positionManager.queryOneByPositionCode(positionCode);
		if(apiResult == null || !apiResult.isSuccess()  || apiResult.getData() == null){
			return true;
		}
		dto.setPositionDetailRecord(apiResult.getData());
		if (Objects.isNull(dto.getPositionDetailRecord())){
			return true;
		}

		boolean flag = sysConfigService.getConfigByName(Constants.STAND_NUM_PDA_SIGN_CHECK_SWITCH);
		if (log.isInfoEnabled()){
			log.info("UserSignRecordServiceImpl.checkStandardNum 网格工序编制人数配置-PDA强卡开关:{}", flag);
		}
		if(!flag){
			return true;
		}

		if (log.isInfoEnabled()){
			log.info("UserSignRecordServiceImpl.checkStandardNum 岗位数据：{}，user_code:{}", JsonHelper.toJson(dto), userCode);
		}
		UserSignQueryRequest request = new UserSignQueryRequest();
		request.setRefGridKey(dto.getPositionDetailRecord().getRefGridKey());
		List<UserSignRecord> userSignRecords = queryUnSignOutListWithPosition(request);
		if (CollectionUtils.isEmpty(userSignRecords)){
			return true;
		}
		dto.setNum(userSignRecords.size());
		if (userSignRecords.size() >= dto.getPositionDetailRecord().getStandardNum()){
			// 已经登陆的用户可以继续登陆
			List<String> userCodeList = userSignRecords.stream().map(UserSignRecord::getUserCode).collect(Collectors.toList());
			if (log.isInfoEnabled()){
				log.info("UserSignRecordServiceImpl.checkStandardNum 登陆用户编码集合：{}", JsonHelper.toJson(userCodeList));
			}
			if (CollectionUtils.isNotEmpty(userCodeList) && userCodeList.contains(userCode)){
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * 作业APP网格码错误检验
	 */
	private boolean checkOperatorBaseInfo(String positionCode, String userCode, PositionSignNumDto dto) {
		if (StringUtils.isBlank(positionCode) || StringUtils.isBlank(userCode)) {
			return true;
		}
		// 查询网格码信息
		PositionDetailRecord positionDetailRecord = dto.getPositionDetailRecord();
		if(positionDetailRecord == null){
			return true;
		}
		BaseSiteInfoDto dtoStaff = baseMajorManager.getBaseSiteInfoBySiteId(positionDetailRecord.getSiteCode());
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
			return baseStaffByErp.getSiteCode().equals(positionDetailRecord.getSiteCode());
		}
		// 网格码为接货仓场地类型
		if (BusinessUtil.isReceivingWarehouse(dtoStaff.getSortType())) {
			// 所属场地对应省区与网格码所属接货仓省区是否一致
			if (StringUtils.isBlank(baseStaffByErp.getProvinceAgencyCode()) || StringUtils.isBlank(positionDetailRecord.getProvinceAgencyCode())) {
				return true;
			}
			return baseStaffByErp.getProvinceAgencyCode().equals(positionDetailRecord.getProvinceAgencyCode());
		}
		return true;
	}

	/**
	 * 签到记录-加载网格相关数据
	 * @param signData
	 */
	private void loadGridData(UserSignRecord signData) {
		if(signData != null) {
			com.jdl.basic.api.domain.workStation.WorkStationGridQuery  workStationGridCheckQuery = new com.jdl.basic.api.domain.workStation.WorkStationGridQuery ();
			workStationGridCheckQuery.setBusinessKey(signData.getRefGridKey());
			com.jdl.basic.common.utils.Result<WorkStationGrid> gridData = workStationGridManager.queryByGridKey(workStationGridCheckQuery);
			if(gridData != null && gridData.getData() != null) {
				signData.setGridCode(gridData.getData().getGridCode());
				signData.setGridName(gridData.getData().getGridName());
				signData.setGridNo(gridData.getData().getGridNo());
				signData.setAreaCode(gridData.getData().getAreaCode());
				signData.setAreaName(gridData.getData().getAreaName());
				signData.setWorkCode(gridData.getData().getWorkCode());
				signData.setWorkName(gridData.getData().getWorkName());
			}
		}
	}
	@Override
	public Result<UserSignRecord> checkAndCreateSignInDataForFlowAdd(UserSignRequest signInRequest) {
		Result<UserSignRecord> result = new Result<UserSignRecord>();
		result.toSuccess();
		JdCResponse<UserSignRecordData> checkResult = checkAndFillUserInfo(signInRequest);
		if(!checkResult.isSucceed()) {
			result.toFail(checkResult.getMessage());
			return result;
		}
		//校验岗位码,并获取网格信息
		JdCResponse<WorkStationGrid> gridResult = this.checkAndGetWorkStationGrid(signInRequest);
		if(!gridResult.isSucceed()) {
			result.toFail(gridResult.getMessage());
			return result;
		}
		WorkStationGrid gridInfo = gridResult.getData();
		//校验并组装签到数据
        UserSignRecord signInData = new UserSignRecord();
        JdCResponse<UserSignRecordData> fillResult = this.checkAndFillSignInInfo(signInRequest,signInData,gridInfo);
        if(!fillResult.isSucceed()) {
        	result.toFail(fillResult.getMessage());
        	return result;
        }
        result.setData(signInData);
		return result;
	}
	@Override
	public Integer queryCountForFlow(UserSignRecordQuery historyQuery) {
		return userSignRecordDao.queryCountForFlow(historyQuery);
	}
	@Override
	public List<UserSignRecord> queryDataListForFlow(UserSignRecordQuery historyQuery) {
		return userSignRecordDao.queryDataListForFlow(historyQuery);
	}
	@Override
	public Integer queryCountForCheckSignTime(UserSignRecordFlowQuery checkQuery) {
		return userSignRecordDao.queryCountForCheckSignTime(checkQuery);
	}
	@Override
	public UserSignRecord queryByIdForFlow(Long recordId) {
		return userSignRecordDao.queryByIdForFlow(recordId);
	}

	@Override
	public List<UserSignRecord> listSignRecordByTime(UserSignRecordQuery query) {
		checkUserSignRecordQuery(query);
		return userSignRecordDao.listSignRecordByTime(query);
	}

	/**
	 * 用business_key查询
	 * @param query
	 * @return
	 */
	@Override
	public List<UserSignRecord> queryByBusinessKeyAndJobCode(UserSignRecordQuery query) {
		return userSignRecordDao.queryByBusinessKeyAndJobCode(query);
	}

	private void checkUserSignRecordQuery(UserSignRecordQuery query) {
		if (ObjectHelper.isEmpty(query.getSignDateStart())){
			throw new JyBizException("参数错误：签到开始时间为空！");
		}
		if (ObjectHelper.isEmpty(query.getSignDateEnd())){
			throw new JyBizException("参数错误：签到结束时间为空！");
		}
		if (ObjectHelper.isEmpty(query.getUserCode())){
			throw new JyBizException("参数错误：用户编码为空！");
		}
		if (ObjectHelper.isEmpty(query.getSiteCode())){
			throw new JyBizException("参数错误：场地编码为空！");
		}
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
				Date loseTime=attendanceBlackList.getLoseTime();
				String dateStr= DateUtil.format(new Date(),DateUtil.FORMAT_DATE_MINUTE);
				Date currentTime=DateUtil.parse(dateStr,DateUtil.FORMAT_DATE_MINUTE);
				if(cancelFlag ==Constants.NUMBER_ZERO && ((loseTime ==null && currentTime.compareTo(takeTime) >=0) ||  (loseTime !=null && currentTime.compareTo(takeTime) >=0 && currentTime.compareTo(loseTime) <0))){//已生效
					//已生效
					String defaultMsg = String.format(HintCodeConstants.ATTENDANCE_BLACK_LIST_TAKE_EFFECTIVE_MSG, userCode);
					result.toFail(defaultMsg);
					return defaultMsg;
				}
			}
		}
		return  "";
	}

	private String checkAttendanceBlackListOfBefore(JdCResponse<UserSignRecordData> result,String userCode){
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

    /**
     * 根据配置获取站点代码列表
     * @return result 站点代码列表
     * @throws Exception 异常情况
     */
	private List<Integer> getSiteCodeByConfig() {
		ArrayList<Integer> result = new ArrayList<>();
		SysConfig sysConfig =
			sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_AUTO_SIGN_OUT_SITE_CODE);
		if (Objects.isNull(sysConfig) || StringUtils.isBlank(sysConfig.getConfigContent())){
			return result;
		}
		for (String siteCode : sysConfig.getConfigContent().split(",")) {
			if(NumberHelper.isNumber(siteCode)){
				result.add(Integer.valueOf(siteCode));
			}
		}
        return result;
	}
	@Override
	public List<BaseUserSignRecordVo> queryByGridSign(UserSignRecordQuery query){
		return userSignRecordDao.queryByGridSign(query);
	}

	@Override
	public List<BaseUserSignRecordVo> queryMonitorRoomPerson(UserSignRecordQuery query) {
		return userSignRecordDao.queryMonitorRoomPerson(query);
	}
}
