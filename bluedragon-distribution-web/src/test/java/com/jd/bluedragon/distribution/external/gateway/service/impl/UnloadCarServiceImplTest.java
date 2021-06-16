package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarScanRequest;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarStatusEnum;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarTaskReq;
import com.jd.bluedragon.common.dto.unloadCar.UnloadScanDetailDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadScan;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadScanRecord;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadScanDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadScanRecordDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.external.gateway.service.LoadAndUnloadCarGatewayService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 卸车相关功能测试
 * @author lvyuan21
 * @date 2020-12-29 16:14
 */
//@ContextConfiguration( locations = {"classpath:distribution-web-context.xml"})
//@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
//@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
//@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
//@ContextConfiguration(locations = "classpath:distribution-web-context.xml")

@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class UnloadCarServiceImplTest {

    @Resource
    private UnloadCarService unloadCarService;

    @Resource
    private UnloadScanDao unloadScanDao;
    @Resource
    private UnloadScanRecordDao unloadScanRecordDao;


    @Resource
    private LoadAndUnloadCarGatewayService loadAndUnloadCarGatewayService;



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
        List<UnloadScan> unloadScan = unloadScanDao.findUnloadScanBySealCarCode("SC20050800003129");
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
        UnloadScan unloadScan = unloadScanDao.findUnloadBySealAndWaybillCode("SC20050800003129", "JD0003334775397");
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

    @Test
    public void testFindRecordsBySealAndWaybillCode() {
        List<String> packageList = new ArrayList<>();
        packageList.add("JDV000503638245-1-5-");
        packageList.add("JDV000503638245-2-5-");
        packageList.add("JDV000503638245-3-5-");
        packageList.add("JDV000503638245-4-5-");
        packageList.add("JDV000503638245-5-5-");

        List<UnloadScanRecord> unloadRecordList = unloadScanRecordDao.findRecordsBySealAndWaybillCode("PDA1609338538034",
                "JDV000503638245");
        List<String> loadPackages;
        if (CollectionUtils.isNotEmpty(unloadRecordList)) {
            loadPackages = new ArrayList<>();
            for (UnloadScanRecord scanRecord : unloadRecordList) {
                loadPackages.add(scanRecord.getPackageCode());
            }
            packageList = ListUtils.subtract(packageList, loadPackages);
        }
        System.out.println(packageList);
    }


    @Test
    public void testPackageCodeScanNew() {
        for(int i = 0; i<10; i++) {
            try{
                String waybillCode = "JDV000700260347";//专网1:JDV000700260347  专网2：JDV000700260355  暂存1：
                String sealCarCode = "PDA1618985705156";
                String erp = "xumigen";
                String name = "徐迷根";
                Integer userCode = 202110;
                Integer siteCode = 10186;
                String siteName = "凉水河转运中心test";
                UnloadCarScanRequest req = new UnloadCarScanRequest();

                req.setBarCode(waybillCode + "-1-5-");
                req.setSealCarCode(sealCarCode);
                req.setWaybillCode(waybillCode);
                req.setOperateUserErp(erp);
                req.setOperateUserName(name);
                req.setOperateUserCode(userCode);
                req.setOperateSiteCode(siteCode);
                req.setOperateSiteName(siteName);

                String str = "{\n" +
                        " \"barCode\": \"JDV000700264620-1-5-\",\n" +
                        " \"forceCombination\": false,\n" +
                        " \"isCombinationTransfer\": 0,\n" +
                        " \"isForceCombination\": false,\n" +
                        " \"operateSiteCode\": 10186,\n" +
                        " \"operateSiteName\": \"北京凉水河快运中心\",\n" +
                        " \"operateTime\": 1623822191428,\n" +
                        " \"operateUserCode\": 18225,\n" +
                        " \"operateUserErp\": \"xumigen\",\n" +
                        " \"operateUserName\": \"徐迷根\",\n" +
                        " \"sealCarCode\": \"PDA1623821906170\",\n" +
                        " \"type\": 1\n" +
                        "}";
                UnloadCarScanRequest req1 = JSONObject.parseObject(str, UnloadCarScanRequest.class);
                JdVerifyResponse<UnloadScanDetailDto> res = loadAndUnloadCarGatewayService.packageCodeScanNew(req1);
                System.out.println("end");
            }catch (Exception e) {
                System.out.println("error");
            }
        }
    }

}
