package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarStatusEnum;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarTaskReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadScan;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadScanRecord;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadScanDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadScanRecordDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;


/**
 * 卸车相关功能测试
 * @author lvyuan21
 * @date 2020-12-29 16:14
 */
@ContextConfiguration( locations = {"classpath:distribution-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class UnloadCarServiceImplTest {

    @Resource
    private UnloadCarService unloadCarService;

    @Resource
    private UnloadScanDao unloadScanDao;
    @Resource
    private UnloadScanRecordDao unloadScanRecordDao;



    @Test
    public void testStartUnloadTask() {
        UnloadCarTaskReq req = new UnloadCarTaskReq();
        req.setTaskCode("PDA1609235524228");
        req.setTaskStatus(UnloadCarStatusEnum.UNLOAD_CAR_STARTED.getType());
        User user = new User();
        user.setUserName("邢松");
        user.setUserErp("bjxings");
        req.setUser(user);
        req.setOperateTime("2020-12-29");
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        req.setCurrentOperate(currentOperate);
        InvokeResult<Void> result = unloadCarService.startUnloadTask(req);
        System.out.println(result);

    }

    @Test
    public void testFindUnloadScan() {
        List<UnloadScan> unloadScan = unloadScanDao.findUnloadScanByBySealCarCode("SC20050800003129");
        System.out.println(unloadScan);
    }

    @Test
    public void testCreateUnloadScan() {
        UnloadScan unloadScan = new UnloadScan();
        unloadScan.setSealCarCode("PDA20050800003129");
        unloadScan.setWaybillCode("JD0003334775397");
        unloadScan.setYn(1);
        boolean result = unloadScanDao.insert(unloadScan);
        System.out.println(result);
    }

    @Test
    public void testUpdateUnloadScan() {
        UnloadScan unloadScan = unloadScanDao.findUnloadByBySealAndWaybillCode("SC20050800003129", "JD0003334775397");
        if (unloadScan != null) {
            boolean result = unloadScanDao.updateByPrimaryKey(unloadScan);
            System.out.println(result);
        }
        System.out.println(unloadScan);
    }

    @Test
    public void testFindUnloadScanRecord() {
        List<UnloadScanRecord> unloadScan = unloadScanRecordDao.findRecordBySealCarCode("SC20050800003129");
        System.out.println(unloadScan);
    }

    @Test
    public void testCreateUnloadScanRecord() {
        UnloadScanRecord unloadScanRecord = new UnloadScanRecord();
        unloadScanRecord.setSealCarCode("PDA20050800003129");
        unloadScanRecord.setWaybillCode("JD0003334775397");
        unloadScanRecord.setWaybillCode("JD0003334775397-1-1-");
        unloadScanRecord.setYn(1);
        boolean result = unloadScanRecordDao.insert(unloadScanRecord);
        List<UnloadScanRecord> unloadRecordList = unloadScanRecordDao.findRecordBySealCarCode("PDA20050800003129");
        System.out.println(unloadRecordList);
        System.out.println(result);
    }

    @Test
    public void testFindRecordBySealAndPackCode() {
        UnloadScanRecord unloadScanRecord = unloadScanRecordDao.findRecordBySealAndPackCode("PDA1609827639109",
                "JDV000503743644-3-5-");
        System.out.println(unloadScanRecord);
    }



}
