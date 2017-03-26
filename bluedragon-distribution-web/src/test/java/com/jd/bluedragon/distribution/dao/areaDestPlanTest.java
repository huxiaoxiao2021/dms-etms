package com.jd.bluedragon.distribution.dao;

import com.jd.bluedragon.distribution.areadest.dao.AreaDestPlanDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuzuxiang on 2017/3/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/distribution-web-context.xml"})
public class areaDestPlanTest {
    private static Map<String, Object> params = new HashMap<String,Object>();

    @Autowired
    private AreaDestPlanDao areaDestPlanDao;

    @Before
    public void setUp() throws Exception {
        params.put("planId",4);
        params.put("siteCode",910);
        params.put("machineId",649);
    }

    @Test
    public void name() throws Exception {
        Boolean bool = areaDestPlanDao.isExist(params);
        Assert.assertEquals(true,bool);
    }
}
