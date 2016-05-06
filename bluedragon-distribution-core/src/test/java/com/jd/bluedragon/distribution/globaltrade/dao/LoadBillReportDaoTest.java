package com.jd.bluedragon.distribution.globaltrade.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;

public class LoadBillReportDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private LoadBillReportDao loadBillReportDao;
	
	
	@Test
    public void testAdd() {
        LoadBillReport parameter = new LoadBillReport();
        parameter.setReportId("Mary");
        parameter.setLoadId("James");
        parameter.setWarehouseId("Mary");
        parameter.setProcessTime(new Date());
        parameter.setStatus(1);
        parameter.setOrderId("Jax");
        parameter.setNotes("James");
        loadBillReportDao.add(parameter);
    }
	
	@Test
    public void testAddBatch() {
		List<LoadBillReport> reportList = new ArrayList<LoadBillReport>();
		LoadBillReport subReport = new LoadBillReport();
		subReport.setReportId("Mary1");
		subReport.setLoadId("James1");
		subReport.setWarehouseId("Mary1");
		subReport.setProcessTime(new Date());
		subReport.setStatus(1);
		subReport.setOrderId("Jax1");
		subReport.setNotes("James1");
		reportList.add(subReport);
		
		subReport = new LoadBillReport();
		subReport.setReportId("Mary2");
		subReport.setLoadId("James2");
		subReport.setWarehouseId("Mary2");
		subReport.setProcessTime(new Date());
		subReport.setStatus(1);
		subReport.setOrderId("Jax2");
		subReport.setNotes("James2");
		reportList.add(subReport);
		
		subReport = new LoadBillReport();
		subReport.setReportId("Mary3");
		subReport.setLoadId("James3");
		subReport.setWarehouseId("Mary3");
		subReport.setProcessTime(new Date());
		subReport.setStatus(1);
		subReport.setOrderId("Jax3");
		subReport.setNotes("James3");
		reportList.add(subReport);
        loadBillReportDao.addBatch(reportList);
    }
}
