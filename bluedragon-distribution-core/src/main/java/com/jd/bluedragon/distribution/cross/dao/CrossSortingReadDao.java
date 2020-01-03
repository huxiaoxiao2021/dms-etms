package com.jd.bluedragon.distribution.cross.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.cross.domain.CrossSorting;

public class CrossSortingReadDao extends BaseDao<CrossSorting> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public static final String namespace = CrossSortingReadDao.class.getName();

	@SuppressWarnings("unchecked")
	public List<CrossSorting> findPageCrossSorting(Map<String, Object> params) {
		log.info("CrossSortingReadDao.findPageCrossSorting begin...");
		return super.getSqlSessionRead().selectList(CrossSortingReadDao.namespace + ".findPageTask", params);
	}

	public Integer findCountCrossSorting(Map<String, Object> params) {
		log.info("CrossSortingReadDao.findCountCrossSorting begin...");
		return (Integer) super.getSqlSessionRead().selectOne(CrossSortingReadDao.namespace + ".findCountTask", params);
	}

	@SuppressWarnings("unchecked")
	public List<CrossSorting> findMixDms(Map<String, Object> params) {
		log.info("CrossSortingReadDao.findMixDms begin...");
		return super.getSqlSessionRead().selectList(CrossSortingReadDao.namespace + ".findMixDms", params);
	}
	
    @SuppressWarnings("unchecked")
    public List<CrossSorting> findOne(Map<String, Object> params) {
        log.info("CrossSortingReadDao.findOne begin...");
        return super.getSqlSessionRead().selectList(CrossSortingReadDao.namespace + ".findOne", params);
    }
}
