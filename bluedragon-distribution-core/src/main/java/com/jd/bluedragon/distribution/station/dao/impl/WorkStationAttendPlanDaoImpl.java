package com.jd.bluedragon.distribution.station.dao.impl;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.station.dao.WorkStationAttendPlanDao;
import com.jd.bluedragon.distribution.station.domain.DeleteRequest;
import com.jd.bluedragon.distribution.station.domain.WorkStationAttendPlan;
import com.jd.bluedragon.distribution.station.query.WorkStationAttendPlanQuery;

/**
 * 岗位人员出勤计划表--Dao接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public class WorkStationAttendPlanDaoImpl extends BaseDao<WorkStationAttendPlan> implements WorkStationAttendPlanDao {

    private final static String NAMESPACE = WorkStationAttendPlanDao.class.getName();
    
	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public int insert(WorkStationAttendPlan insertData){
		return this.getSqlSession().insert(NAMESPACE+".insert",insertData);
	}
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public int updateById(WorkStationAttendPlan updateData){
		return this.getSqlSession().update(NAMESPACE+".updateById",updateData);
	}
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	public int deleteById(WorkStationAttendPlan deleteData){;
		return this.getSqlSession().update(NAMESPACE+".deleteById",deleteData);
	}
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public WorkStationAttendPlan queryById(Long id){
		return this.getSqlSession().selectOne(NAMESPACE+".queryById",id);
	}
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public List<WorkStationAttendPlan> queryList(WorkStationAttendPlanQuery query){
	    return this.getSqlSession().selectList(NAMESPACE+".queryList",query);
	}
	/**
	 * 按条件查询数量
	 * @param query
	 * @return
	 */
	public long queryCount(WorkStationAttendPlanQuery query){
	    return this.getSqlSession().selectOne(NAMESPACE+".queryCount",query);
	}
	@Override
	public int deleteByBusinessKey(WorkStationAttendPlan data) {
		return this.getSqlSession().update(NAMESPACE+".deleteByBusinessKey",data);
	}
	@Override
	public WorkStationAttendPlan queryByBusinessKey(WorkStationAttendPlan data) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryByBusinessKey",data);
	}
	@Override
	public List<WorkStationAttendPlan> queryWaveDictList(WorkStationAttendPlanQuery query) {
	    return this.getSqlSession().selectList(NAMESPACE+".queryWaveDictList",query);
	}
	@Override
	public List<WorkStationAttendPlan> queryListForExport(WorkStationAttendPlanQuery query) {
		return this.getSqlSession().selectList(NAMESPACE+".queryListForExport",query);
	}
	@Override
	public List<WorkStationAttendPlan> queryByIds(DeleteRequest<WorkStationAttendPlan> deleteRequest) {
		return this.getSqlSession().selectList(NAMESPACE+".queryByIds",deleteRequest);
	}
	@Override
	public int deleteByIds(DeleteRequest<WorkStationAttendPlan> deleteRequest) {
		return this.getSqlSession().update(NAMESPACE+".deleteByIds",deleteRequest);
	}
}
