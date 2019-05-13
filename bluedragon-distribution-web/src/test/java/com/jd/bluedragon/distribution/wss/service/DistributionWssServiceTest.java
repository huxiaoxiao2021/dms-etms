package com.jd.bluedragon.distribution.wss.service;

import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author : xumigen
 * @date : 2019/5/13
 */
@ContextConfiguration( {"classpath:distribution-web-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class DistributionWssServiceTest {

    @Autowired
    private DistributionWssService distributionWssService;

    @Test
    public void testqueryPageSendInfoByBatchCode(){
        PageDto pageDto = new PageDto();
        pageDto.setCurrentPage(1);
        pageDto.setPageSize(2);
        distributionWssService.queryPageSendInfoByBatchCode(pageDto,"364605-910-20190308162459015");
    }


}
