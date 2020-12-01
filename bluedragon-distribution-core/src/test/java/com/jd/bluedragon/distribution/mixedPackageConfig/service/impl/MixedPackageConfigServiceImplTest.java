package com.jd.bluedragon.distribution.mixedPackageConfig.service.impl;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.mixedPackageConfig.dao.MixedPackageConfigDao;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfig;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfigRequest;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedSite;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.PrintQueryRequest;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/9/3 14:36
 */
@RunWith(MockitoJUnitRunner.class)
public class MixedPackageConfigServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(MixedPackageConfigServiceImplTest.class);

    @InjectMocks
    private MixedPackageConfigServiceImpl mixedPackageConfigService;

    @Mock
    private BaseMajorManager baseMajorManager;

    @Mock
    private MixedPackageConfigDao mixedPackageConfigDao;

    private List<MixedPackageConfig> list;

    private PrintQueryRequest printQueryRequest;

    private BaseStaffSiteOrgDto baseDto;

    @Before
    public void before(){
        list = new ArrayList<>();
        MixedPackageConfig mixedPackageConfig = new MixedPackageConfig();
        mixedPackageConfig.setCreateSiteCode(910);
        mixedPackageConfig.setReceiveSiteCode(39);
        mixedPackageConfig.setMixedSiteCode(910);
        mixedPackageConfig.setTransportType(1);
        mixedPackageConfig.setTs(1599115671523L);
        list.add(mixedPackageConfig);
        MixedPackageConfig mixedPackageConfig1 = new MixedPackageConfig();
        mixedPackageConfig1.setCreateSiteCode(910);
        mixedPackageConfig1.setReceiveSiteCode(39);
        mixedPackageConfig1.setMixedSiteCode(910);
        mixedPackageConfig1.setTransportType(1);
        mixedPackageConfig1.setTs(1599115673523L);
        list.add(mixedPackageConfig1);
        MixedPackageConfig mixedPackageConfig2 = new MixedPackageConfig();
        mixedPackageConfig2.setCreateSiteCode(910);
        mixedPackageConfig2.setReceiveSiteCode(39);
        mixedPackageConfig2.setMixedSiteCode(910);
        mixedPackageConfig2.setTransportType(1);
        mixedPackageConfig2.setTs(1599115675523L);
        list.add(mixedPackageConfig2);

        printQueryRequest = new PrintQueryRequest();
        printQueryRequest.setOriginalDmsCode(910);
        printQueryRequest.setDestinationDmsCode(39);
        printQueryRequest.setTransportType(1);
        printQueryRequest.setRuleType(1);

        baseDto = new BaseStaffSiteOrgDto();
        baseDto.setDmsShortName("北京通州");
    }

    @Test
    public void queryMixedSiteCodeForPrint() {

        try {
            Mockito.when(mixedPackageConfigDao.queryMixedPackageConfigs((MixedPackageConfigRequest) Mockito.any())).thenReturn(list);

            Mockito.when(baseMajorManager.getBaseSiteBySiteId(Mockito.anyInt())).thenReturn(baseDto);

            MixedSite mixedSite = mixedPackageConfigService.queryMixedSiteCodeForPrint(printQueryRequest);
        }catch (Exception e){
            logger.error("",e);
        }
        Assert.assertTrue(true);
    }
}