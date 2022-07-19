package com.jd.bluedragon.distribution.enterpriseDistribution.service.impl;

import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.EnterpriseInspectionManager;
import com.jd.bluedragon.distribution.enterpriseDistribution.domain.QualityInspectionQueryCondition;
import com.jd.bluedragon.distribution.enterpriseDistribution.dto.QualityInspectionDetailDto;
import com.jd.bluedragon.distribution.enterpriseDistribution.dto.QualityInspectionDto;
import com.jd.ningde.enterprise.request.QualityInspectionDetailRequest;
import com.jd.ningde.enterprise.request.QualityInspectionReportRequest;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

/**
 * @Description
 * @Author chenjunyan
 * @Date 2022/7/12 18:01
 */
public class EnterpriseDistributionServiceImplTest {
    @Mock
    ExportConcurrencyLimitService exportConcurrencyLimitService;
    @Mock
    EnterpriseInspectionManager enterpriseInspectionManager;
    @Mock
    Logger log;
    @InjectMocks
    EnterpriseDistributionServiceImpl enterpriseDistributionServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testQueryQualityInspectionPage() throws Exception {
        when(enterpriseInspectionManager.queryQualityInspectionPage((QualityInspectionReportRequest) any())).thenReturn(null);

        PagerResult<QualityInspectionDto> result = enterpriseDistributionServiceImpl.queryQualityInspectionPage(new QualityInspectionQueryCondition());
        Assert.assertNull(result);
    }

    @Test
    public void testQueryQualityInspectionDetailPage() throws Exception {
        when(enterpriseInspectionManager.queryQualityInspectionDetailPage((QualityInspectionDetailRequest) any())).thenReturn(null);

        PagerResult<QualityInspectionDetailDto> result = enterpriseDistributionServiceImpl.queryQualityInspectionDetailPage(new QualityInspectionQueryCondition());
        Assert.assertNull(result);
    }

    @Test
    public void testExport() throws Exception {
        when(enterpriseInspectionManager.queryQualityInspectionPage((QualityInspectionReportRequest) any())).thenReturn(null);

        enterpriseDistributionServiceImpl.export(new QualityInspectionQueryCondition(), null);
    }
}
