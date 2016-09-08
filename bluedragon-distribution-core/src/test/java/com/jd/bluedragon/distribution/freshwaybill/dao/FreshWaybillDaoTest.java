package com.jd.bluedragon.distribution.freshwaybill.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.waybill.dao.FreshWaybillDao;
import com.jd.bluedragon.distribution.waybill.domain.FreshWaybill;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dudong on 2016/9/1.
 */
public class FreshWaybillDaoTest extends AbstractDaoIntegrationTest{
    @Autowired
    private FreshWaybillDao freshWaybillDao;

    @Test
    public void testInsertFreshWaybill() {
        FreshWaybill freshWaybill = new FreshWaybill();
        freshWaybill.setPackageCode("jamers");
        freshWaybill.setBoxType(1);
        freshWaybill.setCreateTime(new Date());
        freshWaybill.setPackageTemp(4.4);
        freshWaybill.setSlabNum(4);
        freshWaybill.setSlabTemp(23.1);
        Assert.assertEquals(new Integer(1), freshWaybillDao.addFreshWaybill(freshWaybill));
    }

    @Test
    public void testGetFreshWaybillByCode() throws Exception {
        FreshWaybill freshWaybill = new FreshWaybill();
        freshWaybill.setPackageCode("jamers");
        Assert.assertEquals(1, freshWaybillDao.getFreshWaybillByCode(freshWaybill).size());
    }

    @Test
    public void testGetFreshWaybillByID() throws Exception {
        FreshWaybill freshWaybill = new FreshWaybill();
        freshWaybill.setId(1L);
        Assert.assertNotNull(freshWaybillDao.getFreshWaybillByID(freshWaybill));
    }

    @Test
    public void testGetFreshWaybillPage() throws Exception {
        Map<String, Object> para = new HashMap<String, Object>();
        para.put("startIndex",new Integer(1));
        para.put("pageSize",new Integer(10));
        Assert.assertNotNull(freshWaybillDao.getFreshWaybillPage(para));
    }

    @Test
    public void testGetFreshWaybillCountByCode() throws Exception {
        FreshWaybill freshWaybill = new FreshWaybill();
        freshWaybill.setPackageCode("jamers");
        Assert.assertNotNull(freshWaybillDao.getFreshWaybillCountByCode(freshWaybill));
    }

}
