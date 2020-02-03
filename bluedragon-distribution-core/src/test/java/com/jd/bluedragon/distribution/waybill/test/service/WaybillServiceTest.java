package com.jd.bluedragon.distribution.waybill.test.service;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.dao.CancelWaybillDao;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptModeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptTypeEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillServiceImpl;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @ClassName WaybillServiceTest
 * @Description
 * @Author wangyinhui3
 * @Date 2019/12/19
 **/
@RunWith(MockitoJUnitRunner.class)
public class WaybillServiceTest {

    @InjectMocks
    private WaybillServiceImpl waybillService;

    @Mock
    private WaybillQueryManager waybillQueryManager;

    @Mock
    private CancelWaybillDao cancelWaybillDao;

    @Mock
    private TaskService taskService;

    private PdaOperateRequest request;

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

        when(waybillQueryManager.queryExist(anyString())).thenReturn(Boolean.TRUE);
        when(cancelWaybillDao.getByWaybillCode(cancelWaybill.getWaybillCode())).thenReturn(cancelWaybills);
        when(taskService.toTask(any(TaskRequest.class), anyString())).thenReturn(any(Task.class));
    }

    @Test
    public void thirdCheckWaybillCancelTest() {

        System.out.println(JsonHelper.toJson(waybillService.thirdCheckWaybillCancel(request)));

    }
}
