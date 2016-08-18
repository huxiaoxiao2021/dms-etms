package com.jd.bluedragon.distribution.cross.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.cross.domain.CrossSorting;

public class CrossSortingReadDao extends BaseDao<CrossSorting> {

	private final Log logger = LogFactory.getLog(this.getClass());

	public static final String namespace = CrossSortingReadDao.class.getName();

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CrossSorting> findPageCrossSorting(Map<String, Object> params) {
		logger.info("CrossSortingReadDao.findPageCrossSorting begin...");
		return super.getSqlSessionRead().selectList(CrossSortingReadDao.namespace + ".findPageTask", params);
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Integer findCountCrossSorting(Map<String, Object> params) {
		logger.info("CrossSortingReadDao.findCountCrossSorting begin...");
		return (Integer) super.getSqlSessionRead().selectOne(CrossSortingReadDao.namespace + ".findCountTask", params);
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CrossSorting> findMixDms(Map<String, Object> params) {
		logger.info("CrossSortingReadDao.findMixDms begin...");
		return super.getSqlSessionRead().selectList(CrossSortingReadDao.namespace + ".findMixDms", params);
	}
	
    @SuppressWarnings("unchecked")
    public List<CrossSorting> findOne(Map<String, Object> params) {
        logger.info("CrossSortingReadDao.findOne begin...");
        return super.getSqlSessionRead().selectList(CrossSortingReadDao.namespace + ".findOne", params);
    }
}
