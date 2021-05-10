package ld;

import com.jd.bluedragon.distribution.coldChain.domain.InspectionCheckResult;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionCheckVO;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionVO;
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
        InvokeResult<Boolean> result =  coldChainExternalService.inspection(req);

        Assert.assertTrue(result.getCode() == InvokeResult.RESULT_SUCCESS_CODE);
    }

    @Test
    public void sendCheck() {
    }

    @Test
    public void send() {
    }

    @Test
    public void sendOfKYCheck() {
    }

    @Test
    public void sendOfKY() {
    }
}