package com.jd.bluedragon.distribution.rest.sorting;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by xumigen on 2019/4/11.
 */

@ContextConfiguration( {"classpath:distribution-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SortingResourceTest {

    @Autowired
    private SortingResource sortingResource;

    @Test
    public void testcancelPackage(){
        sortingResource.cancelPackage(null);
    }
}
