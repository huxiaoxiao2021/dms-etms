package com.jd.bluedragon.distribution.globaltrade.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

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
        parameter.setStatus(584);
        parameter.setOrderId("Jax");
        parameter.setNotes("James");
        loadBillReportDao.add(parameter);
    }
	
	@Test
    public void testAddBatch() {
        List parameter = new ArrayList();
        //set property for item.reportId
        //set property for item.loadId
        //set property for item.warehouseId
        //set property for item.processTime
        //set property for item.status
        //set property for item.orderId
        //set property for item.notes
        loadBillReportDao.addBatch(parameter);
    }
}
