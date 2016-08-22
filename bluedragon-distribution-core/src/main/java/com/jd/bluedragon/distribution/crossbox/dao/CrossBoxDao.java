package com.jd.bluedragon.distribution.crossbox.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;

public class CrossBoxDao extends BaseDao<CrossBox> {
	private final Log logger = LogFactory.getLog(this.getClass());

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
		logger.info("CrossBoxDao.addCrossBox begin...");
		return this.getSqlSession().insert(this.getSqlId("addCrossBox"), crossBox);
	}
	
	public Integer deleteById(CrossBox crossBox) {
		logger.info("CrossBoxDao.deleteById begin...");
		return this.getSqlSession().update(this.getSqlId("deleteById"), crossBox);
	}
	
	public Integer updateYnCrossBoxById(CrossBox crossBox) {
		logger.info("CrossBoxDao.updateYnCrossBoxById begin...");
		return this.getSqlSession().update(this.getSqlId("updateYnCrossBoxById"), crossBox);
	}

	public Integer updateCrossDmsBoxById(CrossBox crossBox) {
		logger.info("CrossBoxDao.updateCrossDmsBoxById begin...");
		return this.getSqlSession().update(this.getSqlId("updateCrossDmsBoxById"), crossBox);
	}

	public CrossBox selectActiveCrossBoxByDmsId(CrossBox crossBox) {
		logger.info("CrossBoxDao.selectActiveCrossBoxByDmsId begin...");
		return (CrossBox) this.getSqlSession().selectOne(this.getSqlId("selectActiveCrossBoxByDmsId"), crossBox);
	}

	public String checkLineExist(CrossBox crossBox) {
		logger.info("CrossBoxDao.checkLineExist begin...");
		return (String) this.getSqlSession().selectOne(this.getSqlId("checkLineExist"), crossBox);
	}

	public Integer countByCondition(Map<String, Object> params) {
		logger.info("CrossBoxDao.countByCondition begin...");
		return (Integer) this.getSqlSession().selectOne(this.getSqlId("countByCondition"), params);
	}

	@SuppressWarnings("unchecked")
	public List<CrossBox> queryByCondition(Map<String, Object> params) {
		logger.info("CrossBoxDao.queryByCondition begin...");
		return this.getSqlSession().selectList(this.getSqlId("queryByCondition"), params);
	}

	public CrossBox getCrossBoxById(Integer id) {
		logger.info("CrossBoxDao.getCrossBoxById begin...");
		return (CrossBox) this.getSqlSession().selectOne(this.getSqlId("getCrossBoxById"), id);
	}

	public Integer updateCrossBoxByDms(CrossBox crossBox) {
		logger.info("CrossBoxDao.updateCrossBoxByDms begin...");
		return this.getSqlSession().update(this.getSqlId("updateCrossBoxByDms"), crossBox);
	}

	public String getFullLineByDmsId(CrossBox crossBox) {
		logger.info("CrossBoxDao.getFullLineByDmsId begin...");
		return (String) this.getSqlSession().selectOne(this.getSqlId("getFullLineByDmsId"), crossBox);
	}
	
	public CrossBox selectInactiveCrossBoxByDmsId(CrossBox crossBox) {
		logger.info("CrossBoxDao.selectInactiveCrossBoxByDmsId begin...");
		return (CrossBox) this.getSqlSession().selectOne(this.getSqlId("selectInactiveCrossBoxByDmsId"), crossBox);
	}

	public CrossBox selectCrossBoxByDmsId(CrossBox crossBox){
		return (CrossBox) this.getSqlSession().selectOne(this.getSqlId("selectCrossBoxByDmsId"), crossBox);
	}

}
