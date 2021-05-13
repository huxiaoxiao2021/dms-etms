package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.OrderServiceHelper;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.jdorders.component.vo.request.OrderQueryRequest;
import com.jd.jdorders.component.vo.response.OrderQueryResponse;
import com.jd.order.export.domain.ComponentBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/15 20:06
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-test.xml")
public class OrderWebServiceTest {


    @Test
    public void test1(){
        com.jd.jdorders.component.export.OrderMiddlewareCBDExport newOrderMiddlewareJsfService = (com.jd.jdorders.component.export.OrderMiddlewareCBDExport) SpringHelper
                .getBean("newOrderMiddlewareJsfService");

        // 请求对象
        OrderQueryRequest request = new OrderQueryRequest();
        // 通用业务参数
        ComponentBase componentBase = new ComponentBase();
        componentBase.setRegion("301");
        // 必传字段，测试环境或者物理机部署的应用无法自动获取appname，可手动传入，默认取jvm的启动参数deploy.app.name
        componentBase.setAppName(Constants.UMP_APP_NAME_DMSWEB);
        List<String> querys = OrderServiceHelper.getQueryKeys();
        // 赋值
        request.setComponentBase(componentBase);
        request.setBizType("1");
        request.setOrderId(111111111111111L);
        request.setQueryKeys(querys);

        OrderQueryResponse response = newOrderMiddlewareJsfService.getOrderData(request);
        System.out.println(response);
    }
}
    
