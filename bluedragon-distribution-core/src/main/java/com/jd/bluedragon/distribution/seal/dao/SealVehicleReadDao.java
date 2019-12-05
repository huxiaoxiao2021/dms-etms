package com.jd.bluedragon.distribution.seal.dao;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SealVehicleReadDao extends BaseDao<SealVehicle> {
	public static final String namespace = SealVehicleReadDao.class.getName();
	private final Logger log = LoggerFactory.getLogger(SealVehicleReadDao.class);

	/**
	 * 根据封车号及有效性查询封车信息 (从读库中获取数据)
	 * 
	 * @param sealCode
	 * @return
	 */
	public SealVehicle findBySealCodeFromRead(String sealCode) {
		log.info("SealVehicleReadDao.findBySealCodeFromRead begin...");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sealCode", sealCode);
		Object obj = this.getSqlSessionRead().selectOne(SealVehicleReadDao.namespace + ".findBySealCode", params);
		SealVehicle sealVehicle = (obj == null) ? null : (SealVehicle) obj;
		return sealVehicle;
	}

	/**
	 * 根据批次号、有效性查询封车信息 (从读库中获取数据)
	 * 
	 * @param sendCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SealVehicle> findBySendCode(String sendCode) {
		log.info("SealVehicleReadDao.findBySendCode begin...");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sendCode", sendCode);
		Object obj = this.getSqlSessionRead().selectList(SealVehicleReadDao.namespace + ".findBySendCode", params);
		return (obj == null) ? null : (List<SealVehicle>) obj;
	}

	/**
	 * 根据多个批次号查询
	 * 
	 * @param sealVehicleList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SealVehicle> findBySealCodes(List<SealVehicle> sealVehicleList) {
		log.info("SealVehicleReadDao.findBySealCodes begin...");
		Set<String> sealCodeSet = new HashSet<String>();
		for (SealVehicle sv : sealVehicleList) {
			sealCodeSet.add(sv.getCode());
		}
		List<String> list = Lists.newArrayList(sealCodeSet);
		Object obj = this.getSqlSessionRead().selectList(SealVehicleReadDao.namespace + ".findBySealCodes", list);
		return (obj == null) ? null : (List<SealVehicle>) obj;
	}

	@SuppressWarnings("unchecked")
	public List<SealVehicle> findByVehicleCode(String vehicleCode) {
		log.info("SealVehicleReadDao.findByVehicleCode begin...");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vehicleCode", vehicleCode);
		Object obj = this.getSqlSessionRead().selectList(SealVehicleReadDao.namespace + ".findByVehicleCode", params);
		return (obj == null) ? null : (List<SealVehicle>) obj;
	}
}
