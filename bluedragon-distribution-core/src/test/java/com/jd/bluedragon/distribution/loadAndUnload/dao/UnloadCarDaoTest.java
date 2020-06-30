package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
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
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("unloadUserErp","bjxings");
        params.put("endSiteCode",364605);
        List<UnloadCar> unloadCars = unloadCarDao.getUnloadCarTaskByParams(params);
        System.out.println(unloadCars);
    }

    @Test
    public void testUpdateUnloadCarTaskStatus() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sealCarCode","SC12345678");
        params.put("status",0);
        params.put("unloadUserErp","bjxings");
        params.put("updateUserErp","lijie357");
        params.put("updateUserName","李杰");
        params.put("endSiteCode",364605);
        params.put("endSiteName","天坛分拣中心");

        params.put("updateTime",new Date());
        int count = unloadCarDao.updateUnloadCarTaskStatus(params);

        Map<String, Object> p = new HashMap<String, Object>();
        p.put("unloadUserErp","bjxings");
        p.put("endSiteCode",364605);
        List<UnloadCar> unloadCars = unloadCarDao.getUnloadCarTaskByParams(p);
    }

    @Test
    public void testGetUnloadCarTaskScan() {
        List<String> sealCarCodes = new ArrayList<>();
        sealCarCodes.add("SC12345670");
        sealCarCodes.add("SC12345678");
        List<UnloadCar> unloadCars = unloadCarDao.getUnloadCarTaskScan(sealCarCodes);
        Assert.assertTrue(unloadCars.size() > 0);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme