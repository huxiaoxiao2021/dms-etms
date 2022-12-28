package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.BoardQueryReq;
import com.jd.bluedragon.common.dto.comboard.request.QueryBelongBoardReq;
import com.jd.bluedragon.common.dto.comboard.response.BoardQueryResp;
import com.jd.bluedragon.common.dto.comboard.response.QueryBelongBoardResp;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail;
import com.jd.bluedragon.common.dto.seal.request.CheckTransportReq;
import com.jd.bluedragon.common.dto.seal.request.SealCodeReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
import com.jd.bluedragon.external.gateway.service.JyComboardSealGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.SealCarDto;
import org.apache.avro.data.Json;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liwenji
 * @date 2022-12-23 10:07
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyComboardSealGatewayServiceImplTest {
    
    @Autowired
    private JyComboardSealGatewayService jyComboardSealGatewayService;
    
    @Autowired
    private JySealVehicleService jySealVehicleService;
    
    @Test
    public void listComboardBySendFlowTest() {
        BoardQueryReq boardQueryReq = new BoardQueryReq();
        boardQueryReq.setEndSiteId(39);
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        boardQueryReq.setCurrentOperate(currentOperate);
        JdCResponse<BoardQueryResp> jdCResponse = jyComboardSealGatewayService.listComboardBySendFlow(boardQueryReq);
        System.out.println(JsonHelper.toJson(jdCResponse));
    }
    
    @Test
    public void queryBelongBoardByBarCodeTest() {
        QueryBelongBoardReq resp = new QueryBelongBoardReq();
        CurrentOperate operate = new CurrentOperate();
        resp.setGroupCode("G00000047004");
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        resp.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        resp.setUser(user);
        resp.setBarCode("JDV000707553583-2-5-");
        JdCResponse<QueryBelongBoardResp> re = jyComboardSealGatewayService.queryBelongBoardByBarCode(resp);
        System.out.println(JsonHelper.toJson(re));
    }
    
    @Test
    public void listSealCodeByBizIdTest() {
        SealCodeReq sealCodeReq = new SealCodeReq();
        sealCodeReq.setSendVehicleBizId("SST22092000000094");
        JdCResponse<SealCodeResp> jdCResponse = jyComboardSealGatewayService.listSealCodeByBizId(sealCodeReq);
        System.out.println(JsonHelper.toJson(jdCResponse));
    }
    
    @Test
    public void checkTransCodeTest() {
        CheckTransportReq checkTransportReq = new CheckTransportReq();
        checkTransportReq.setTransportCode("T220303001517");
        checkTransportReq.setEndSiteId(31);
        JdCResponse jdCResponse = jyComboardSealGatewayService.checkTransCode(checkTransportReq);
        System.out.println(JsonHelper.toJson(jdCResponse));
    }
    
    @Test
    public void sendDestDetailTest() {
        SendDetailRequest sendDetailRequest = new SendDetailRequest();
        sendDetailRequest.setSendVehicleBizId("SST22081600000067");
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        sendDetailRequest.setCurrentOperate(currentOperate);
        JdCResponse<List<SendDestDetail>> listJdCResponse = jyComboardSealGatewayService.sendDestDetail(sendDetailRequest);
        System.out.println(JsonHelper.toJson(listJdCResponse));
    }
    
    @Test
    public void saveSealVehicleTest() {
        SealVehicleReq sealVehicleReq = new SealVehicleReq();
        sealVehicleReq.setSendVehicleBizId("SST22102700000890");
        sealVehicleReq.setSendVehicleDetailBizId("TW22102400827098-004");
        sealVehicleReq.setTransportCode("T220311001098");
        sealVehicleReq.setWeight(10.99);
        sealVehicleReq.setVolume(9.99);
        List<String> batchCodes = new ArrayList<>();
        batchCodes.add("910-38-20221205232254844");
        sealVehicleReq.setScannedBatchCodes(batchCodes);
        List<String> sealCodes = new ArrayList<>();
        sealCodes.add("F098765098");
        sealVehicleReq.setScannedSealCodes(sealCodes);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        sealVehicleReq.setUser(user);
        jyComboardSealGatewayService.saveSealVehicle(sealVehicleReq );
    }
    
    @Test
    public void getSealVehicleInfoTest() {
        SealVehicleInfoReq sealVehicleReq = new SealVehicleInfoReq();
        sealVehicleReq.setSendVehicleBizId("SST22102700000890");
        sealVehicleReq.setSendVehicleDetailBizId("TW22102400827098-004");
        JdCResponse<SealVehicleInfoResp> sealVehicleInfo = jyComboardSealGatewayService.getSealVehicleInfo(sealVehicleReq);
        System.out.println(JsonHelper.toJson(sealVehicleInfo));
    }
    
    @Test
    public void cancelSealCarTest() {
        SealCarDto sealCarDto = new SealCarDto();
        List<String> batchCodes = new ArrayList<>();
        batchCodes.add("910-39-20221205212254643");
        batchCodes.add("910-39-20221205212254654");
        sealCarDto.setBatchCodes(batchCodes);
        sealCarDto.setTransWorkItemCode("TW22121900853385-001");
        InvokeResult<Boolean> result = jySealVehicleService.cancelSealCar(sealCarDto, "liwenji3", "李文吉");
        System.out.println(JsonHelper.toJson(result));
    }
}
