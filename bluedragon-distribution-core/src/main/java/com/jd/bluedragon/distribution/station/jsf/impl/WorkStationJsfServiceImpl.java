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
import com.jd.bluedragon.distribution.station.api.WorkStationJsfService;
import com.jd.bluedragon.distribution.station.domain.DeleteRequest;
import com.jd.bluedragon.distribution.station.domain.WorkStation;
import com.jd.bluedragon.distribution.station.domain.WorkStationCountVo;
import com.jd.bluedragon.distribution.station.query.WorkStationQuery;
import com.jd.bluedragon.distribution.station.service.WorkStationService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * @ClassName: WorkStationServiceImpl
 * @Description: 网格工序信息表--Service接口实现
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
@Service("workStationJsfService")
public class WorkStationJsfServiceImpl implements WorkStationJsfService {

	private static final Logger logger = LoggerFactory.getLogger(WorkStationJsfServiceImpl.class);

	@Autowired
	@Qualifier("workStationService")
	private WorkStationService workStationService;
	
    @Autowired
    @Qualifier("jimdbRemoteLockService")
    private LockService lockService;

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(final WorkStation insertData){
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_EDIT,DateHelper.ONE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationService.insert(insertData);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改工序信息，请稍后操作！");
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
	public Result<Boolean> importDatas(final List<WorkStation> dataList) {
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_EDIT,DateHelper.FIVE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationService.importDatas(dataList);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改工序信息，请稍后操作！");
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
	public Result<Boolean> updateById(final WorkStation updateData){
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_EDIT,DateHelper.ONE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationService.updateById(updateData);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改工序信息，请稍后操作！");
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
	public Result<Boolean> deleteById(final WorkStation deleteData){
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_EDIT,DateHelper.ONE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationService.deleteById(deleteData);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改工序信息，请稍后操作！");
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
	public Result<WorkStation> queryById(Long id){
		return workStationService.queryById(id);
	 }
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public Result<PageDto<WorkStation>> queryPageList(WorkStationQuery query){
		return workStationService.queryPageList(query);
	 }
	/**
	 * 按条件查询统计信息
	 * @param query
	 * @return
	 */	
	@Override	
	public Result<WorkStationCountVo> queryPageCount(WorkStationQuery query){
		return workStationService.queryPageCount(query);
	 }
	@Override
	public Result<List<WorkStation>> queryAreaDictList(WorkStationQuery query) {
		return workStationService.queryAreaDictList(query);
	}
	@Override
	public Result<List<WorkStation>> queryWorkDictList(WorkStationQuery query) {
		return workStationService.queryWorkDictList(query);
	}
	@Override
	public Result<Boolean> deleteByIds(final DeleteRequest<WorkStation> deleteRequest) {
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_EDIT,DateHelper.ONE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationService.deleteByIds(deleteRequest);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改工序信息，请稍后操作！");
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
	public Result<Long> queryCount(WorkStationQuery query) {
		return workStationService.queryCount(query);
	}
	@Override
	public Result<List<WorkStation>> queryListForExport(WorkStationQuery query) {
		return workStationService.queryListForExport(query);
	}
}
