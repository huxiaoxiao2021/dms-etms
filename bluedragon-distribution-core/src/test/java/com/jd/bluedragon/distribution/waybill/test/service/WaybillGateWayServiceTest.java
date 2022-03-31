package com.jd.bluedragon.distribution.waybill.test.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.dao.CancelWaybillDao;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptModeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptTypeEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillServiceImpl;
import com.jd.bluedragon.external.gateway.service.WaybillGateWayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.WaybillVasDto;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @ClassName WaybillServiceTest
 * @Description
 * @Author wangyinhui3
 * @Date 2019/12/19
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/distribution-core-context-test.xml"})
public class WaybillGateWayServiceTest {

    @Autowired
    private WaybillGateWayService waybillGateWayService;

    @Mock
    private CancelWaybillDao cancelWaybillDao;

    private PdaOperateRequest request;

    @Mock
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Before
    public void initMocks() throws Exception {

        request = new PdaOperateRequest();
        request.setPackageCode("JDV000050018177");

        List<CancelWaybill> cancelWaybills = new ArrayList<>();
        CancelWaybill cancelWaybill = new CancelWaybill();
        cancelWaybill.setWaybillCode(request.getPackageCode());
        cancelWaybill.setInterceptType(WaybillCancelInterceptTypeEnum.REFUSE.getCode());
        cancelWaybill.setInterceptMode(WaybillCancelInterceptModeEnum.INTERCEPT.getCode());
        cancelWaybills.add(cancelWaybill);


        when(cancelWaybillDao.getByWaybillCode(cancelWaybill.getWaybillCode())).thenReturn(cancelWaybills);
//        when(taskService.toTask(any(TaskRequest.class), anyString())).thenReturn(any(Task.class));
        when(uccPropertyConfiguration.isPreOutZoneSwitch()).thenReturn(Boolean.TRUE);
    }

    @Test
    public void queryPackageCodesTest() {

        JdCResponse<List<String>> res = waybillGateWayService.queryPackageCodes("JDV000050018177");
        System.out.println(res.getData());

    }

}
