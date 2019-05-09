package com.jd.bluedragon.distribution.distribution.sorting.service;

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
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context.xml")
public class SortingReturnServiceImpleTest {

    @Autowired
    private SortingReturnService sortingReturnService;

    @Test
    public void testdoSortingReturn()throws Exception{
        String jmqMessage = "{\"body\":\"[{\\n  \\\"userCode\\\" : 203886,\\n  \\\"userName\\\" : \\\"高艳梅\\\",\\n  \\\"siteCode\\\" : 1087,\\n  \\\"siteName\\\" : \\\"沈阳亚一分拣中心\\\",\\n  \\\"businessType\\\" : 10,\\n  \\\"operateTime\\\" : \\\"2019-04-30 16:08:07\\\",\\n  \\\"shieldsError\\\" : \\\"3-客户取消订单\\\",\\n  \\\"packageCode\\\" : \\\"VA52006231524-1-1-\\\"\\n}]\",\"createSiteCode\":1087,\"executeTime\":1556611695415,\"fingerprint\":\"CE5B8A4904C073B8ADE727DAAF1BEC9F\",\"keyword1\":\"1087\",\"keyword2\":\"VA52006231524-1-1-\",\"ownSign\":\"DMS\",\"receiveSiteCode\":1087,\"sequenceName\":\"SEQ_TASK_SORTING\",\"tableName\":\"task_sorting\",\"type\":1220}";

        Task task2 = new Task();
        task2.setBody("[{\\n  \\\"userCode\\\" : 203886,\\n  \\\"userName\\\" : \\\"高艳梅\\\",\\n  \\\"siteCode\\\" : 1087,\\n  \\\"siteName\\\" : \\\"沈阳亚一分拣中心\\\",\\n  \\\"businessType\\\" : 10,\\n  \\\"operateTime\\\" : \\\"2019-04-30 16:08:07\\\",\\n  \\\"shieldsError\\\" : \\\"3-客户取消订单\\\",\\n  \\\"packageCode\\\" : \\\"VA52006231524-1-1-\\\"\\n}]");

        sortingReturnService.doSortingReturn(task2);
    }
}
