package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.unloadCar.domain.UnloadCarCondition;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.jddl.common.utils.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/6/24 14:34
 */
public class UnloadCarDaoTest extends AbstractDaoIntegrationH2Test {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UnloadCarDao unloadCarDao;

    @Before
    public void setUp() {
    }

    @Test
    public void testSelectBySealCarCode() throws Exception {
        try {
            UnloadCar result = unloadCarDao.selectBySealCarCode("sealCarCode");
            Assert.assertTrue(true);
        }catch (Exception e){
            logger.error("异常信息:",e);
            Assert.assertTrue(false);
        }

    }

    @Test
    public void testGetUnloadCarTaskByParams() {

        UnloadCar unload = new UnloadCar();
        unload.setUnloadUserErp("bjxings");
        unload.setEndSiteCode(364605);
        List<UnloadCar> unloadCars = unloadCarDao.getUnloadCarTaskByParams(unload);
        System.out.println(unloadCars);
    }

    @Test
    public void testUpdateUnloadCarTaskStatus() {

        UnloadCar unloadCar = new UnloadCar();
        unloadCar.setSealCarCode("SC12345678");
        unloadCar.setStatus(1);
        unloadCar.setUnloadUserErp("bjxings");
        unloadCar.setUpdateUserErp("lijie357");
        unloadCar.setUpdateUserName("李杰");
        unloadCar.setEndSiteCode(364605);
        unloadCar.setOperateTime(new Date());

        int count = unloadCarDao.updateUnloadCarTaskStatus(unloadCar);

        Map<String, Object> p = new HashMap<String, Object>();
        p.put("unloadUserErp","bjxings");
        p.put("endSiteCode",364605);
        List<UnloadCar> unloadCars = unloadCarDao.getUnloadCarTaskByParams(unloadCar);
    }

    @Test
    public void testGetUnloadCarTaskScan() {
        List<String> sealCarCodes = new ArrayList<>();
        sealCarCodes.add("SC12345670");
        sealCarCodes.add("SC12345678");
        List<UnloadCar> unloadCars = unloadCarDao.getUnloadCarTaskScan(sealCarCodes);
        Assert.assertTrue(unloadCars.size() > 0);
    }

    @Test
    public void testQueryByCondition() {
        UnloadCarCondition condition = new UnloadCarCondition();
        condition.setStartTime(DateHelper.parseDateTime("2020-06-29 00:10:10"));
        condition.setEndTime(DateHelper.parseDateTime("2020-06-30 23:10:10"));
        //condition.setVehicleNumber("京A66666");
        List<Integer> status = new ArrayList<>();
        status.add(1);
        condition.setStatus(status);
        unloadCarDao.queryByCondition(condition);
    }

    @Test
    public void testDistributeTaskByParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("unloadUserErp","bjxings");
        params.put("railWayPlatForm","月-AAA");

        List<String> sealCarCodes = new ArrayList<>();
        sealCarCodes.add("SC12345678");
        sealCarCodes.add("SC12345670");
        params.put("sealCarCodes",sealCarCodes);
        params.put("updateUserErp","bjxings");
        params.put("updateUserName","邢松");
        params.put("distributeTime",new Date());
        unloadCarDao.distributeTaskByParams(params);
        unloadCarDao.selectBySealCarCode("SC12345678");
    }

    @Test
    public void testAdd() {
        UnloadCar unloadCar = new UnloadCar();
        unloadCar.setWaybillNum(123);
        unloadCar.setPackageNum(234);
        unloadCar.setSealCarCode("SC12345678");
        unloadCar.setVehicleNumber("京A66666");
        unloadCar.setSealTime(new Date());
        unloadCar.setStartSiteCode(910);
        unloadCar.setStartSiteName("马驹桥分拣中心");
        unloadCar.setEndSiteCode(364605);
        unloadCar.setEndSiteName("通州分拣中心");
        unloadCar.setBatchCode("121212-111111-123456789");
        unloadCar.setCreateTime(new Date());
        unloadCarDao.add(unloadCar);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme