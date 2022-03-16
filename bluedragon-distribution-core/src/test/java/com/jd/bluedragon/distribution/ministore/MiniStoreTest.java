package com.jd.bluedragon.distribution.ministore;

import com.jd.bluedragon.distribution.seal.dao.SealBoxDao;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-dev.xml")
public class MiniStoreTest {
    @Autowired
    SortingDao sortingDao;
    @Test
    public void bindRelationTest(){
        Sorting sorting = new Sorting();
        sorting.setBoxCode("JDVA00003498559-1-1-");
        sorting.setCreateSiteCode(10186);
        List<Sorting> sortingList = sortingDao.findByBoxCode(sorting);
        System.out.println("sort:"+sortingList.toString());
    }
}
