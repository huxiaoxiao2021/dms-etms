package com.jd.bluedragon.distribution.globaltrade.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;

public class LoadBillDao extends BaseDao<LoadBill> {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final String namespace = LoadBillDao.class.getName();

	public int updateLoadBillStatus(Map<String, Object> loadBillStatusMap) {
		logger.info("LoadBillDao.updateLoadBillStatus orderId is " + loadBillStatusMap.get("orderId"));
		return this.getSqlSession().update(LoadBillDao.namespace + ".updateLoadBillStatus", loadBillStatusMap);
	}

	public int addBatch(List<LoadBill> loadBillList) {
		logger.info("LoadBillDao.addBatch with number of loadBillList is " + loadBillList.size());
		return this.getSqlSession().insert(LoadBillDao.namespace + ".addBatch", loadBillList);
	}

	public LoadBill findByPackageBarcode(String packageBarcode) {
		logger.info("LoadBillDao.getLoadBill with packageBarcode is " + packageBarcode);
		return (LoadBill) this.getSqlSession().selectOne(LoadBillDao.namespace + ".findByPackageBarcode", packageBarcode);
	}

	public int add(LoadBill lb) {
		return this.getSqlSession().insert(LoadBillDao.namespace + ".add", lb);
	}

	public int update(LoadBill lb) {
		return this.getSqlSession().update(LoadBillDao.namespace + ".update", lb);
	}

}
