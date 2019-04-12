package com.jd.bluedragon.distribution.globaltrade.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.utils.StringHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xumigen on 2019/4/12.
 */
@ContextConfiguration( {"classpath:distribution-web-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class LoadBillServiceTest {

    @Autowired
    private LoadBillService loadBillService;
    @Autowired
    private LoadBillDao loadBillDao;

    @Test
    public void testupdateLoadBillStatusByReport()throws Exception{
//        LoadBillReport report = new LoadBillReport();
//        loadBillService.updateLoadBillStatusByReport(report);
        LoadBillReport report2 = new LoadBillReport();
        report2.setSiteCode("");
        report2.setBoxCode("");
        report2.setId("");
        report2.setReportId("");
        report2.setLoadId("");
        report2.setWarehouseId("");
        report2.setProcessTime(new Date());
        report2.setStatus(0);
        report2.setNotes("");
        report2.setOrderId("");
        report2.setYn(0);
        report2.setCiqCheckFlag(0);
        report2.setCustBillNo("");
        report2.setWaybillCode("");

//        loadBillService.updateLoadBillStatusByReport(report2);

//
//        LoadBillReport report3 = new LoadBillReport();
//        report3.setReportId("1,1,1");
//        report3.setWarehouseId("werwqer");
//        report3.setProcessTime(new Date());
//        report3.setCiqCheckFlag(1);
//        report3.setCustBillNo("123123");
//        report3.setWaybillCode("JDVA00000265077,JDVA00000265077,JDVA00000265077");
//        report3.setStatus(0);
//        loadBillDao.updateLoadBillStatus(getLoadBillStatusMap(report3,null));

        LoadBillReport report4 = new LoadBillReport();
        report4.setReportId("1,1,1");
        report4.setWarehouseId("werwqer");
        report4.setProcessTime(new Date());
        report4.setCiqCheckFlag(1);
        report4.setCustBillNo("123123");
        report4.setWaybillCode("JDVA00000265077,JDVA00000265077,JDVA00000265077");
        report4.setStatus(1);
        report4.setLoadId("1,1,1");
        loadBillDao.updateLoadBillStatus(getLoadBillStatusMap(report4, Lists.asList("JDVA00000265077","JDVA00000265077,JDVA00000265077,JDVA00000265077".split("," +
                ""))));
    }

    private static Map<String, Object> getLoadBillStatusMap(LoadBillReport report, List<String> waybillCodeList) {
        Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
        loadBillStatusMap.put("loadIdList", StringHelper.parseList(report.getLoadId(), ","));
        loadBillStatusMap.put("warehouseId", report.getWarehouseId());
        /****更新全部为失败时已经设置过以下两个字段，此处无需重复设置****/
//		loadBillStatusMap.put("ciqCheckFlag", report.getCiqCheckFlag());
//		loadBillStatusMap.put("custBillNo", report.getCustBillNo());
        loadBillStatusMap.put("waybillCodeList", waybillCodeList);
        if (report.getStatus() == 1) {
            loadBillStatusMap.put("approvalCode", LoadBill.GREENLIGHT);
        } else {
            loadBillStatusMap.put("approvalCode", LoadBill.REDLIGHT);
        }
        return loadBillStatusMap;
    }
}
