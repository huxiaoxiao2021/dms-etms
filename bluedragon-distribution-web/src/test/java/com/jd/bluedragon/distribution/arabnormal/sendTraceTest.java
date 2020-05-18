package com.jd.bluedragon.distribution.arabnormal;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.arAbnormal.ArAbnormalServiceImpl;
import com.jd.bluedragon.distribution.transport.domain.ArContrabandReason;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BdTraceDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author lijie
 * @date 2020/3/23 20:35
 */
@RunWith(MockitoJUnitRunner.class)
public class sendTraceTest {

    @InjectMocks
    public ArAbnormalServiceImpl arAbnormalService;

    @Mock
    public WaybillTraceApi waybillTraceApi;

    @Mock
    public WaybillQueryManager waybillQueryManager;

    private ArAbnormalRequest arAbnormalRequest;

    private Map<String, List<String>> waybillMap;



    @Before
    public void initMocks() throws Exception{
        arAbnormalRequest = new ArAbnormalRequest();
        arAbnormalRequest.setPackageCode("JDV000051629805-1-1-");
        arAbnormalRequest.setContrabandReason(1);
        arAbnormalRequest.setCancelType(1);
        arAbnormalRequest.setTranspondReason(10);
        arAbnormalRequest.setTranspondType(10);
        arAbnormalRequest.setSiteCode(910);
        arAbnormalRequest.setSiteName("北京马驹桥分拣中心");
        arAbnormalRequest.setOperateTime("2020-03-24 09:51:28");
        arAbnormalRequest.setUserErp("bjxings");
        arAbnormalRequest.setUserCode(10053);

        List packageList = new ArrayList(1);
        packageList.add("JDV000051629805-1-1-");
        waybillMap = new HashMap<>();
        waybillMap.put("JDV000051629805",packageList);

        when(waybillTraceApi.sendBdTrace(any(BdTraceDto.class))).thenReturn(new BaseEntity<Boolean>());
        when(waybillQueryManager.sendBdTrace(any(BdTraceDto.class))).thenReturn(true);
    }

    @Test
    public void sendTraceTest(){
        try {
            Method testSendTrace = arAbnormalService.getClass().getDeclaredMethod("doSendTrace",ArAbnormalRequest.class,Map.class);
            testSendTrace.setAccessible(true);
            Object result = testSendTrace.invoke(arAbnormalService,arAbnormalRequest,waybillMap);
            System.out.println(result);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //arAbnormalService.doSendTrace(arAbnormalRequest,waybillMap);
    }

    @Test
    public void getReasonListTest(){
        List<ArContrabandReason> reasons = arAbnormalService.getArContrabandReasonList();
        System.out.println(reasons.toString());
    }

}
