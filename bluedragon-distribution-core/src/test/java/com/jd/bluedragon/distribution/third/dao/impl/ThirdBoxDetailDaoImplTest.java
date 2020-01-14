package com.jd.bluedragon.distribution.third.dao.impl;

import com.jd.bluedragon.distribution.third.dao.ThirdBoxDetailDao;
import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2020年01月08日 16时:11分
 */
@RunWith(MockitoJUnitRunner.class)
public class ThirdBoxDetailDaoImplTest {

    @InjectMocks
    private ThirdBoxDetailDaoImpl boxDetailDao;

    private static ThirdBoxDetail detail = new ThirdBoxDetail();

    @Before
    public void setUp() throws Exception {

        detail.setTenantCode("ECONOMIC_NET");
        detail.setStartSiteId(910);
        detail.setStartSiteCode("910F910");
        detail.setEndSiteId(39);
        detail.setEndSiteCode("039F039");
        detail.setOperatorId("wang");
        detail.setOperatorName("王五");
        detail.setOperatorUnitName("经济网站点");
        detail.setOperatorTime(new Date());
        detail.setBoxCode("1234567890123456789");
        detail.setWaybillCode("JD111111111111");
        detail.setPackageCode("JD111111111111-1-1-");
    }

    @Test
    public void insert() {
        boolean result = boxDetailDao.insert(detail);
        Assert.assertEquals(true, result);
    }

    @Test
    public void queryByBoxCode() {
        List<ThirdBoxDetail> result = boxDetailDao.queryByBoxCode(detail.getTenantCode(), detail.getStartSiteId(), detail.getBoxCode());
        Assert.assertEquals(1, result.size());
    }
    @Test
    public void cancel() {
        boolean result = boxDetailDao.cancel(detail);
        Assert.assertEquals(true, result);
    }


}