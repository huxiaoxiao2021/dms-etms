package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarDistribution;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.jddl.common.utils.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/6/24 16:59
 */
public class UnloadCarDistributionDaoTest  extends AbstractDaoIntegrationH2Test {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UnloadCarDistributionDao unloadCarDistributionDao;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void add() {

    }

    @Test
    public void testSelectUnloadCarTaskHelpers() {
        String sealCarCode = "SC12345678";
        List<UnloadCarDistribution> distributions = unloadCarDistributionDao.selectUnloadCarTaskHelpers(sealCarCode);
        Assert.assertNotNull(distributions);
    }

    @Test
    public void testDeleteUnloadCarTaskHelpers() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sealCarCode","SC12345678");
        params.put("unloadUserErp","bjxings");
        params.put("updateTime",new Date());
        boolean result = unloadCarDistributionDao.deleteUnloadCarTaskHelpers(params);
        Assert.assertTrue(result);
    }

    @Test
    public void testAdd() {
        UnloadCarDistribution unloadCarDistribution = new UnloadCarDistribution();
        unloadCarDistribution.setSealCarCode("SC12345670");
        unloadCarDistribution.setUnloadUserErp("lijie357");
        unloadCarDistribution.setUnloadUserName("李杰");
        unloadCarDistribution.setUnloadUserType(1);
        unloadCarDistribution.setCreateTime(new Date());
        int result = unloadCarDistributionDao.add(unloadCarDistribution);
        Assert.assertTrue(result > 0);
        unloadCarDistributionDao.selectHelperBySealCarCode("SC12345670");
    }

    @Test
    public void testSelectTasksByUser() {
        List<String> sealCarCodes = unloadCarDistributionDao.selectTasksByUser("bjxings");
        Assert.assertTrue(sealCarCodes.size() > 0);
    }

    @Test
    public void testSelectHelperBySealCarCode() {
        String sealCarCode = "SC12345678";
        List<String> helpers = unloadCarDistributionDao.selectHelperBySealCarCode(sealCarCode);
    }



}