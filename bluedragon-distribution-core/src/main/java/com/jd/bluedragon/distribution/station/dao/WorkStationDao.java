package com.jd.bluedragon.distribution.station.dao;

import java.util.List;

import com.jd.bluedragon.distribution.station.domain.WorkStation;
import com.jd.bluedragon.distribution.station.query.WorkStationQuery;


/**
 * @ClassName: WorkStationDao
 * @Description: 网格工序信息表--Dao接口
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
public interface WorkStationDao {
	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	int insert(WorkStation insertData);
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	int updateById(WorkStation updateData);
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	int deleteById(WorkStation deleteData);
	/**
	 * 根据业务主键删除
	 * @param id
	 * @return
	 */
	int deleteByBusinessKey(WorkStation data);	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	WorkStation queryById(Long id);
	/**
	 * 根据业务主键查询
	 * @param id
	 * @return
	 */
	WorkStation queryByBusinessKey(WorkStation data);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	List<WorkStation> queryList(WorkStationQuery query);
	/**
	 * 按条件查询数量
	 * @param query
	 * @return
	 */
	Long queryCount(WorkStationQuery query);
	/**
	 * 查询作业区字典
	 * @return
	 */
	List<WorkStation> queryAreaDictList(WorkStationQuery query);	
	/**
	 * 查询工序字典
	 * @return
	 */
	List<WorkStation> queryWorkDictList(WorkStationQuery query);
}
