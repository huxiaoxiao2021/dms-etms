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
import com.jd.bluedragon.distribution.station.api.WorkStationGridJsfService;
import com.jd.bluedragon.distribution.station.domain.DeleteRequest;
import com.jd.bluedragon.distribution.station.domain.WorkStationGrid;
import com.jd.bluedragon.distribution.station.domain.WorkStationGridCountVo;
import com.jd.bluedragon.distribution.station.query.WorkStationGridQuery;
import com.jd.bluedragon.distribution.station.service.WorkStationGridService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 工序岗位网格表--JsfService接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Slf4j
@Service("workStationGridJsfService")
public class WorkStationGridJsfServiceImpl implements WorkStationGridJsfService {
	private static final Logger logger = LoggerFactory.getLogger(WorkStationGridJsfServiceImpl.class);
	@Autowired
	@Qualifier("workStationGridService")
	private WorkStationGridService workStationGridService;
    @Autowired
    @Qualifier("jimdbRemoteLockService")
    private LockService lockService;	

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(final WorkStationGrid insertData){
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_GRID_EDIT,DateHelper.ONE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationGridService.insert(insertData);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改网格信息，请稍后操作！");
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
	public Result<Boolean> updateById(final WorkStationGrid updateData){
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_GRID_EDIT,DateHelper.ONE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationGridService.updateById(updateData);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改网格信息，请稍后操作！");
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
	public Result<Boolean> deleteById(final WorkStationGrid deleteData){
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_GRID_EDIT,DateHelper.ONE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationGridService.deleteById(deleteData);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改网格信息，请稍后操作！");
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
	public Result<WorkStationGrid> queryById(Long id){
		return workStationGridService.queryById(id);
	 }
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public Result<PageDto<WorkStationGrid>> queryPageList(WorkStationGridQuery query){
		return workStationGridService.queryPageList(query);
	 }
	/**
	 * 按条件查询统计信息
	 * @param query
	 * @return
	 */	
	@Override	
	public Result<WorkStationGridCountVo> queryPageCount(WorkStationGridQuery query){
		return workStationGridService.queryPageCount(query);
	 }	
	@Override
	public Result<List<WorkStationGrid>> queryAllGridBySiteCode(WorkStationGridQuery query) {
		return workStationGridService.queryAllGridBySiteCode(query);
	}
	@Override
	public Result<Boolean> importDatas(final List<WorkStationGrid> dataList) {
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_GRID_EDIT,DateHelper.FIVE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationGridService.importDatas(dataList);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改网格信息，请稍后操作！");
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
	public Result<List<WorkStationGrid>> queryGridWorkDictList(WorkStationGridQuery query) {
		return workStationGridService.queryGridWorkDictList(query);
	}
	@Override
	public Result<List<WorkStationGrid>> queryGridAreaDictList(WorkStationGridQuery query) {
		return workStationGridService.queryGridAreaDictList(query);
	}
	@Override
	public Result<List<WorkStationGrid>> queryGridNoDictList(WorkStationGridQuery query) {
		return workStationGridService.queryGridNoDictList(query);
	}
	@Override
	public Result<List<WorkStationGrid>> queryGridFloorDictList(WorkStationGridQuery query) {
		return workStationGridService.queryGridFloorDictList(query);
	}
	@Override
	public Result<Boolean> deleteByIds(final DeleteRequest<WorkStationGrid> deleteRequest) {
		final Result<Boolean> result = Result.success();
		lockService.tryLock(CacheKeyConstants.CACHE_KEY_WORK_STATION_GRID_EDIT,DateHelper.FIVE_MINUTES_MILLI, new ResultHandler() {
			@Override
			public void success() {
				Result<Boolean> apiResult = workStationGridService.deleteByIds(deleteRequest);
				if(!apiResult.isSuccess()) {
					result.setCode(apiResult.getCode());
					result.setMessage(apiResult.getMessage());
					result.setData(apiResult.getData());
					return ;
				}
			}
			@Override
			public void fail() {
				result.toFail("其他用户正在修改网格信息，请稍后操作！");
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
	public Result<Long> queryCount(WorkStationGridQuery query) {
		return workStationGridService.queryCount(query);
	}
	@Override
	public Result<List<WorkStationGrid>> queryListForExport(WorkStationGridQuery query) {
		return workStationGridService.queryListForExport(query);
	}
	@Override
	public List<String> queryOwnerUserErpListBySiteCode(WorkStationGridQuery query) {
		return workStationGridService.queryOwnerUserErpListBySiteCode(query);
	}
	@Override
	public List<WorkStationGrid> querySiteListByOrgCode(WorkStationGridQuery query) {
		return workStationGridService.querySiteListByOrgCode(query);
	}
}
