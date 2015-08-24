package com.jd.bluedragon.distribution.globaltrade.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillDao;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillReadDao;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillReportDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;

@Service("loadBillService")
public class LoadBillServiceImpl implements LoadBillService {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final int SUCCESS = 1; // report的status,1为成功,2为失败
	@Autowired
	private LoadBillDao loadBillDao;

	@Autowired
	private LoadBillReadDao loadBillReadDao;

	@Autowired
	private LoadBillReportDao loadBillReportDao;

	@Override
	public int add(LoadBill loadBill) {
		return 0;
	}

	@Override
	public int update(LoadBill loadBill) {
		return 0;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void updateLoadBillStatusByReport(LoadBillReport report) {
		logger.info("更新装载单状态 reportId is " + report.getReportId() + ", orderId is " + report.getOrderId());
		loadBillReportDao.add(report);
		loadBillDao.updateLoadBillStatus(getLoadBillStatusMap(report)); // 更新loadbill的approval_code
	}

	private Map<String, Object> getLoadBillStatusMap(LoadBillReport report) {
		Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
		loadBillStatusMap.put("orderId", report.getOrderId());
		if (report.getStatus() == SUCCESS) {
			loadBillStatusMap.put("approvalCode", LoadBill.GREENLIGHT);
		} else {
			loadBillStatusMap.put("approvalCode", LoadBill.REDLIGHT);
		}
		return loadBillStatusMap;
	}

    public List<LoadBill> findWaybillInLoadBill(LoadBillReport report){
        Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
        loadBillStatusMap.put("orderId", report.getOrderId());
        List<LoadBill> loadBillList=  loadBillReadDao.findWaybillInLoadBill(loadBillStatusMap);
        return loadBillList;
    }

}
