package com.jd.bluedragon.distribution.station.jsf.impl;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.base.ResultHandler;
import com.jd.bluedragon.distribution.lock.LockService;
import com.jd.bluedragon.distribution.station.api.WorkStationAttendPlanJsfService;
import com.jd.bluedragon.distribution.station.domain.DeleteRequest;
import com.jd.bluedragon.distribution.station.domain.WorkStationAttendPlan;
import com.jd.bluedragon.distribution.station.query.WorkStationAttendPlanQuery;
import com.jd.bluedragon.distribution.station.service.WorkStationAttendPlanService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 岗位人员出勤计划表--JsfService接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Slf4j
@Service("workStationAttendPlanJsfService")
public class WorkStationAttendPlanJsfServiceImpl implements WorkStationAttendPlanJsfService {
	private static final Logger logger = LoggerFactory.getLogger(WorkStationAttendPlanJsfServiceImpl.class);
	@Autowired
	@Qualifier("workStationAttendPlanService")
	private WorkStationAttendPlanService workStationAttendPlanService;
	
    @Autowired
    @Qualifier("jimdbRemoteLockService")
    private LockService lockService;
	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(final WorkStationAttendPlan insertData){
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_ATTEND_PLAN_EDIT,DateHelper.ONE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationAttendPlanService.insert(insertData);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改网格计划信息，请稍后操作！");
			}

			@Override
			public void error(Exception e) {
				logger.error(e.getMessage(), e);
				result.toFail("操作异常，请稍后重试！");
			}
		});
		return result;		
	 }
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public Result<Boolean> updateById(final WorkStationAttendPlan updateData){
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_ATTEND_PLAN_EDIT,DateHelper.ONE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationAttendPlanService.updateById(updateData);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改网格计划信息，请稍后操作！");
			}

			@Override
			public void error(Exception e) {
				logger.error(e.getMessage(), e);
				result.toFail("操作异常，请稍后重试！");
			}
		});
		return result;			
	 }
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	public Result<Boolean> deleteById(final WorkStationAttendPlan deleteData){
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_ATTEND_PLAN_EDIT,DateHelper.ONE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationAttendPlanService.deleteById(deleteData);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改网格计划信息，请稍后操作！");
			}

			@Override
			public void error(Exception e) {
				logger.error(e.getMessage(), e);
				result.toFail("操作异常，请稍后重试！");
			}
		});
		return result;		
	 }
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Result<WorkStationAttendPlan> queryById(Long id){
		return workStationAttendPlanService.queryById(id);
	 }
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public Result<PageDto<WorkStationAttendPlan>> queryPageList(WorkStationAttendPlanQuery query){
		return workStationAttendPlanService.queryPageList(query);
	 }
	@Override
	public Result<Boolean> importDatas(final List<WorkStationAttendPlan> dataList) {
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_ATTEND_PLAN_EDIT,DateHelper.FIVE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationAttendPlanService.importDatas(dataList);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改网格计划信息，请稍后操作！");
			}

			@Override
			public void error(Exception e) {
				logger.error(e.getMessage(), e);
				result.toFail("操作异常，请稍后重试！");
			}
		});
		return result;		
	}
	@Override
	public Result<List<WorkStationAttendPlan>> queryWaveDictList(WorkStationAttendPlanQuery query) {
		return workStationAttendPlanService.queryWaveDictList(query);
	}
	@Override
	public Result<Boolean> deleteByIds(final DeleteRequest<WorkStationAttendPlan> deleteRequest) {
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_ATTEND_PLAN_EDIT,DateHelper.FIVE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationAttendPlanService.deleteByIds(deleteRequest);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改网格计划信息，请稍后操作！");
			}

			@Override
			public void error(Exception e) {
				logger.error(e.getMessage(), e);
				result.toFail("操作异常，请稍后重试！");
			}
		});
		return result;		
	}
	@Override
	public Result<Long> queryCount(WorkStationAttendPlanQuery query) {
		return workStationAttendPlanService.queryCount(query);
	}
	@Override
	public Result<List<WorkStationAttendPlan>> queryListForExport(WorkStationAttendPlanQuery query) {
		return workStationAttendPlanService.queryListForExport(query);
	}

}
