package com.jd.bluedragon.distribution.globaltrade.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoadBillReportDao extends BaseDao<LoadBillReport> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String namespace = LoadBillReportDao.class.getName();

	public int add(LoadBillReport report) {
		log.info("LoadBillReportDao.add orderId is {}" , report.getOrderId());
		return this.getSqlSession().insert(LoadBillReportDao.namespace + ".add", report);
	}

	public int addBatch(List<LoadBillReport> reportList) {
		return this.getSqlSession().insert(LoadBillReportDao.namespace + ".addBatch", reportList);
	}
}
