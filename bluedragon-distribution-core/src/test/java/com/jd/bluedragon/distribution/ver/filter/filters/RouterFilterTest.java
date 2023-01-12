package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.router.impl.RouterServiceImpl;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouterFilterTest extends TestCase {


    @InjectMocks
    private RouterFilter routerFilter;

    @Mock
    private SiteService siteService;

    @Mock
    private RouterService routerService;

    public void testDoFilter() throws Exception{
        RouteNextDto routeNextDto = new RouteNextDto(null,true,null, null);
        when(routerService.matchRouterNextNode(anyInt(),anyString())).thenReturn(routeNextDto);
        when(siteService.getDmsShortNameByCode(anyInt())).thenReturn("11111");
        FilterContext filterContext = new FilterContext();
        WaybillCache waybillCache = new WaybillCache();
        waybillCache.setSendPay("");
        routerFilter.doFilter(filterContext,null);
    }
}