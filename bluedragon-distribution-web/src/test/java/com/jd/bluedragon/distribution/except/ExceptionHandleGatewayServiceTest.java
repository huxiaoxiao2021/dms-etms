package com.jd.bluedragon.distribution.except;

import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.external.gateway.service.ExceptionHandleGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:distribution-web-context.xml"})
public class ExceptionHandleGatewayServiceTest {
    @Autowired
    private ExceptionHandleGatewayService exceptionHandleGatewayService;
    @Test
    public void testExceptionHandleGateway() throws Exception{
        QualityControlRequest request=new QualityControlRequest();
        request.setQcCode(20262);
        request.setQcValue("JDVB24708584685-1-1-");
        request.setDistCenterID(120191);
        exceptionHandleGatewayService.exceptionSubmit(request);
    }
}
