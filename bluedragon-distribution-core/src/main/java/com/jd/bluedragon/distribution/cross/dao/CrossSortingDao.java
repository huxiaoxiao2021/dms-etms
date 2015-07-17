package com.jd.bluedragon.distribution.cross.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.cross.domain.CrossSorting;

public class CrossSortingDao extends BaseDao<CrossSorting> {

	private final Log logger = LogFactory.getLog(this.getClass());

	public static final String namespace = CrossSortingDao.class.getName();

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public int deleteCrossSorting(Map<String, Object> params) {
		logger.info("CrossSortingDao.deleteCrossSorting begin...");
		return super.getSqlSession().delete(CrossSortingDao.namespace + ".delete", params);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public int addBatchCrossSorting(List<CrossSorting> csList) {
		logger.info("CrossSortingDao.addBatchCrossSorting begin...");
		return super.getSqlSession().insert(CrossSortingDao.namespace + ".addBatch", csList);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
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
		logger.info("CrossSortingDao.updateCrossSortingForDelete begin...");
		return super.getSqlSession().insert(CrossSortingDao.namespace + ".updateForDelete", cs);
	}

}
