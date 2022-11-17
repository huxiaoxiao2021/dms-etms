package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.CreateGroupCTTReq;
import com.jd.bluedragon.common.dto.comboard.request.CrossDataReq;
import com.jd.bluedragon.common.dto.comboard.request.TableTrolleyReq;
import com.jd.bluedragon.common.dto.comboard.response.CreateGroupCTTResp;
import com.jd.bluedragon.common.dto.comboard.response.CrossDataResp;
import com.jd.bluedragon.common.dto.comboard.response.TableTrolleyDto;
import com.jd.bluedragon.common.dto.comboard.response.TableTrolleyResp;
import com.jd.bluedragon.external.gateway.service.JyComboardGatewayService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

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
    
    @Test
    public void getDefaultGroupCTTNameTest() {
        JdCResponse<CreateGroupCTTResp> response = jyComboardGatewayService.getDefaultGroupCTTName();
        System.out.println(JsonHelper.toJson(response));
    }
    
    @Test
    public void createGroupCTTDataTest() {
        CreateGroupCTTReq resp = new CreateGroupCTTReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("group111");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setTemplateName("混扫01");
        List<TableTrolleyDto> dtos = new ArrayList<>();
        resp.setTableTrolleyDtoList(dtos);
        TableTrolleyDto tableTrolleyDto = new TableTrolleyDto();
        tableTrolleyDto.setEndSiteId(630109);
        tableTrolleyDto.setCrossCode("2002");
        tableTrolleyDto.setEndSiteName("北京海淀区言语站点001");
        tableTrolleyDto.setTableTrolleyCode("106");
        dtos.add(tableTrolleyDto);
        TableTrolleyDto tableTrolleyDto1 = new TableTrolleyDto();
        tableTrolleyDto1.setEndSiteId(630171);
        tableTrolleyDto1.setCrossCode("2002");
        tableTrolleyDto1.setEndSiteName("京东便民站-测试wyy");
        tableTrolleyDto1.setTableTrolleyCode("11-笼车");
        dtos.add(tableTrolleyDto1);
        TableTrolleyDto tableTrolleyDto2 = new TableTrolleyDto();
        tableTrolleyDto2.setEndSiteId(11398);
        tableTrolleyDto2.setCrossCode("89");
        tableTrolleyDto2.setEndSiteName("1栋15秦希深专用");
        tableTrolleyDto2.setTableTrolleyCode("106");
        dtos.add(tableTrolleyDto2);
        jyComboardGatewayService.createGroupCTTData(resp);
    }
}
