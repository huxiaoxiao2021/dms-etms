package com.jd.bluedragon.distribution.station.dao.impl;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.station.dao.WorkStationGridDao;
import com.jd.bluedragon.distribution.station.domain.WorkStationGrid;
import com.jd.bluedragon.distribution.station.domain.WorkStationGridCountVo;
import com.jd.bluedragon.distribution.station.query.WorkStationGridQuery;

/**
 * 工序岗位网格表--Dao接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public class WorkStationGridDaoImpl extends BaseDao<WorkStationGrid> implements WorkStationGridDao {
	
	private final static String NAMESPACE = WorkStationGridDao.class.getName();
	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public int insert(WorkStationGrid insertData){
		return this.getSqlSession().insert(NAMESPACE+".insert",insertData);
	}
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public int updateById(WorkStationGrid updateData){
		return this.getSqlSession().update(NAMESPACE+".updateById",updateData);
	}
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	public int deleteById(WorkStationGrid deleteData){;
		return this.getSqlSession().update(NAMESPACE+".deleteById",deleteData);
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public WorkStationGrid queryById(Long id){
		return this.getSqlSession().selectOne(NAMESPACE+".queryById",id);
	}
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public List<WorkStationGrid> queryList(WorkStationGridQuery query){
		return this.getSqlSession().selectList(NAMESPACE+".queryList",query);
	}
	/**
	 * 按条件查询数量
	 * @param query
	 * @return
	 */
	public long queryCount(WorkStationGridQuery query){
		return this.getSqlSession().selectOne(NAMESPACE+".queryCount",query);
	}
	@Override
	public List<WorkStationGrid> queryAllGridBySiteCode(WorkStationGridQuery query) {
		return this.getSqlSession().selectList(NAMESPACE+".queryAllGridBySiteCode",query);
	}
	@Override
	public int deleteByBusinessKey(WorkStationGrid deleteData) {
		return this.getSqlSession().update(NAMESPACE+".deleteByBusinessKey",deleteData);
	}
	@Override
	public WorkStationGrid queryByBusinessKey(WorkStationGrid data) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryByBusinessKey",data);
	}
	@Override
	public List<WorkStationGrid> queryGridWorkDictList(WorkStationGridQuery query) {
		return this.getSqlSession().selectList(NAMESPACE+".queryGridWorkDictList",query);
	}
	@Override
	public List<WorkStationGrid> queryGridAreaDictList(WorkStationGridQuery query) {
		return this.getSqlSession().selectList(NAMESPACE+".queryGridAreaDictList",query);
	}
	@Override
	public List<WorkStationGrid> queryGridNoDictList(WorkStationGridQuery query) {
		return this.getSqlSession().selectList(NAMESPACE+".queryGridNoDictList",query);
	}
	@Override
	public List<WorkStationGrid> queryGridFloorDictList(WorkStationGridQuery query) {
		return this.getSqlSession().selectList(NAMESPACE+".queryGridFloorDictList",query);
	}
	@Override
	public WorkStationGridCountVo queryPageCount(WorkStationGridQuery query) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryPageCount",query);
	}
	@Override
	public long queryCountByRefStationKey(String stationKey) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryCountByRefStationKey",stationKey);
	}
	@Override
	public WorkStationGrid queryByGridKey(WorkStationGridQuery workStationGridQuery) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryByGridKey",workStationGridQuery);
	}

	@Override
	public List<WorkStationGrid> queryAllByPage(WorkStationGridQuery query) {
		return this.getSqlSession().selectList(NAMESPACE+".queryAllByPage",query);
	}

}
