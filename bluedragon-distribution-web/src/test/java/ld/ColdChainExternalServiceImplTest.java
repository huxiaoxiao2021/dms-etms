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
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.JyUnloadVehicleGatewayService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class ColdChainExternalServiceImplTest {


    @Autowired
    private ColdChainExternalServiceImpl coldChainExternalService;

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
        SendInspectionVO sendInspectionReq = new SendInspectionVO();
        sendInspectionReq.setSendCode("910-364605-20220825188454650");
        sendInspectionReq.setBoxCode("JDVE00004325207-1-1-");
        sendInspectionReq.setCreateSiteCode(910);
        sendInspectionReq.setReceiveSiteCode(364605);
        sendInspectionReq.setOperateTime(new Date(System.currentTimeMillis()));
        sendInspectionReq.setCreateUserCode(17331);
        InvokeResult<Boolean> result = coldChainExternalService.sendAndInspectionOfPack(sendInspectionReq);
        Assert.assertTrue(result.codeSuccess());
    }
}