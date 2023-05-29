package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.*;
import com.jd.bluedragon.external.gateway.service.JyWarehouseSendGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.avro.data.Json;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liwenji
 * @description 
 * @date 2023-05-19 11:07
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyWarehouseSendGatewayServiceTest {
    
    @Autowired
    private JyWarehouseSendGatewayService jyWarehouseSendGatewayService;
    
    private static final CurrentOperate CURRENT_OPERATE = new CurrentOperate(910,"马驹桥分拣中心",new Date());
    private static final User USER = new User(111,"李文吉");
    static {
        USER.setUserErp("liwenji3");
    }
    
    @Test
    public void getMixScanTaskDefaultNameTest() {
        MixScanTaskDefaultNameQueryReq req = new MixScanTaskDefaultNameQueryReq();
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setUser(USER);
        JdCResponse<String> response = jyWarehouseSendGatewayService.getMixScanTaskDefaultName(req);
        System.out.println(JsonHelper.toJson(response));
    }
    
    @Test
    public void createMixScanTaskTest() {
        CreateMixScanTaskReq createMixScanTaskReq = new CreateMixScanTaskReq();
        createMixScanTaskReq.setTemplateName("接货仓混扫任务01");
        createMixScanTaskReq.setCurrentOperate(CURRENT_OPERATE);
        createMixScanTaskReq.setUser(USER);
        createMixScanTaskReq.setGroupCode("G00000059567");
        List<MixScanTaskDetailDto> list = new ArrayList<>();
        MixScanTaskDetailDto mixScanTaskDetailDto = new MixScanTaskDetailDto();
        mixScanTaskDetailDto.setEndSiteId(30L);
        mixScanTaskDetailDto.setEndSiteName("目的站点");
        mixScanTaskDetailDto.setSendVehicleDetailBizId("SST22070100000004");
        mixScanTaskDetailDto.setCrossCode("567");
        mixScanTaskDetailDto.setTabletrolleyCode("gyeydge");
        MixScanTaskDetailDto mixScanTaskDetailDto2 = new MixScanTaskDetailDto();
        mixScanTaskDetailDto2.setEndSiteId(33L);
        mixScanTaskDetailDto2.setEndSiteName("目的站点2");
        mixScanTaskDetailDto2.setSendVehicleDetailBizId("SST22070100000344");
        mixScanTaskDetailDto2.setCrossCode("324");
        mixScanTaskDetailDto2.setTabletrolleyCode("rfbrf");
        list.add(mixScanTaskDetailDto);
        list.add(mixScanTaskDetailDto2);
        createMixScanTaskReq.setSendFlowList(list);
        jyWarehouseSendGatewayService.createMixScanTask(createMixScanTaskReq);
    }
    
    @Test
    public void appendMixScanTaskFlowTest() {
        AppendMixScanTaskFlowReq req = new AppendMixScanTaskFlowReq();
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setTemplateCode("CTT23051900000004");
        req.setTemplateName("接货仓混扫任务01");
        req.setGroupCode("G00000059567");
        List<MixScanTaskDetailDto> sendFLow = new ArrayList<>();
        req.setSendFlowList(sendFLow);
        MixScanTaskDetailDto detailDto = new MixScanTaskDetailDto();
        detailDto.setEndSiteId(23L);
        detailDto.setEndSiteName("目的站点2");
        detailDto.setSendVehicleDetailBizId("SST22070100000344");
        detailDto.setCrossCode("324");
        detailDto.setTabletrolleyCode("rfbrf");
        sendFLow.add(detailDto);
        System.out.println(JsonHelper.toJson(jyWarehouseSendGatewayService.appendMixScanTaskFlow(req)));
    }
    
    @Test
    public void deleteMixScanTaskTest() {
        DeleteMixScanTaskReq req = new DeleteMixScanTaskReq();
        req.setTemplateCode("CTT23051900000004");
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setGroupCode("G00000059567");
        JdCResponse<Void> response = jyWarehouseSendGatewayService.deleteMixScanTask(req);
        System.out.println(JsonHelper.toJson(response));
    }
    
    @Test
    public void removeMixScanTaskFlowTest() {
        RemoveMixScanTaskFlowReq req = new RemoveMixScanTaskFlowReq();
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setGroupCode("G00000059567");
        req.setTemplateCode("CTT23051900000004");
        req.setEndSiteId(33);
        req.setSendVehicleDetailBizId("SST22070100000344");
        jyWarehouseSendGatewayService.removeMixScanTaskFlow(req);
    }
    
    @Test
    public void mixScanTaskFocusTest() {
        MixScanTaskFocusReq req = new MixScanTaskFocusReq();
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setGroupCode("G00000059567");
        req.setTemplateCode("CTT23051900000004");
        req.setEndSiteId(33);
        req.setSendVehicleDetailBizId("SST22070100000344");
        req.setFocus(0);
        JdCResponse<Void> response = jyWarehouseSendGatewayService.mixScanTaskFocus(req);
        System.out.println(JsonHelper.toJson(response));
    }
    
    @Test
    public void getMixScanTaskListPageTest() {
        MixScanTaskListQueryReq req = new MixScanTaskListQueryReq();
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setUser(USER);
        req.setGroupCode("G00000059567");
        req.setPageNo(1);
        req.setPageSize(10);
        JdCResponse<MixScanTaskQueryRes> mixScanTaskListPage = jyWarehouseSendGatewayService.getMixScanTaskListPage(req);
        System.out.println(JsonHelper.toJson(mixScanTaskListPage));
    }
    
    @Test
    public void getMixScanTaskFlowDetailListTest() {
        MixScanTaskFlowDetailReq req = new MixScanTaskFlowDetailReq();
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setGroupCode("G00000059567");
        req.setTemplateCode("CTT23051900000004");
        JdCResponse<MixScanTaskFlowDetailRes> l = jyWarehouseSendGatewayService.getMixScanTaskFlowDetailList(req);
        System.out.println(JsonHelper.toJson(l));
    }
    
    @Test
    public void selectMixScanTaskSealDestTest() {
        SelectMixScanTaskSealDestReq req = new SelectMixScanTaskSealDestReq();
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setGroupCode("G00000059567");
        req.setTemplateCode("CTT23051900000004");
        JdCResponse<MixScanTaskToSealDestAgg> re = jyWarehouseSendGatewayService.selectMixScanTaskSealDest(req);
        System.out.println(JsonHelper.toJson(re));
    }
}
