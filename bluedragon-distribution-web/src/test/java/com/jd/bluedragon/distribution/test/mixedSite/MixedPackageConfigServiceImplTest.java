package com.jd.bluedragon.distribution.test.mixedSite;

import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedSite;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.PrintQueryRequest;
import com.jd.bluedragon.distribution.mixedPackageConfig.service.MixedPackageConfigService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/10/29 14:20
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class MixedPackageConfigServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(MixedPackageConfigServiceImplTest.class);

    @InjectMocks
    private MixedPackageConfigService mixedPackageConfigService;

    @Test
    public void queryMixedSiteCodeForPrint() {

        try {
            PrintQueryRequest printQueryRequest = new PrintQueryRequest();
            printQueryRequest.setOriginalDmsCode(910);
            printQueryRequest.setDestinationDmsCode(39);
            printQueryRequest.setTransportType(1);
            printQueryRequest.setRuleType(1);

            MixedSite mixedSite = mixedPackageConfigService.queryMixedSiteCodeForPrint(printQueryRequest);
            mixedSite.getCollectionAddress();
        }catch (Exception e){
            logger.error("服务异常",e);
        }
        Assert.assertTrue(true);
    }
}
