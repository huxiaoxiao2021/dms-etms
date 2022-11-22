package com.jd.bluedragon.distribution.station.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.group.GroupMemberData;
import com.jd.bluedragon.common.dto.group.GroupMemberRequest;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationAttendPlanManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberTypeEnum;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.position.domain.PositionDetailRecord;
import com.jd.bluedragon.distribution.position.service.PositionRecordService;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordDao;
import com.jd.bluedragon.distribution.station.domain.*;
import com.jd.bluedragon.distribution.station.enums.JobTypeEnum;
import com.jd.bluedragon.distribution.station.enums.WaveTypeEnum;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.distribution.station.query.WorkStationGridQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.distribution.station.service.WorkStationAttendPlanService;
import com.jd.bluedragon.distribution.station.service.WorkStationGridService;
import com.jd.bluedragon.distribution.station.service.WorkStationService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.domain.BaseSite;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.workStation.WorkStation;
import com.jdl.basic.api.domain.workStation.WorkStationAttendPlan;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;

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
	private PositionManager positionManager;

	@Autowired
	private PositionRecordService positionRecordService;


	@Autowired
	@Qualifier("jyGroupMemberService")
	private JyGroupMemberService jyGroupMemberService;
	
	@Autowired
	private BaseMajorManager baseMajorManager;

	private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0.00");
	private static final DecimalFormat RATE_FORMAT = new DecimalFormat("0.00%");

    @Autowired
    private UccPropertyConfiguration uccConfiguration;
	
	@Autowired
	private WorkStationManager workStationManager;
	@Autowired
	private WorkStationGridManager workStationGridManager;

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(UserSignRecord insertData){
		Result<Boolean> result = Result.success();
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
        userSignRecordDao.insert(signInRequest);

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
        int notSignedOutRecordMoreThanHours = uccConfiguration.getNotSignedOutRecordMoreThanHours();
        if (notSignedOutRecordMoreThanHours < 0) {
            return result;
        }
        int notSignedOutRecordRangeHours = uccConfiguration.getNotSignedOutRecordRangeHours();
        //扫描范围不能小于1小时
        if(notSignedOutRecordRangeHours < 1) {
        	notSignedOutRecordRangeHours = 1;
        }
        Date signInTimeEnd = DateHelper.add(new Date(),Calendar.HOUR_OF_DAY, -notSignedOutRecordMoreThanHours);
        Date signInTimeStart = DateHelper.add(signInTimeEnd,Calendar.HOUR_OF_DAY,-notSignedOutRecordRangeHours);
        List<Long> toSignOutPks;
        Date now = new Date();
        int updateRows = 0;
        log.info("自动签退数据扫描：{} - {}", DateHelper.formatDateTimeMs(signInTimeStart),DateHelper.formatDateTimeMs(signInTimeEnd));
        try {
            do {
                toSignOutPks = userSignRecordDao.querySignInMoreThanSpecifiedTime(signInTimeStart,signInTimeEnd, 100);

                if (CollectionUtils.isNotEmpty(toSignOutPks)) {
                    UserSignRecord updateData = new UserSignRecord();
                    updateData.setSignOutTime(now);
                    updateData.setUpdateTime(now);
                    updateData.setUpdateUser("sys.dms");
                    updateData.setUpdateUserName(updateData.getUpdateUser());

                    updateRows += userSignRecordDao.signOutById(updateData, toSignOutPks);
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
		//校验并组装签到数据
        UserSignRecord signInData = new UserSignRecord();
        result = this.checkAndFillSignInInfo(signInRequest,signInData,gridResult.getData());
        if(!result.isSucceed()) {
        	return result;
        }

		UserSignRecordQuery lastSignRecordQuery = new UserSignRecordQuery();
		lastSignRecordQuery.setUserCode(signInRequest.getUserCode());
		//查询签到记录，先签退
        UserSignRecord lastSignRecord = this.queryLastUnSignOutRecord(lastSignRecordQuery);
        UserSignRecord signOutRequest = new UserSignRecord();
        boolean needSignOut = false;
        if(lastSignRecord != null) {
        	signOutRequest.setId(lastSignRecord.getId());
        	signOutRequest.setUpdateUser(signInRequest.getOperateUserCode());
        	signOutRequest.setUpdateUserName(signInRequest.getOperateUserName());
    		this.doSignOut(signOutRequest);
    		needSignOut = true;
    		context.signOutData = this.toUserSignRecordData(lastSignRecord);
    		context.signOutData.setSignOutTime(signOutRequest.getSignOutTime());
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

		UserSignRecord signOutData = new UserSignRecord();
		if(signOutRequest.getRecordId() != null) {
			signOutData.setId(signOutRequest.getRecordId());
			UserSignRecordData lastSignRecord = queryUserSignRecordDataById(signOutRequest.getRecordId());
			if(lastSignRecord == null || lastSignRecord.getSignOutTime() != null) {
				result.toFail("签到数据无效|已签退！");
				return result;
			}
		}else {
			UserSignRecordQuery lastSignRecordQuery = new UserSignRecordQuery();
			lastSignRecordQuery.setUserCode(signOutRequest.getUserCode());
			
            UserSignRecord lastSignRecord = queryLastUnSignOutRecord(lastSignRecordQuery);
            if(lastSignRecord == null) {
            	result.toFail("该用户未签到，无法签退！");
				return result;
			}
			signOutData.setId(lastSignRecord.getId());
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
		UserSignRecordQuery lastSignRecordQuery = new UserSignRecordQuery();
		lastSignRecordQuery.setUserCode(userSignRequest.getUserCode());
		//查询签到记录，自动签退
        UserSignRecord lastSignRecord = this.queryLastUnSignOutRecord(lastSignRecordQuery);
        
        boolean needSignIn = true;
        boolean needSignOut = false;
        
        if(lastSignRecord != null) {
        	needSignOut = true;
        	//上次签到岗位和本次相同，不需要签到
            if(gridInfo.getBusinessKey() != null
            		&& gridInfo.getBusinessKey().equals(lastSignRecord.getRefGridKey())) {
            	needSignIn = false;
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
        	UserSignRecord signOutData = new UserSignRecord();
        	signOutData.setId(lastSignRecord.getId());
        	signOutData.setUpdateUser(userSignRequest.getOperateUserCode());
        	signOutData.setUpdateUserName(userSignRequest.getOperateUserName());
            this.doSignOut(signOutData);
            //不需要签到，直接返回签退结果
            if(!needSignIn) {
            	result.setData(this.queryUserSignRecordDataById(signOutData.getId()));
                result.toSucceed("签退成功！");
                context.signOutData = result.getData();
        		return result;
            }else {
            	context.signOutData = this.toUserSignRecordData(lastSignRecord);
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
			signRequest.setJobCode(BusinessUtil.getJobCodeFromScanUserCode(scanUserCode));
			signRequest.setUserCode(BusinessUtil.getUserCodeFromScanUserCode(scanUserCode));
			return result;
		}else if(StringHelper.isEmpty(signRequest.getUserCode())) {
			result.toFail("用户编码不能为空！");
			return result;
		}
		if (signRequest.getUserCode().contains("$")) {
			result.toFail("用户编码不能包含特殊字符");
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
		signInData.setUserName(signInData.getUserCode());
		signInData.setModeType(signInRequest.getModeType());
		// 计算班次
		Integer waveCode = calculateWave(signInRequest);
		signInData.setWaveCode(waveCode);
		signInData.setWaveName(WaveTypeEnum.getNameByCode(waveCode));
		signInData.setRefPlanKey(queryPlanKey(signInData));
		setWarZoneInfo(signInData);
		
		Integer jobCode = signInData.getJobCode();
		String userCode = signInData.getUserCode();
		boolean isCarId = BusinessUtil.isIdCardNo(userCode);
		if(JobTypeEnum.JOBTYPE1.getCode().equals(jobCode)
				||JobTypeEnum.JOBTYPE2.getCode().equals(jobCode)) {
			//正式工设置erp对应的名称
			BaseStaffSiteOrgDto userInfo = baseMajorManager.getBaseStaffIgnoreIsResignByErp(signInData.getUserCode());
			boolean isEffectErp = false;
			if(userInfo != null
					&& Constants.FLAG_USER_Is_Resign.equals(userInfo.getIsResign())) {
				isEffectErp = true;
			}
			if(!isEffectErp) {
				if(JobTypeEnum.JOBTYPE1.getCode().equals(jobCode)
							||JobTypeEnum.JOBTYPE2.getCode().equals(jobCode)) {
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
		return result;
	}

	private void setWarZoneInfo(UserSignRecord signInData) {
		if(signInData.getSiteCode() == null){
			return;
		}
		BaseSite baseSite = baseMajorManager.getSiteBySiteCode(signInData.getSiteCode());
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
		return null;
	}

	private boolean doSignIn(UserSignRecord userSignRecord) {
		Date date = new Date();
		userSignRecord.setCreateTime(date);
		userSignRecord.setSignInTime(date);
		userSignRecord.setSignDate(date);
		return userSignRecordDao.insert(userSignRecord) == 1;
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
		data.setUserName(BusinessUtil.encryptIdCard(data.getUserName()));
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
		result.getData().setGroupData(context.groupData);
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
		UserSignRecordData data = queryUserSignRecordDataById(userSignRequest.getRecordId());
		if(data == null) {
			result.toFail("签到数据无效|已作废！");
			return result;
		}
		UserSignRecord deleteData = new UserSignRecord();
		deleteData.setId(data.getId());
		deleteData.setUpdateTime(new Date());
		deleteData.setUpdateUser(userSignRequest.getOperateUserCode());
		deleteData.setUpdateUserName(userSignRequest.getOperateUserName());
		this.deleteById(deleteData);
		context.deleteData = data;
		data.setYn(Constants.YN_NO);
		result.setData(data);
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
}
