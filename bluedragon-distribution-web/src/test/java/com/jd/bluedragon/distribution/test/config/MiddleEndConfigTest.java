package com.jd.bluedragon.distribution.test.config;

import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxSystemTypeEnum;
import com.jd.bluedragon.distribution.box.service.BoxServiceImpl;
import com.jd.bluedragon.distribution.middleend.SortingServiceFactory;
import com.jd.bluedragon.distribution.middleend.sorting.dao.DynamicSortingQueryDao;
import com.jd.bluedragon.distribution.middleend.sorting.dao.ISortingDao;
import com.jd.bluedragon.distribution.middleend.sorting.service.DmsSortingServiceImpl;
import com.jd.bluedragon.distribution.middleend.sorting.service.ISortingService;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @program: bluedragon-distribution
 * @description: 中台下线开关测试
 * @author: liuduo8
 * @create: 2020-09-14 14:00
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class MiddleEndConfigTest {

    @Autowired
    private DynamicSortingQueryDao dynamicSortingQueryDao;

    @Autowired
    private SortingServiceFactory sortingServiceFactory;

    @Autowired
    private BoxServiceImpl boxService;

    @Test
    public void dynamicSortingQueryDao(){
        ISortingDao sortingDao = dynamicSortingQueryDao.selectDao(364605);

        Assert.assertTrue(sortingDao instanceof SortingDao);
    }

    @Test
    public void sortingServiceFactory(){
        ISortingService sortingService = sortingServiceFactory.getSortingService(364605);
        Assert.assertTrue(sortingService instanceof DmsSortingServiceImpl);

    }

    @Test
    public void boxService(){
        Box param = new Box();
        param.setCreateSiteCode(364605);
        boxService.batchAddNew(param,BoxSystemTypeEnum.PRINT_CLIENT);

    }
    @Test
    public void testAllConfig(){

        Box param = new Box();
        param.setCreateSiteCode(364605);
        boxService.batchAddNew(param,BoxSystemTypeEnum.PRINT_CLIENT);

        ISortingDao sortingDao = dynamicSortingQueryDao.selectDao(364605);

        Assert.assertTrue(sortingDao instanceof SortingDao);

        ISortingService sortingService = sortingServiceFactory.getSortingService(364605);
        Assert.assertTrue(sortingService instanceof DmsSortingServiceImpl);


    }

}
