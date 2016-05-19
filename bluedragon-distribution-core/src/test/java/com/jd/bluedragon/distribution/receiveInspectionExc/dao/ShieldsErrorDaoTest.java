package com.jd.bluedragon.distribution.receiveInspectionExc.dao;

import com.jd.bluedragon.distribution.receiveInspectionExc.domain.ShieldsError;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;

import java.util.List;

public class ShieldsErrorDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private ShieldsErrorDao shieldsErrorDao;


    @Test
    public void testAdd() {
        ShieldsError parameter = new ShieldsError();
        parameter.setBoxCode("James");
        parameter.setCarCode("James");
        parameter.setShieldsCode("James");
        parameter.setShieldsError("James");
        parameter.setCreateSiteCode(910);
        parameter.setCreateTime(new Date());
        parameter.setCreateUser("James");
        parameter.setCreateUserCode(910);
        parameter.setYn(1);
        parameter.setBusinessType(10);
        parameter.setUpdateTime(new Date());
        shieldsErrorDao.add(ShieldsErrorDao.class.getName(),parameter);
    }
}
