package com.jd.bluedragon.distribution.external;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerPageData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerQueryRequest;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerTransferData;
import com.jd.bluedragon.common.dto.work.ScanTaskPositionRequest;
import com.jd.bluedragon.distribution.jy.gateway.work.JyWorkGridManagerGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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
        req.setPositionCode("GW00019001");

        System.out.println("测试请求:"+JSON.toJSONString(req));
        JdCResponse<List<String>> listJdCResponse = jyWorkGridManagerGatewayService.queryCandidateList(req);
        System.out.println("测试响应:"+JSON.toJSONString(listJdCResponse));

    }

    @Test
    public void queryDataListTest() {
        JyWorkGridManagerQueryRequest req = new JyWorkGridManagerQueryRequest();
        req.setOperateUserCode("bjxings");
        req.setStatus(99);
        req.setLimit(10);
        req.setPageNumber(1);
        req.setPageSize(10);
        req.setOffset(0);

        System.out.println("测试请求:"+JSON.toJSONString(req));
        JdCResponse<JyWorkGridManagerPageData> jyWorkGridManagerPageDataJdCResponse = jyWorkGridManagerGatewayService.queryDataList(req);
        System.out.println("测试响应:"+JSON.toJSONString(jyWorkGridManagerPageDataJdCResponse));

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

    @Test
    public void createSelfCheckTask() {
        try {
            ScanTaskPositionRequest request = new ScanTaskPositionRequest();
            request.setScanPositionCode("GW00015004");
            User user = new User();
            user.setUserErp("bjxings");
            user.setUserName("邢松");
            request.setUser(user);
            CurrentOperate currentOperate = new CurrentOperate();
            currentOperate.setSiteCode(910);
            currentOperate.setSiteName("北京马驹桥分拣中心");
            request.setCurrentOperate(currentOperate);

            JdCResponse<String> selfCheckTask = jyWorkGridManagerGatewayService.createSelfCheckTask(request);

            String bizId = selfCheckTask.getData();

            System.out.println(bizId);

        }catch (Exception e) {
            log.error("服务异常!", e);
            Assert.fail();
        }
    }
    
}
