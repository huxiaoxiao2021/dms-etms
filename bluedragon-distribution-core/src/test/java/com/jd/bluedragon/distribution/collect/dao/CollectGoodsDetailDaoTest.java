package com.jd.bluedragon.distribution.collect.dao;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetail;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author lijie
 * @date 2019/11/12 10:10
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
public class CollectGoodsDetailDaoTest {

    @Autowired
//    @Qualifier("collectGoodsDetailDao")
    private CollectGoodsDetailDao collectGoodsDetailDao;

    @Test
    public void findCollectGoodsDetailByPackageCodeTest(){
        String packageCode = "JDVA00024197173-4-30-";
        CollectGoodsDetail collectGoodsDetail = collectGoodsDetailDao.findCollectGoodsDetailByPackageCode(packageCode);
        Assert.assertEquals("B999",collectGoodsDetail.getCollectGoodsPlaceCode());
    }
}
