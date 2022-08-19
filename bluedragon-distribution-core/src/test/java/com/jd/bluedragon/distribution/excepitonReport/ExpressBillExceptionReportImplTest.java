package com.jd.bluedragon.distribution.excepitonReport;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.request.ExpressBillExceptionReportRequest;
import com.jd.bluedragon.distribution.exceptionReport.billException.service.impl.ExpressBillExceptionReportServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/3/1 10:40
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/distribution-core-context-dev.xml")
public class ExpressBillExceptionReportImplTest {

    ExpressBillExceptionReportServiceImpl expressBillExceptionReportServiceImpl;


    @Test
    public void test1(){
        ExpressBillExceptionReportRequest request = new ExpressBillExceptionReportRequest();
        request.setReportType(1);
        request.setPackageCode("JDV123456789-1-1");
        JdCResponse<Boolean> response  = expressBillExceptionReportServiceImpl.reportExpressBillException(request);
        System.out.println(JSON.toJSONString(response));
    }
}
    
