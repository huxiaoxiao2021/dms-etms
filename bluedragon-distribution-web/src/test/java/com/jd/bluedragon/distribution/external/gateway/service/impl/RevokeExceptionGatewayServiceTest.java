package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.revokeException.request.QueryExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.request.RevokeExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.response.ExceptionReportResp;
import com.jd.bluedragon.external.gateway.service.RevokeExceptionGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author liwenji
 * @date 2023-01-05 15:02
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class RevokeExceptionGatewayServiceTest {
    
    @Autowired
    private RevokeExceptionGatewayService revokeExceptionGatewayService;
    
    @Test
    public void queryAbnormalPageTest() {
        QueryExceptionReq queryExceptionReq = new QueryExceptionReq();
        CurrentOperate operate = new CurrentOperate();
        operate.setSiteCode(910);
        queryExceptionReq.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("吴有德");
        user.setUserErp("wuyoude");
        queryExceptionReq.setUser(user);
        queryExceptionReq.setPageSize(20);
        queryExceptionReq.setCurrentPage(1);
        JdCResponse<List<ExceptionReportResp>> listJdCResponse = revokeExceptionGatewayService.queryAbnormalPage(queryExceptionReq);
        System.out.println(JsonHelper.toJson(listJdCResponse));
    }
    
    @Test
    public void closeTransAbnormalTest() {
        RevokeExceptionReq revokeExceptionReq = new RevokeExceptionReq();
        revokeExceptionReq.setTransAbnormalCode("TAB23010300565637");
        CurrentOperate operate = new CurrentOperate();
        operate.setSiteCode(910);
        revokeExceptionReq.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("吴有德");
        user.setUserErp("wuyoude");
        revokeExceptionReq.setUser(user);
        JdCResponse<String> stringJdCResponse = revokeExceptionGatewayService.closeTransAbnormal(revokeExceptionReq);
        System.out.println(JsonHelper.toJson(stringJdCResponse));
    }
    
}
