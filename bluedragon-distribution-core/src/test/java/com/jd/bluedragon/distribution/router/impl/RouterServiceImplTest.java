package com.jd.bluedragon.distribution.router.impl;

import com.google.common.base.Joiner;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedPackageConfig;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.PrintQueryRequest;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouterServiceImplTest {

    @InjectMocks
    private RouterServiceImpl routerService;
    @Mock
    private WaybillCacheService waybillCacheService;

    @Before
    public void before(){


    }

    @Test
    public void testmatchRouterNextNode(){
        when(waybillCacheService.getRouterByWaybillCode(anyString())).thenReturn("761806|866046|1489738|755380|1366370|58175|830824");
        RouteNextDto routeNextDto1 = routerService.matchRouterNextNode(755380,"111");
        System.out.println(JsonHelper.toJson(routeNextDto1));
        Assert.assertEquals(routeNextDto1.getFirstNextSiteId(),new Integer(1366370));
        Assert.assertEquals(routeNextDto1.isRoutExistCurrentSite(),Boolean.TRUE);

        when(waybillCacheService.getRouterByWaybillCode(anyString())).thenReturn("4444|555|666|777|888");
        RouteNextDto routeNextDto2 = routerService.matchRouterNextNode(555,"111");
        System.out.println(JsonHelper.toJson(routeNextDto2));
        Assert.assertEquals(Joiner.on(",").join(routeNextDto2.getNextSiteIdList()),"666,777,888");
        Assert.assertEquals(routeNextDto2.isRoutExistCurrentSite(),Boolean.TRUE);

        when(waybillCacheService.getRouterByWaybillCode(anyString())).thenReturn("4444|333|4444|222|888");
        RouteNextDto routeNextDto3 = routerService.matchRouterNextNode(4444,"222");
        System.out.println(JsonHelper.toJson(routeNextDto3));
        Assert.assertEquals(routeNextDto3.getFirstNextSiteId(),new Integer(222));

        when(waybillCacheService.getRouterByWaybillCode(anyString())).thenReturn("4444|333|111|222|888");
        RouteNextDto routeNextDto4 = routerService.matchRouterNextNode(888,"111");
        System.out.println(JsonHelper.toJson(routeNextDto4));
        Assert.assertEquals(routeNextDto4.getFirstNextSiteId(),null);
        Assert.assertEquals(routeNextDto4.isRoutExistCurrentSite(),Boolean.TRUE);

        when(waybillCacheService.getRouterByWaybillCode(anyString())).thenReturn("4444|333|111|222|888");
        RouteNextDto routeNextDto5 = routerService.matchRouterNextNode(9999,"111");
        System.out.println(JsonHelper.toJson(routeNextDto5));
        Assert.assertEquals(routeNextDto5.isRoutExistCurrentSite(),Boolean.FALSE);
    }

    public static void main(String[] args) {
        List routerShow = new ArrayList<>();
        boolean getCurNodeFlag =false;
        boolean verifyPass =false;
        String[] routerNodes = "761806|866046|1489738|755380|1366370|58175|830824".split("\\|");
        for (int i = 0; i < routerNodes.length - 1; i++) {
            int curNode = Integer.parseInt(routerNodes[i]);
            int nexNode = Integer.parseInt(routerNodes[i + 1]);
            if(curNode == 755380){
                getCurNodeFlag = true;
                routerShow.add(nexNode);
                if(nexNode == 58175){
                    verifyPass = true;
                    break;
                }
            }
        }

        System.out.println((getCurNodeFlag));
        System.out.println((verifyPass));
        System.out.println((getCurNodeFlag && !verifyPass));
        System.out.println(routerShow);
    }

}