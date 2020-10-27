package com.jd.bluedragon.distribution.consumable.dao;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecordCondition;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lijie
 * @date 2020/6/17 22:00
 */
public class WaybillConsumableRecordDaoTest extends AbstractDaoIntegrationH2Test {

    @Autowired
    private WaybillConsumableRecordDao waybillConsumableRecordDao;

    private static final String USER_NAME = "邢松";
    private static final String USER_ERP = "bjxings";
    private static final long SITE_CODE = 910L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() {
        WaybillConsumableRecordCondition condition = new WaybillConsumableRecordCondition();
        condition.setWaybillCode("JDVC03978826121");
        Assert.assertNotNull(waybillConsumableRecordDao.queryOneByCondition(condition));
    }
}
