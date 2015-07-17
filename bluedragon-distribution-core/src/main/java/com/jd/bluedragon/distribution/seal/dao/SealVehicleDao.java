package com.jd.bluedragon.distribution.seal.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;

public class SealVehicleDao extends BaseDao<SealVehicle> {

	public static final String namespace = SealVehicleDao.class.getName();
	private final Log logger = LogFactory.getLog(SealVehicleDao.class);

	/**
	 * 根据封车号及有效性查询封车信息
	 * 
	 * @param sealCode
	 * @return
	 */
	public SealVehicle findBySealCode(String sealCode) {
		Object obj = this.getSqlSession().selectOne(namespace + ".findBySealCode", sealCode);
		SealVehicle sealVehicle = (obj == null) ? null : (SealVehicle) obj;
		return sealVehicle;
	}

	/**
	 * 根据主键或者必要条件更新封车信息为失效状态
	 * 
	 * @param sealVehicle
	 * @return
	 */
	public int updateDisable(SealVehicle sealVehicle) {
		return this.getSqlSession().update(SealVehicleDao.namespace + ".updateDisable", sealVehicle);
	}

	/**
	 * 增加解封车信息
	 * 
	 * @param sealVehicle
	 * @return
	 */
	public int updateSealVehicle(SealVehicle sealVehicle) {
		return this.getSqlSession().update(SealVehicleDao.namespace + ".updateSealVehicle", sealVehicle);
	}

	/**
	 * 增加解封车信息
	 * 
	 * @param sealVehicle
	 *            添加了批次号字段
	 * @return
	 */
	public int updateSealVehicle2(SealVehicle sealVehicle) {
		logger.info("SealVehicleDao.updateSealVehicle2 begin...");
		return this.getSqlSession().update(SealVehicleDao.namespace + ".updateSealVehicle2", sealVehicle);
	}

	/**
	 * 增加封车信息
	 * 
	 * @param sealVehicle
	 *            (添加了新字段)
	 * @return
	 */
	public Integer add2(String namespace, SealVehicle sealVehicle) {
		logger.info("SealVehicleDao.add2 begin...");
		return this.getSqlSession().insert(SealVehicleDao.namespace + ".add2", sealVehicle);
	}

	/**
	 * 批量增加
	 * 
	 * @param sealVehicleList
	 */
	public int addBatch(List<SealVehicle> sealVehicleList) {
		logger.info("SealVehicleDao.addBatch begin...");
		return this.getSqlSession().insert(SealVehicleDao.namespace + ".addBatch", sealVehicleList);
	}

	/**
	 * 批量更新
	 * 
	 * @param sealVehicleList
	 * @return
	 */
	public int updateBatch(SealVehicle sealVehicle) {
		logger.info("SealVehicleDao.updateBatch begin...");
		return this.getSqlSession().update(SealVehicleDao.namespace + ".updateBatch", sealVehicle);
	}

}
