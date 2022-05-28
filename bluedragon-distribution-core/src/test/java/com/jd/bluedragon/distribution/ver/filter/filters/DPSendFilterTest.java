package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExt;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
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
        BaseEntity<BigWaybillDto> baseEntity = new BaseEntity<BigWaybillDto>();
        BigWaybillDto bigWaybillDto = new BigWaybillDto();
        Waybill waybill = new Waybill();
        WaybillExt waybillExt = new WaybillExt();
        waybillExt.setThirdCarrierFlag(WAYBILL_EXTEND_DEPPON_THIRDCARRIERFLAG);
        waybill.setWaybillExt(waybillExt);
        bigWaybillDto.setWaybill(waybill);
        baseEntity.setData(bigWaybillDto);
        when(uccConfiguration.getDpSiteCodeList()).thenReturn(siteCodes);
        when(waybillQueryManager.getDataByChoiceNoCache(anyString(), ArgumentMatchers.<WChoice>any())).thenReturn(baseEntity);
        FilterContext filterContext = new FilterContext();
        filterContext.setWaybillCode("");
        filterContext.setReceiveSiteCode(1999257);
        dpSendFilter.doFilter(filterContext,null);
    }
}
