package com.jd.bluedragon.distribution.distribution.sorting.service;

import com.jd.bluedragon.distribution.crossbox.service.CrossBoxService;
import com.jd.bluedragon.distribution.sorting.service.SortingReturnService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author : xumigen
 * @date : 2019/4/30
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class CrossBoxServiceTest {

    @Autowired
    private CrossBoxService crossBoxService;

    @Test
    public void updateSiteName()throws Exception{
        crossBoxService.updateSiteName(1632742,"深圳梅林营业部14","xxx");
    }
}
