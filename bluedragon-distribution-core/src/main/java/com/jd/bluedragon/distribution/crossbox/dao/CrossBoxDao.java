package com.jd.bluedragon.distribution.crossbox.dao;

import com.google.common.collect.Maps;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.jddl.executor.function.scalar.filter.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class CrossBoxDao extends BaseDao<CrossBox> {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String SQLID_SEPARATOR = "."; // namespace中连接类全名和sqlId的分隔符

	protected String getSqlId(String sqlId) {
		return getSqlId(this.getClass(), sqlId);
	}

	@SuppressWarnings("rawtypes")
	protected static String getSqlId(Class clazz, String sqlId) {
		if (clazz == null || sqlId == null) {
			return null;
		}
		return clazz.getName() + SQLID_SEPARATOR + sqlId;
	}
	
	public Integer addCrossBox(CrossBox crossBox) {
		log.info("CrossBoxDao.addCrossBox begin...");
		return this.getSqlSession().insert(this.getSqlId("addCrossBox"), crossBox);
	}
	
	public Integer deleteById(CrossBox crossBox) {
		log.info("CrossBoxDao.deleteById begin...");
		return this.getSqlSession().update(this.getSqlId("deleteById"), crossBox);
	}
	
	public Integer updateYnCrossBoxById(CrossBox crossBox) {
		log.info("CrossBoxDao.updateYnCrossBoxById begin...");
		return this.getSqlSession().update(this.getSqlId("updateYnCrossBoxById"), crossBox);
	}

	public Integer updateCrossDmsBoxById(CrossBox crossBox) {
		log.info("CrossBoxDao.updateCrossDmsBoxById begin...");
		return this.getSqlSession().update(this.getSqlId("updateCrossDmsBoxById"), crossBox);
	}

	public CrossBox selectActiveCrossBoxByDmsId(CrossBox crossBox) {
		log.info("CrossBoxDao.selectActiveCrossBoxByDmsId begin...");
		return (CrossBox) this.getSqlSession().selectOne(this.getSqlId("selectActiveCrossBoxByDmsId"), crossBox);
	}

	public String checkLineExist(CrossBox crossBox) {
		log.info("CrossBoxDao.checkLineExist begin...");
		return (String) this.getSqlSession().selectOne(this.getSqlId("checkLineExist"), crossBox);
	}

	public Integer countByCondition(Map<String, Object> params) {
		log.info("CrossBoxDao.countByCondition begin...");
		return (Integer) this.getSqlSession().selectOne(this.getSqlId("countByCondition"), params);
	}

	@SuppressWarnings("unchecked")
	public List<CrossBox> queryByCondition(Map<String, Object> params) {
		log.info("CrossBoxDao.queryByCondition begin...");
		return this.getSqlSession().selectList(this.getSqlId("queryByCondition"), params);
	}

	public CrossBox getCrossBoxById(Integer id) {
		log.info("CrossBoxDao.getCrossBoxById begin...");
		return (CrossBox) this.getSqlSession().selectOne(this.getSqlId("getCrossBoxById"), id);
	}

	public Integer updateCrossBoxByDms(CrossBox crossBox) {
		log.info("CrossBoxDao.updateCrossBoxByDms begin...");
		return this.getSqlSession().update(this.getSqlId("updateCrossBoxByDms"), crossBox);
	}

	public String getFullLineByDmsId(CrossBox crossBox) {
		log.info("CrossBoxDao.getFullLineByDmsId begin...");
		return (String) this.getSqlSession().selectOne(this.getSqlId("getFullLineByDmsId"), crossBox);
	}
	
	public CrossBox selectInactiveCrossBoxByDmsId(CrossBox crossBox) {
		log.info("CrossBoxDao.selectInactiveCrossBoxByDmsId begin...");
		return (CrossBox) this.getSqlSession().selectOne(this.getSqlId("selectInactiveCrossBoxByDmsId"), crossBox);
	}

	public CrossBox selectCrossBoxByDmsId(CrossBox crossBox){
		return (CrossBox) this.getSqlSession().selectOne(this.getSqlId("selectCrossBoxByDmsId"), crossBox);
	}

	public List<CrossBox> selectCrossBoxBySiteCode(Integer offset,Integer limit,Integer siteCode) {
		Map<String, Integer> param = Maps.newHashMap();
		param.put("offset",offset);
		param.put("limit",limit);
		param.put("siteCode",siteCode);
		log.info("CrossBoxDao.selectCrossBoxBySiteCode begin...");
		return this.getSqlSession().selectList(this.getSqlId("selectCrossBoxBySiteCode"), param);
	}

	public int updateCrossBoxById(CrossBox crossDmsBox) {
		log.info("CrossBoxDao.updateCrossBoxById begin...");
		return this.getSqlSession().update(this.getSqlId("updateCrossBoxById"), crossDmsBox);
	}

	public int countCrossBoxBySiteCode(Integer siteCode) {
		log.info("CrossBoxDao.countCrossBoxBySiteCode begin...");
		return this.getSqlSession().selectOne(this.getSqlId("countCrossBoxBySiteCode"), siteCode);
	}
}
