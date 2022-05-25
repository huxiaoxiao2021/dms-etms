package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExt;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.jd.bluedragon.utils.BusinessHelper.WAYBILL_EXTEND_DEPPON_THIRDCARRIERFLAG;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DPSendFilterTest extends TestCase {
    @InjectMocks
    private DPSendFilter dpSendFilter;

    @Mock
    private UccPropertyConfiguration uccConfiguration;
    @Mock
    private WaybillQueryManager waybillQueryManager;
    @Test
    public void testDoFilter() throws Exception{
        List<Integer> siteCodes = new ArrayList<>();
        siteCodes.add(1999257);
        Waybill waybill = new Waybill();
        WaybillExt waybillExt = new WaybillExt();
        waybillExt.setThirdCarrierFlag(WAYBILL_EXTEND_DEPPON_THIRDCARRIERFLAG);
        waybill.setWaybillExt(waybillExt);
        when(uccConfiguration.getDpSiteCodeList()).thenReturn(siteCodes);
        when(waybillQueryManager.queryWaybillByWaybillCode(anyString())).thenReturn(waybill);
        FilterContext filterContext = new FilterContext();
        filterContext.setWaybillCode("");
        filterContext.setReceiveSiteCode(1999257);
        dpSendFilter.doFilter(filterContext,null);
    }
}
