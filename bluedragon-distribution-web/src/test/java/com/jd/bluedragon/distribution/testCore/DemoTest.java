package com.jd.bluedragon.distribution.testCore;

import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by wuzuxiang on 2017/3/23.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(SortSchemeService.class)
public class DemoTest {


    public DemoTest() {
    }

    @Before
    public void setUp() throws Exception {


    }

    @Test
    public void testCase() throws Exception {
        Assert.assertEquals(1,1);
//        assert 1 ==1;
    }
}
