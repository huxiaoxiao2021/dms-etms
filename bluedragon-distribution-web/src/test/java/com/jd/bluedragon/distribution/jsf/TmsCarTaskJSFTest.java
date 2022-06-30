package com.jd.bluedragon.distribution.jsf;


import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.external.gateway.service.TMSCarTaskGateWayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/distribution-web-context.xml" })
public class TmsCarTaskJSFTest {

    @Autowired
    private TMSCarTaskGateWayService tmsCarTaskGateWayService;

    @Test
    public void getEndNodeListTest(){

        JdCResponse<List<CarTaskEndNodeResponse>> endNodeList = tmsCarTaskGateWayService.getEndNodeList("10098");

        System.out.println(JSON.toJSON(endNodeList));
    }
}
