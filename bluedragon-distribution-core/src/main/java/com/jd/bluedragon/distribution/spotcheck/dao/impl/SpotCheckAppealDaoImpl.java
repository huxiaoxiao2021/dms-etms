package com.jd.bluedragon.distribution.spotcheck.dao.impl;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.spotcheck.dao.SpotCheckAppealDao;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckAppealEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备抽检申诉
 */
@Repository("spotCheckAppealDao")
public class SpotCheckAppealDaoImpl extends BaseDao<SpotCheckAppealEntity> implements SpotCheckAppealDao {

    private final static String NAMESPACE = SpotCheckAppealDao.class.getName();

	/**
	 * 插入一条数据
	 */
	public int insertRecord(SpotCheckAppealEntity spotCheckAppealEntity) {
		return this.getSqlSession().insert(NAMESPACE + ".insertRecord", spotCheckAppealEntity);
	}

	/**
	 * 根据id更新数据
	 */
	public int updateById(SpotCheckAppealEntity spotCheckAppealEntity) {
		return this.getSqlSession().update(NAMESPACE + ".updateById", spotCheckAppealEntity);
	}

	/**
	 * 根据id列表批量更新
	 */
	public int batchUpdateByIds(SpotCheckAppealEntity spotCheckAppealEntity) {
		return this.getSqlSession().update(NAMESPACE + ".batchUpdateByIds", spotCheckAppealEntity);
	}

	/**
	 * 根据条件统计总数
	 */
	public int countByCondition(SpotCheckAppealEntity spotCheckAppealEntity) {
		return this.getSqlSession().selectOne(NAMESPACE + ".countByCondition", spotCheckAppealEntity);
	}

	/**
	 * 按条件分页查询
	 */
	public List<SpotCheckAppealEntity> findByCondition(SpotCheckAppealEntity spotCheckAppealEntity) {
	    return this.getSqlSession().selectList(NAMESPACE + ".findByCondition", spotCheckAppealEntity);
	}

}
