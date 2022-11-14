package com.jd.bluedragon.distribution.station.jsf.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.api.UserSignRecordJsfService;
import com.jd.bluedragon.distribution.station.domain.UserSignNoticeVo;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportSumVo;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportVo;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
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
@Service("userSignRecordJsfService")
public class UserSignRecordJsfServiceImpl implements UserSignRecordJsfService {

	@Autowired
	@Qualifier("userSignRecordService")
	private UserSignRecordService userSignRecordService;

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(UserSignRecord insertData){
		return userSignRecordService.insert(insertData);
	 }
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public Result<Boolean> updateById(UserSignRecord updateData){
		return userSignRecordService.updateById(updateData);
	 }
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	public Result<Boolean> deleteById(UserSignRecord deleteData){
		return userSignRecordService.deleteById(deleteData);
	 }
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Result<UserSignRecord> queryById(Long id){
		return userSignRecordService.queryById(id);
	 }
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	@JProfiler(jKey = "dmsWeb.server.userSignRecordJsfService.queryPageList",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public Result<PageDto<UserSignRecord>> queryPageList(UserSignRecordQuery query){
		return userSignRecordService.queryPageList(query);
	 }
	@Override
	public Result<PageDto<UserSignRecordReportVo>> queryReportPageList(UserSignRecordQuery query) {
		return userSignRecordService.queryReportPageList(query);
	}
	@Override
	public Result<Boolean> signIn(UserSignRecord signInRequest) {
		return userSignRecordService.signIn(signInRequest);
	}
	@Override
	public Result<Boolean> signOut(UserSignRecord signOutRequest) {
		return userSignRecordService.signOut(signOutRequest);
	}
	@Override
	public Result<UserSignRecord> queryLastSignRecord(UserSignRecordQuery query) {
		return userSignRecordService.queryLastSignRecord(query);		
	}
	@Override
	public Result<UserSignRecordReportSumVo> queryReportSum(UserSignRecordQuery query) {
		return userSignRecordService.queryReportSum(query);
	}
	@Override
	public Result<Long> queryCount(UserSignRecordQuery query) {
		return userSignRecordService.queryCount(query);
	}
	@JProfiler(jKey = "dmsWeb.server.userSignRecordJsfService.queryListForExport",
			jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})	
	@Override
	public Result<List<UserSignRecord>> queryListForExport(UserSignRecordQuery query) {
		return userSignRecordService.queryListForExport(query);
	}
	@Override
	public Result<UserSignNoticeVo> queryUserSignRecordToNoticeVo(UserSignRecordQuery query) {
		return userSignRecordService.queryUserSignRecordToNoticeVo(query);
	}

	@Override
	public Result<List<UserSignRecord>> queryUnsignedOutRecordByRefGridKey(String refGridKey) {
		return userSignRecordService.queryUnsignedOutRecordByRefGridKey(refGridKey);
	}

}
