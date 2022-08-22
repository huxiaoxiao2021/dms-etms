package com.jd.bluedragon.distribution.jy;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.jyexpection.request.StatisticsByGridReq;
import com.jd.bluedragon.common.dto.send.request.CancelSendTaskReq;
import com.jd.bluedragon.common.dto.send.request.CreateVehicleTaskReq;
import com.jd.bluedragon.common.dto.send.request.DeleteVehicleTaskReq;
import com.jd.bluedragon.common.dto.send.response.CancelSendTaskResp;
import com.jd.bluedragon.common.dto.send.response.CreateVehicleTaskResp;
import com.jd.bluedragon.common.dto.send.response.VehicleSpecResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.send.JyNoTaskSendService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-dev.xml")
@Slf4j
public class JyExceptionTest {
    @Autowired
    JyExceptionService jyExceptionService;


    @Test
    public void getGridStatisticsPageListTest() {
        StatisticsByGridReq req = new StatisticsByGridReq();
        req.setStatus(0);
        req.setGridRid("");
        req.setPageNumber(0);
        req.setPageSize(0);
        req.setUserErp("");
        req.setSiteId(0);
        req.setPositionCode("");

        jyExceptionService.getGridStatisticsPageList(req);
    }
}
