package com.jd.bluedragon.distribution.whiteList.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.whitelist.WhiteList;
import com.jd.bluedragon.distribution.whitelist.WhiteListCondition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lijie
 * @date 2020/3/18 14:00
 */
public class WhiteListDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private WhiteListDao whiteListDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void queryByConditionTest() {
        WhiteListCondition condition = new WhiteListCondition();
        condition.setMenu("验货");
        condition.setDimension("个人");
        condition.setErp("bjxings");
        Assert.assertTrue(whiteListDao.queryByCondition(condition).size() > 0);
    }

    @Test
    public void queryTest(){
        WhiteList whiteList = new WhiteList();
        whiteList.setMenu("分拣");
        whiteList.setDimension("场地");
        whiteList.setSiteName("北京马驹桥分拣中心");
        Assert.assertTrue(whiteListDao.query(whiteList)>0);
    }

    @Test
    public void saveTest(){
        WhiteList whiteList = new WhiteList();
        whiteList.setMenu("发货");
        whiteList.setDimension("场地");
        whiteList.setSiteCode(910);
        whiteList.setSiteName("北京马驹桥分拣中心");
        Assert.assertTrue(whiteListDao.save(whiteList)>0);
    }

    @Test
    public void deleteByIdsTest(){
        List<Long> ids = new ArrayList<>();
        ids.add(1l);
        ids.add(2l);
        ids.add(3l);
        ids.add(4l);
        Assert.assertTrue(whiteListDao.deleteByIds(ids)>0);
    }
}
