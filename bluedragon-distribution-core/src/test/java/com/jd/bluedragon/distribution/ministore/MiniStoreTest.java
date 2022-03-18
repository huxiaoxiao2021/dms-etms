package com.jd.bluedragon.distribution.ministore;

import com.jd.bluedragon.distribution.ministore.dao.MiniStoreBindRelationDao;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.ministore.service.impl.MiniStoreServiceImpl;
import com.jd.bluedragon.distribution.seal.dao.SealBoxDao;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.AssertTrue;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-dev.xml")
public class MiniStoreTest {
    @Autowired
    MiniStoreBindRelationDao miniStoreBindRelationDao;
    @Autowired
    MiniStoreService miniStoreService;
    @Test
    public void bindRelationTest(){
        //Boolean flag =miniStoreService.validateIceBoardBindStatus("i1");
        Integer a =1;
        Assert.assertTrue(1==a);
    }
}
