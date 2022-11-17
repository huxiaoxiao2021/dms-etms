package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.CrossDataReq;
import com.jd.bluedragon.common.dto.comboard.request.TableTrolleyReq;
import com.jd.bluedragon.common.dto.comboard.response.CrossDataResp;
import com.jd.bluedragon.common.dto.comboard.response.TableTrolleyResp;
import com.jd.bluedragon.external.gateway.service.JyComboardGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author liwenji
 * @date 2022-11-16 17:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyComboardGatewayServiceImplTest {
    
    @Autowired
    private JyComboardGatewayService jyComboardGatewayService;
    
    @Test
    public void listCrossDataTest() {
        CrossDataReq crossDataReq = new CrossDataReq();
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        crossDataReq.setPageNo(1);
        crossDataReq.setPageSize(4);
        crossDataReq.setCurrentOperate(currentOperate);
        JdCResponse<CrossDataResp> crossDataRespJdCResponse = jyComboardGatewayService.listCrossData(crossDataReq);
        System.out.println(JsonHelper.toJson(crossDataRespJdCResponse));
    }
    
    @Test
    public void listTableTrolleyUnderCrossTest() {
        TableTrolleyReq query = new TableTrolleyReq();
        query.setPageSize(4);
        query.setPageNo(2);
        query.setCrossCode("2002");
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        query.setCurrentOperate(currentOperate);
        JdCResponse<TableTrolleyResp> re = jyComboardGatewayService.listTableTrolleyUnderCross(query);
        System.out.println("根据滑道获取结果：" + JsonHelper.toJson(re));
        TableTrolleyReq tableTrolleyReq = new TableTrolleyReq();
        tableTrolleyReq.setPageSize(100);
        tableTrolleyReq.setPageNo(1);
        CurrentOperate operate = new CurrentOperate();
        operate.setSiteCode(910);
        tableTrolleyReq.setCurrentOperate(operate);
        JdCResponse<TableTrolleyResp> response = jyComboardGatewayService.listTableTrolleyUnderCross(tableTrolleyReq);
        System.out.println("根据场地获取结果：" + JsonHelper.toJson(response));
    }
}
