package ld;

import com.jd.bluedragon.distribution.coldChain.domain.*;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainExternalServiceImpl;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

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
}