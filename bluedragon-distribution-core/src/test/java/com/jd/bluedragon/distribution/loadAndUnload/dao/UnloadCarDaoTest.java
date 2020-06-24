package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.jddl.common.utils.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme