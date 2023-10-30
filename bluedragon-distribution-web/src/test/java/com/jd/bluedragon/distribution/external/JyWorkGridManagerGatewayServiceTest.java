package com.jd.bluedragon.distribution.external;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerQueryRequest;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerTransferData;
import com.jd.bluedragon.distribution.jy.gateway.work.JyWorkGridManagerGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
@Slf4j
public class JyWorkGridManagerGatewayServiceTest {

    @Autowired
    JyWorkGridManagerGatewayService jyWorkGridManagerGatewayService;

    @Test
    public void queryCandidateListTest() {
        JyWorkGridManagerQueryRequest req = new JyWorkGridManagerQueryRequest();
        req.setPositionCode("GW00010002");

        System.out.println("测试请求:"+JSON.toJSONString(req));
        JdCResponse<List<String>> listJdCResponse = jyWorkGridManagerGatewayService.queryCandidateList(req);
        System.out.println("测试响应:"+JSON.toJSONString(listJdCResponse));

    }

    @Test
    public void transferCandidateTest() {
        JyWorkGridManagerTransferData req = new JyWorkGridManagerTransferData();
        req.setBizId("a83a3fe9-8c3f-43cc-ae1d-91ad02a39311");
        req.setErp("wuyoude");

        System.out.println("测试请求:"+JSON.toJSONString(req));
        JdCResponse<Boolean> booleanJdCResponse = jyWorkGridManagerGatewayService.transferCandidate(req);
        System.out.println("测试响应:"+JSON.toJSONString(booleanJdCResponse));

    }
}
