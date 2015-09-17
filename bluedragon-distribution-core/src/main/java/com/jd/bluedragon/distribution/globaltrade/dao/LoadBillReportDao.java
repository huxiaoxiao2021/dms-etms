package com.jd.bluedragon.distribution.globaltrade.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;

public class LoadBillReportDao extends BaseDao<LoadBillReport> {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final String namespace = LoadBillReportDao.class.getName();

	public int add(LoadBillReport report) {
		logger.info("LoadBillReportDao.add orderId is " + report.getOrderId());
		return this.getSqlSession().insert(LoadBillReportDao.namespace + ".add", report);
	}

}
