package com.jd.bluedragon.distribution.station.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordDao;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportSumVo;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportVo;
import com.jd.bluedragon.distribution.station.enums.JobTypeEnum;
import com.jd.bluedragon.distribution.station.enums.WaveTypeEnum;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import lombok.extern.slf4j.Slf4j;

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
	
	@Value("${beans.userSignRecordService.signDateRangeMaxDays:7}")
	private int signDateRangeMaxDays;
	
	private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0.00");
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
		if(totalCount > 0){
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
					return result.toFail("查询日期范围不能超过" + signDateRangeMaxDays + "天");
				}
				query.setSignDateStart(signDateStart);
				query.setSignDateEnd(signDateEnd);
			}else {
				signDate = DateHelper.parseDate(DateHelper.getDateOfyyMMdd2(),DateHelper.DATE_FORMAT_YYYYMMDD);
			}
		}
		query.setSignDate(signDate);
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
//		UserSignRecord data = userSignRecordDao.queryByBusinessKey(signInRequest);
//		if(data != null) {
//			return result.toFail("该用户已签到！");
//		}
		signInRequest.setCreateTime(new Date());
		signInRequest.setSignInTime(new Date());
		userSignRecordDao.insert(signInRequest);
		return result;
	}
	@Override
	public Result<Boolean> signOut(UserSignRecord signOutRequest) {
		Result<Boolean> result = Result.success();

		UserSignRecord data = new UserSignRecord();
		if(signOutRequest.getId() != null) {
			data.setId(signOutRequest.getId());
		}else {
			UserSignRecordQuery query = new UserSignRecordQuery();
			query.setSiteCode(signOutRequest.getSiteCode());
			query.setUserCode(signOutRequest.getUserCode());
			query.setSignDate(signOutRequest.getSignDate());
			UserSignRecord lastSignRecord = userSignRecordDao.queryLastSignRecord(query);
			if(lastSignRecord == null) {
				return result.toFail("该用户未签到，无法签退！");
			}
			data.setId(lastSignRecord.getId());
		}
		data.setUpdateTime(new Date());
		data.setSignOutTime(new Date());
		userSignRecordDao.updateById(data);
		return result;
	}
	@Override
	public Result<UserSignRecord> queryLastSignRecord(UserSignRecordQuery query) {
		Result<UserSignRecord> result = Result.success();
		result.setData(fillOtherInfo(userSignRecordDao.queryLastSignRecord(query)));
		return result;
	}
	@Override
	public Result<UserSignRecordReportSumVo> queryReportSum(UserSignRecordQuery query) {
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		Result<UserSignRecordReportSumVo> result = Result.success();
		result.setData(userSignRecordDao.queryReportSum(query));
		return result;
	}
}
