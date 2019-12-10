package com.jd.bluedragon.distribution.seal.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SealVehicleDao extends BaseDao<SealVehicle> {

	public static final String namespace = SealVehicleDao.class.getName();
	private final Logger log = LoggerFactory.getLogger(SealVehicleDao.class);

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
		log.info("SealVehicleDao.updateSealVehicle2 begin...");
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
		log.info("SealVehicleDao.add2 begin...");
		return this.getSqlSession().insert(SealVehicleDao.namespace + ".add2", sealVehicle);
	}

	/**
	 * 批量增加
	 * 
	 * @param sealVehicleList
	 */
	public int addBatch(List<SealVehicle> sealVehicleList) {
		log.info("SealVehicleDao.addBatch begin...");
		return this.getSqlSession().insert(SealVehicleDao.namespace + ".addBatch", sealVehicleList);
	}

	/**
	 * 批量更新
	 * 
	 * @param sealVehicle
	 * @return
	 */
	public int updateBatch(SealVehicle sealVehicle) {
		log.info("SealVehicleDao.updateBatch begin...");
		return this.getSqlSession().update(SealVehicleDao.namespace + ".updateBatch", sealVehicle);
	}

}
