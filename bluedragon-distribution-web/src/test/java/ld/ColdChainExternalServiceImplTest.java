package ld;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadVehicleTaskRequest;
import com.jd.bluedragon.distribution.coldChain.domain.*;
import com.jd.bluedragon.distribution.coldChain.service.IColdChainService;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainExternalServiceImpl;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.JyUnloadVehicleGatewayService;
import com.jd.fastjson.JSON;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.jd.bluedragon.distribution.task.domain.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class ColdChainExternalServiceImplTest {


    @Autowired
    private ColdChainExternalServiceImpl coldChainExternalService;

    @Autowired
    private DeliveryService deliveryService;

    @Test
    public void inspectionCheck() {
        InspectionCheckVO req = new InspectionCheckVO();
        req.setBarCode("JD0003358207124");
        req.setOperateSiteCode(910);
        InvokeResult<InspectionCheckResult> result =  coldChainExternalService.inspectionCheck(req);

        Assert.assertTrue(result.getCode() == InvokeResult.RESULT_SUCCESS_CODE);
    }

    @Test
    public void inspection() {

        InspectionVO req = new InspectionVO();
        req.setBarCodes(Arrays.asList("JD0003358207124"));
        req.setSiteCode(910);
        req.setSiteName("马驹桥测试");
        req.setUserCode(1);
        req.setUserName("刘铎");
        req.setOperateTime("2021-05-10 06:06:06");
        InvokeResult<Boolean> result =  coldChainExternalService.inspection(req);

        Assert.assertTrue(result.getCode() == InvokeResult.RESULT_SUCCESS_CODE);
    }

    @Test
    public void sendCheck() {

        SendCheckVO req = new SendCheckVO();
        req.setReceiveSiteCode(39);
        req.setBoxCode("JDV000690915751");
        req.setSiteCode(14336);
        req.setUserCode(17907);
        req.setUserName("冷链测试人");
        req.setOperateTime("2021-05-10 06:06:06");
        InvokeResult<ColdCheckCommonResult> result =  coldChainExternalService.sendCheck(req);

        Assert.assertTrue(result.getCode() == InvokeResult.RESULT_SUCCESS_CODE);
    }

    @Test
    public void send() {

        SendVO req = new SendVO();
        req.setTransPlanCode("liuduotest1");
        req.setReceiveSiteCode(39);
        req.setBarCodes(Arrays.asList("JDV000690934291"));
        req.setSiteCode(14336);
        req.setUserCode(17907);
        req.setUserName("冷链测试人");
        req.setOperateTime("2021-05-12 09:06:06");
        req.setNeedCheck(false);
        InvokeResult<Boolean> result =  coldChainExternalService.send(req);

        Assert.assertTrue(result.getCode() == InvokeResult.RESULT_SUCCESS_CODE);
    }

    @Test
    public void sendOfKYCheck() {

        SendOfKYCheckVO req = new SendOfKYCheckVO();
        req.setReceiveSiteCode(39);
        req.setBoxCode("JDV000690915751");
        req.setSiteCode(14336);
        req.setUserCode(17907);
        req.setUserName("冷链测试人");
        req.setOperateTime("2021-05-10 06:06:06");
        InvokeResult<ColdCheckCommonResult> result =  coldChainExternalService.sendOfKYCheck(req);

        Assert.assertTrue(result.getCode() == InvokeResult.RESULT_SUCCESS_CODE);
    }

    @Test
    public void sendOfKY() {

        SendOfKYVO req = new SendOfKYVO();
        req.setReceiveSiteCode(39);
        req.setSendCode("14336-39-20210301164876268");
        req.setBarCodes(Arrays.asList("JDV000690934291"));
        req.setSiteCode(14336);
        req.setUserCode(17907);
        req.setUserName("冷链测试人");
        req.setOperateTime("2021-05-11 07:07:07");
        req.setNeedCheck(false);
        InvokeResult<Boolean> result =  coldChainExternalService.sendOfKY(req);

        Assert.assertTrue(result.getCode() == InvokeResult.RESULT_SUCCESS_CODE);
    }


    @Autowired
    private IJyUnloadVehicleService unloadVehicleService;

    @Test
    public void fetchUnloadTaskTest() {
        UnloadVehicleTaskRequest request = new UnloadVehicleTaskRequest();
        request.setPageNumber(1);
        request.setPageSize(230);
        request.setEndSiteCode(910);
        request.setVehicleStatus(3);
//        request.setBarCode();
//        request.setLineType();
//        request.setFetchType();

        unloadVehicleService.fetchUnloadTask(request);
    }

    private static User user = new User();
    private static CurrentOperate currentOperate = new CurrentOperate();

    static {

        user.setUserCode(17907);
        user.setUserName("邢松");
        user.setUserErp("bjxings");

        currentOperate.setOrgId(1);
        currentOperate.setSiteCode(910);
        currentOperate.setSiteName("北京马驹桥");

    }

    @Autowired
    private JyUnloadVehicleGatewayService unloadVehicleGatewayService;

    @Test
    public void unloadScanTest() {
        UnloadScanRequest request = new UnloadScanRequest();
        request.setUser(user);
        request.setCurrentOperate(currentOperate);
        request.setBarCode("JDVF00001740486");
        request.setTaskId("220411200000001");
        request.setBizId("SC00002");
        request.setSealCarCode("SC00002");
        request.setForceSubmit(false);
        request.setGroupCode("G00000008001");

        unloadVehicleGatewayService.unloadScan(request);

    }

    @Test
    public void sendAndInspection() {
        List<String> waybillList = new ArrayList<>();
        waybillList.add("JDVC00008264211-1-3-");
        waybillList.add("JDVC00008264211-2-3-");
        waybillList.add("JDVC00008264211-3-3-");

        for (int i = 0; i < 10; i++) {
            for (String waybillCode : waybillList) {
                SendInspectionVO sendInspectionReq = new SendInspectionVO();
                sendInspectionReq.setSendCode("14514-910-20220915219354635");
                sendInspectionReq.setBoxCode(waybillCode);
                sendInspectionReq.setCreateSiteCode(14514);
                sendInspectionReq.setReceiveSiteCode(910);
                sendInspectionReq.setOperateTime(new Date(System.currentTimeMillis()));
                sendInspectionReq.setCreateUserCode(17331);
                sendInspectionReq.setCreateUser("吴有德");
                InvokeResult<Boolean> result = coldChainExternalService.sendAndInspectionOfPack(sendInspectionReq);
                // Assert.assertTrue(result.codeSuccess());
            }
            System.out.println("断点等待");
        }
    }

    @Test
    public void updatewaybillCodeMessage() {
        for (int i = 0; i < 10; i++) {
            Task task = JSON.parseObject("{\n" +
                    "  \"createSiteCode\" : 2860,\n" +
                    "  \"executeTime\" : 1663232510573,\n" +
                    "  \"type\" : 1300,\n" +
                    "  \"keyword1\" : \"1\",\n" +
                    "  \"keyword2\" : \"10\",\n" +
                    "  \"body\" : \"{\\n  \\\"sendMId\\\" : 1570337082428809216,\\n  \\\"sendCode\\\" : \\\"2860-910-20220915179354242\\\",\\n  \\\"boxCode\\\" : \\\"JDVC00008281608-2-3-\\\",\\n  \\\"createSiteCode\\\" : 2860,\\n  \\\"receiveSiteCode\\\" : 910,\\n  \\\"sendType\\\" : 10,\\n  \\\"createUser\\\" : \\\"youhua1\\\",\\n  \\\"createUserCode\\\" : -1,\\n  \\\"operateTime\\\" : 1663232514830,\\n  \\\"bizSource\\\" : 26,\\n  \\\"handleCategory\\\" : 4\\n}\",\n" +
                    "  \"tableName\" : \"task_send\",\n" +
                    "  \"sequenceName\" : \"SEQ_TASK_SORTING\",\n" +
                    "  \"boxCode\" : \"JDVC00008281608-2-3-\",\n" +
                    "  \"receiveSiteCode\" : 910,\n" +
                    "  \"fingerprint\" : \"F84389E6875C5E8CDF09F6D383F02D63\",\n" +
                    "  \"ownSign\" : \"DMS\",\n" +
                    "  \"subType\" : 130004\n" +
                    "}",Task.class);

            try {
                deliveryService.updatewaybillCodeMessage(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void inspectionOfColdNewTest() {
        ColdInspectionVo request = new ColdInspectionVo();
        request.setBarCode("JDVC00005517150");
        request.setOperateTime("2022-10-08 06:06:06");
        request.setSiteCode(14514);
        request.setSiteName("转运");
        request.setUserCode(17907);
        request.setUserName("冷链测试人");
        coldChainExternalService.inspectionOfColdNew(request);

    }

    @Test
    public void sendOfColdBusinessNewTest() {
        ColdSendVo request = new ColdSendVo();
        request.setBoxCode("JDVC00000340396");
        request.setForceSend( false);
        request.setOperateTime("2022-10-09 12:06:06");
        request.setReceiveSiteCode(39);
        request.setSendCode("14514-39-20220829148493930");
        request.setSiteCode(14514);
        request.setSiteName("转运");
        request.setTransPlanCode("TP19041300104683");
        request.setUserCode(17907);
        request.setUserName("冷链测试人");

        coldChainExternalService.sendOfColdBusinessNew(request);

    }

    @Test
    public void sendOfColdKYNewTest() {
        ColdSendVo request = new ColdSendVo();
        request.setBoxCode("JDVC00004937970-1-22-");
        request.setForceSend( false);
        request.setOperateTime("2022-10-09 12:06:06");
        request.setReceiveSiteCode(39);
        request.setSendCode("14514-39-20220829148493930");
        request.setSiteCode(14514);
        request.setSiteName("转运");
        request.setTransPlanCode("TP19041300104683");
        request.setUserCode(17907);
        request.setUserName("冷链测试人");

        coldChainExternalService.sendOfColdKYNew(request);

    }
}