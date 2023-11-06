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
        req.setBizId("41d86be0-4784-4cad-b506-e0cdb1bc08d7");
        req.setErp("wuyoude");

        System.out.println("测试请求:"+JSON.toJSONString(req));
        JdCResponse<Boolean> booleanJdCResponse = jyWorkGridManagerGatewayService.transferCandidate(req);
        System.out.println("测试响应:"+JSON.toJSONString(booleanJdCResponse));

    }
}
