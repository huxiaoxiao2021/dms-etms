package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.CreateLoadTaskReq;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadCarTaskCreateReq;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: bluedragon-distribution
 * @description:
 * @author: wuming
 * @create: 2020-10-17 11:46
 */
public class LoadCarDaoTest extends AbstractDaoIntegrationH2Test {

    private final Logger logger = LoggerFactory.getLogger(LoadCarDaoTest.class);

    @Autowired
    private LoadCarDao loadCarDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void insert() {
        LoadCar loadCar = new LoadCar();
        loadCar.setEndSiteCode(364605L);
        loadCar.setEndSiteName("北京通州分拣中心");
        loadCar.setLicenseNumber("京A00001");
        loadCar.setCreateUserErp("zhangsan1");
        loadCar.setCreateUserName("张三");
        loadCar.setCreateSiteCode(910L);
        loadCar.setCreateSiteName("北京马驹桥分拣中心");
        loadCarDao.insert(loadCar);
    }


}
