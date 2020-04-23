package com.jd.bluedragon.distribution.cross.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.cross.domain.CrossSorting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public class CrossSortingDao extends BaseDao<CrossSorting> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final String namespace = CrossSortingDao.class.getName();

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public int deleteCrossSorting(Map<String, Object> params) {
		log.info("CrossSortingDao.deleteCrossSorting begin...");
		return super.getSqlSession().delete(CrossSortingDao.namespace + ".delete", params);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public int addBatchCrossSorting(List<CrossSorting> csList) {
		log.info("CrossSortingDao.addBatchCrossSorting begin...");
		return super.getSqlSession().insert(CrossSortingDao.namespace + ".addBatch", csList);
	}

	public CrossSorting findCrossSorting(CrossSorting crossSorting) {
		List<CrossSorting> crossSortings = super.getSqlSession().selectList(CrossSortingDao.namespace + ".findCrossSorting", crossSorting);
		if (null == crossSortings || crossSortings.size() <= 0)
			return null;
		return crossSortings.get(0);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public int updateCrossSorting(CrossSorting crossSorting) {
		return super.getSqlSession().update(CrossSortingDao.namespace + ".updateCrossSorting", crossSorting);
	}

	public int updateCrossSortingForDelete(CrossSorting cs) {
		log.info("CrossSortingDao.updateCrossSortingForDelete begin...");
		return super.getSqlSession().insert(CrossSortingDao.namespace + ".updateForDelete", cs);
	}

	@SuppressWarnings("unchecked")
	public List<CrossSorting> findPageCrossSorting(Map<String, Object> params) {
		log.info("CrossSortingReadDao.findPageCrossSorting begin...");
		return super.getSqlSession().selectList(CrossSortingDao.namespace + ".findPageTask", params);
	}

	public Integer findCountCrossSorting(Map<String, Object> params) {
		log.info("CrossSortingReadDao.findCountCrossSorting begin...");
		return (Integer) super.getSqlSession().selectOne(CrossSortingDao.namespace + ".findCountTask", params);
	}

	@SuppressWarnings("unchecked")
	public List<CrossSorting> findMixDms(Map<String, Object> params) {
		log.info("CrossSortingReadDao.findMixDms begin...");
		return super.getSqlSession().selectList(CrossSortingDao.namespace + ".findMixDms", params);
	}
	
    @SuppressWarnings("unchecked")
    public List<CrossSorting> findOne(Map<String, Object> params) {
        log.info("CrossSortingReadDao.findOne begin...");
        return super.getSqlSession().selectList(CrossSortingDao.namespace + ".findOne", params);
    }
}
