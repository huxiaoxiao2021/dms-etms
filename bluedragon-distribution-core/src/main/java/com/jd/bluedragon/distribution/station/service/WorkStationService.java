package com.jd.bluedragon.distribution.station.service;

import java.util.List;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.domain.WorkStation;
import com.jd.bluedragon.distribution.station.domain.WorkStationCountVo;
import com.jd.bluedragon.distribution.station.query.WorkStationQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * @ClassName: WorkStationService
 * @Description: 网格工序信息表--Service接口
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
public interface WorkStationService {

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	Result<Boolean> insert(WorkStation insertData);
	/**
	 * 导入数据
	 * @param dataList
	 * @return
	 */
	Result<Boolean> importDatas(List<WorkStation> dataList);	
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	Result<Boolean> updateById(WorkStation updateData);
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	Result<Boolean> deleteById(WorkStation deleteData);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	Result<WorkStation> queryById(Long id);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	Result<PageDto<WorkStation>> queryPageList(WorkStationQuery query);
	/**
	 * 查询作业区字典
	 * @return
	 */
	Result<List<WorkStation>> queryAreaDictList(WorkStationQuery query);
	/**
	 * 查询工序字典
	 * @return
	 */
	Result<List<WorkStation>> queryWorkDictList(WorkStationQuery query);
	/**
	 * 根据业务主键查询
	 * @param data
	 * @return
	 */
	Result<WorkStation> queryByBusinessKey(WorkStation data);
	/**
	 * 根据业务主键判断是否存在
	 * @param data
	 * @return
	 */
	boolean isExist(WorkStation data);
	/**
	 * 按条件查询统计信息
	 * @param query
	 * @return
	 */
	Result<WorkStationCountVo> queryPageCount(WorkStationQuery query);
}
