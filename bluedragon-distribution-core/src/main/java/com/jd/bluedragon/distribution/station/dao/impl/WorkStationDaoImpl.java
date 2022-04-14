package com.jd.bluedragon.distribution.station.dao.impl;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.station.dao.WorkStationDao;
import com.jd.bluedragon.distribution.station.domain.DeleteRequest;
import com.jd.bluedragon.distribution.station.domain.WorkStation;
import com.jd.bluedragon.distribution.station.domain.WorkStationCountVo;
import com.jd.bluedragon.distribution.station.query.WorkStationQuery;

/**
 * @ClassName: WorkStationDaoImpl
 * @Description: 网格工序信息表--Dao接口实现
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
public class WorkStationDaoImpl extends BaseDao<WorkStation> implements WorkStationDao{

    private final static String NAMESPACE = WorkStationDao.class.getName();
	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public int insert(WorkStation insertData){
		return this.getSqlSession().insert(NAMESPACE+".insert",insertData);
	}
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public int updateById(WorkStation updateData){
		return this.getSqlSession().update(NAMESPACE+".updateById",updateData);
	}
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	public int deleteById(WorkStation deleteData){;
		return this.getSqlSession().update(NAMESPACE+".deleteById",deleteData);
	}
	@Override
	public int deleteByBusinessKey(WorkStation deleteData) {
		return this.getSqlSession().update(NAMESPACE+".deleteByBusinessKey",deleteData);
	}	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public WorkStation queryById(Long id){
		return this.getSqlSession().selectOne(NAMESPACE+".queryById",id);
	}
	@Override
	public WorkStation queryByBusinessKey(WorkStation data) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryByBusinessKey",data);
	}	
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public List<WorkStation> queryList(WorkStationQuery query){
	    return this.getSqlSession().selectList(NAMESPACE+".queryList",query);
	}
	/**
	 * 按条件查询数量
	 * @param query
	 * @return
	 */
	public Long queryCount(WorkStationQuery query){
	    return this.getSqlSession().selectOne(NAMESPACE+".queryCount",query);
	}
	@Override
	public List<WorkStation> queryWorkDictList(WorkStationQuery query) {
		return this.getSqlSession().selectList(NAMESPACE+".queryWorkDictList",query);
	}
	@Override
	public List<WorkStation> queryAreaDictList(WorkStationQuery query) {
		return this.getSqlSession().selectList(NAMESPACE+".queryAreaDictList",query);
	}
	@Override
	public WorkStationCountVo queryPageCount(WorkStationQuery query) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryPageCount",query);
	}
	@Override
	public List<WorkStation> queryListForExport(WorkStationQuery query) {
	    return this.getSqlSession().selectList(NAMESPACE+".queryListForExport",query);
	}
	@Override
	public List<WorkStation> queryByIds(DeleteRequest<WorkStation> deleteRequest) {
		return this.getSqlSession().selectList(NAMESPACE+".queryByIds",deleteRequest);
	}
	@Override
	public int deleteByIds(DeleteRequest<WorkStation> deleteRequest) {
		return this.getSqlSession().update(NAMESPACE+".deleteByIds",deleteRequest);
	}
}
