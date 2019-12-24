package com.jd.bluedragon.distribution.rest.transport;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.transport.domain.ArWaitReceive;
import com.jd.bluedragon.distribution.transport.domain.ArWaitReceiveRequest;
import com.jd.ql.dms.common.domain.ListResponse;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xumei3 on 2017/12/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class ArReceiveResourceTest {
    private static final Logger log = LoggerFactory.getLogger(ArReceiveResourceTest.class);

    @Resource
    private ArReceiveResource rest;

    @BeforeClass
    public static void initClass(){

    }

    @Test
    public void getARWaitReceiveTest(){
        List<ArWaitReceiveRequest> requests = new ArrayList<ArWaitReceiveRequest>();
        ArWaitReceiveRequest request1 = new ArWaitReceiveRequest();
        ArWaitReceiveRequest request2 = new ArWaitReceiveRequest();
        request2.setUserCode(10053);
        request2.setUserName("邢松");
        ArWaitReceiveRequest request3 = new ArWaitReceiveRequest();
        request3.setUserCode(10053);
        request3.setUserName("邢松");
        request3.setSiteCode(910);

        ArWaitReceiveRequest request4 = new ArWaitReceiveRequest();
        request4.setUserCode(10053);
        request4.setUserName("邢松");
        request4.setSiteCode(910);
        request4.setStartCityId(-1);

        ArWaitReceiveRequest request5 = new ArWaitReceiveRequest();
        request5.setUserCode(10053);
        request5.setUserName("邢松");
        request5.setSiteCode(910);
        request5.setStartCityId(1);

        ArWaitReceiveRequest request6 = new ArWaitReceiveRequest();
        request6.setUserCode(10053);
        request6.setUserName("邢松");
        request6.setSiteCode(910);
        request6.setStartCityId(1);
        request6.setOrderNo("1111111");

        ArWaitReceiveRequest request7 = new ArWaitReceiveRequest();
        request7.setUserCode(10053);
        request7.setUserName("邢松");
        request7.setSiteCode(9);
        request7.setStartCityId(1);
        request7.setTransportName("111132342");

        ArWaitReceiveRequest request8 = new ArWaitReceiveRequest();
        request8.setUserCode(10053);
        request8.setUserName("邢松");
        request8.setSiteCode(910);
        request8.setStartCityId(1);
        request8.setOrderNo("1111111");
        request8.setTransportName("111132342");

        ArWaitReceiveRequest request9 = new ArWaitReceiveRequest();
        request9.setUserCode(10053);
        request9.setUserName("邢松");
        request9.setSiteCode(910);
        request9.setStartCityId(1);
        request9.setOrderNo("1111111");
        request9.setTransportName("ABVCDSDFA");

//        requests.add(request1);
//        requests.add(request2);
//        requests.add(request3);
        requests.add(request4);
        requests.add(request5);
        requests.add(request6);
        requests.add(request7);
        requests.add(request8);
        requests.add(request9);

        for(ArWaitReceiveRequest request : requests){
            if(log.isInfoEnabled()){
                log.info("【待提货查询接口测试参数】：{}", JsonHelper.toJson(request));
            }
            ListResponse<ArWaitReceive> result= rest.getARWaitReceive(request);
            if(log.isInfoEnabled()) {
                log.info("【待提货查询接口测试结果】:{}", JsonHelper.toJson(result));
            }
        }
    }
}
